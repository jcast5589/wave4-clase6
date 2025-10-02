package com.ingest.postgres.app_ingest_postgres.mapper;

import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;
import com.ingest.postgres.app_ingest_postgres.model.DocumentoPgEntity;

public final class DocumentoPgMapper {
    private DocumentoPgMapper() {}

    public static DocumentoPgEntity toEntity(Documento d) {
        if (d == null) return null;
        DocumentoPgEntity e = new DocumentoPgEntity();
        e.setId(d.getId()); // usamos el mismo id que viene del modelo
        e.setUniqueId(d.getUniqueId());
        e.setTimestamp(d.getTimestamp());
        e.setData(d.getData());
        e.setMetadata(d.getMetadata());
        e.setEstado(d.getEstado() == null
                ? DocumentoPgEntity.Estado.NUEVO
                : DocumentoPgEntity.Estado.valueOf(d.getEstado().name()));
        return e;
    }

    public static Documento toModel(DocumentoPgEntity e) {
        if (e == null) return null;
        Documento d = new Documento();
        // mantenemos el mismo id/timestamp
        d.setUniqueId(e.getUniqueId());
        d.setTimestamp(e.getTimestamp());
        d.setData(e.getData());
        d.setMetadata(e.getMetadata());
        d.setEstado(Documento.Estado.valueOf(e.getEstado().name()));
        // Ojo: tu constructor por defecto genera nuevo id; si quieres conservar el id,
        // añade un setId(String) a Documento o maneja el id en metadata.
        return d;
    }
}
