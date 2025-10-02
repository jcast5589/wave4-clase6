package com.expose.data.app_expose_queue_solace.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

public class DocumentProcessor {

    private static final int MAX_STRING_LEN = 4000; // ej. para evitar payloads enormes
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "pass", "secret", "token", "apikey", "api_key", "authorization", "auth"
    );

    private final ObjectMapper mapper = new ObjectMapper();

    /** Punto de entrada único del “pipeline” de negocio. */
    public Documento process(Documento in, String source) {
        Documento doc = ensureIds(in);
        Map<String, Object> normalized = normalizeKeys(doc.getData());
        normalized = deepSanitize(normalized);
        normalized = enforceStringLimit(normalized, MAX_STRING_LEN);

        Map<String, Object> meta = (doc.getMetadata() == null) ? new LinkedHashMap<>() : new LinkedHashMap<>(doc.getMetadata());
        meta.put("processedAt", Instant.now().toString());
        if (source != null && !source.isBlank()) {
            meta.put("source", source);
        }
        meta.put("schemaVersion", "v1");
        meta.put("dataHash", Objects.hashCode(normalized));

        doc.setData(normalized);
        doc.setMetadata(meta);
        // si todo ok, marcamos como PROCESADO (o déjalo NUEVO si prefieres que downstream cambie el estado)
        doc.setEstado(Documento.Estado.PROCESADO);
        return doc;
    }

    /** Serialización a JSON (útil para logs/debug o para brokers que requieren texto). */
    public String toJson(Documento doc) {
        try {
            return mapper.writeValueAsString(doc);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"serialization-failed\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    // ---------- Helpers del pipeline ----------

    private Documento ensureIds(Documento in) {
        Documento out = in;
        if (out.getUniqueId() == null || out.getUniqueId().isBlank()) {
            out.setUniqueId(UUID.randomUUID().toString());
        }
        if (out.getTimestamp() == null) {
            out.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
        }
        return out;
    }

    /** Normaliza llaves a lowerCamelCase simple (puedes cambiar a snake_case si lo prefieres). */
    @SuppressWarnings("unchecked")
    private Map<String, Object> normalizeKeys(Map<String, Object> data) {
        if (data == null) return new LinkedHashMap<>();
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : data.entrySet()) {
            String nk = toLowerCamel(e.getKey());
            Object v = e.getValue();
            if (v instanceof Map<?, ?> m) {
                result.put(nk, normalizeKeys((Map<String, Object>) m));
            } else if (v instanceof List<?> list) {
                result.put(nk, normalizeListKeys(list));
            } else {
                result.put(nk, v);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Object> normalizeListKeys(List<?> list) {
        List<Object> out = new ArrayList<>(list.size());
        for (Object v : list) {
            if (v instanceof Map<?, ?> m) {
                out.add(normalizeKeys((Map<String, Object>) m));
            } else if (v instanceof List<?> l) {
                out.add(normalizeListKeys(l));
            } else {
                out.add(v);
            }
        }
        return out;
    }

    private String toLowerCamel(String key) {
        if (key == null) return "";
        String k = key.trim()
                .replaceAll("[^A-Za-z0-9_\\- ]", " ") // limpia símbolos raros
                .replace('-', ' ')
                .replace('_', ' ');
        String[] parts = k.split("\\s+");
        if (parts.length == 0) return "";
        StringBuilder sb = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            String p = parts[i].toLowerCase();
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    /** Limpieza profunda: trim de Strings, remueve nulos/empties, redacta sensibles. */
    @SuppressWarnings("unchecked")
    private Map<String, Object> deepSanitize(Map<String, Object> data) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (data == null) return out;

        for (Map.Entry<String, Object> e : data.entrySet()) {
            String key = e.getKey();
            Object val = e.getValue();

            if (val == null) continue;

            if (val instanceof String s) {
                String trimmed = s.trim();
                if (trimmed.isEmpty()) continue;
                out.put(key, redactIfSensitive(key, trimmed));
            } else if (val instanceof Map<?, ?> m) {
                Map<String, Object> child = deepSanitize((Map<String, Object>) m);
                if (!child.isEmpty()) out.put(key, child);
            } else if (val instanceof List<?> list) {
                List<Object> clean = new ArrayList<>();
                for (Object item : list) {
                    if (item == null) continue;
                    if (item instanceof String si) {
                        String t = si.trim();
                        if (!t.isEmpty()) clean.add(t);
                    } else if (item instanceof Map<?, ?> mm) {
                        Map<String, Object> child = deepSanitize((Map<String, Object>) mm);
                        if (!child.isEmpty()) clean.add(child);
                    } else {
                        clean.add(item);
                    }
                }
                if (!clean.isEmpty()) out.put(key, clean);
            } else {
                out.put(key, val);
            }
        }
        return out;
    }

    private Object redactIfSensitive(String key, String value) {
        String k = key == null ? "" : key.toLowerCase(Locale.ROOT);
        for (String s : SENSITIVE_KEYS) {
            if (k.contains(s)) {
                return "***REDACTED***";
            }
        }
        return value;
    }

    /** Limita tamaño de strings largos para evitar problemas en BD/colas. */
    @SuppressWarnings("unchecked")
    private Map<String, Object> enforceStringLimit(Map<String, Object> data, int maxLen) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : data.entrySet()) {
            Object v = e.getValue();
            if (v instanceof String s) {
                out.put(e.getKey(), (s.length() > maxLen) ? s.substring(0, maxLen) : s);
            } else if (v instanceof Map<?, ?> m) {
                out.put(e.getKey(), enforceStringLimit((Map<String, Object>) m, maxLen));
            } else if (v instanceof List<?> list) {
                List<Object> clean = new ArrayList<>(list.size());
                for (Object item : list) {
                    if (item instanceof String si) {
                        clean.add((si.length() > maxLen) ? si.substring(0, maxLen) : si);
                    } else if (item instanceof Map<?, ?> mm) {
                        clean.add(enforceStringLimit((Map<String, Object>) mm, maxLen));
                    } else {
                        clean.add(item);
                    }
                }
                out.put(e.getKey(), clean);
            } else {
                out.put(e.getKey(), v);
            }
        }
        return out;
    }

    // Puedes exponer “pasos” como Functions si quieres componerlos con Spring Cloud Function.
    public Function<Documento, Documento> asFunction(String source) {
        return d -> process(d, source);
    }
}
