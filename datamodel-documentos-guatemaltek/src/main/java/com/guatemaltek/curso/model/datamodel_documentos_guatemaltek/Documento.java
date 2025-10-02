package com.guatemaltek.curso.model.datamodel_documentos_guatemaltek;


import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public class Documento {

    private String id;             
    private String uniqueId;           
    private Timestamp timestamp;       
    private Map<String, Object> data;  
    private Map<String, Object> metadata; 
    private Estado estado;             

    public enum Estado {
        NUEVO,
        PROCESADO,
        ERROR
    }

    public Documento() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.estado = Estado.NUEVO;
    }

    public Documento(String uniqueId, Map<String,Object> data, Map<String,Object> metadata, Estado estado) {
        this();
        this.uniqueId = uniqueId;
        this.data = data;
        this.metadata = metadata;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Documento [" +
                "id=" + id +
                ", uniqueId=" + uniqueId +
                ", timestamp=" + timestamp +
                ", estado=" + estado +
                ", data=" + data +
                ", metadata=" + metadata +
                ']';
    }
}