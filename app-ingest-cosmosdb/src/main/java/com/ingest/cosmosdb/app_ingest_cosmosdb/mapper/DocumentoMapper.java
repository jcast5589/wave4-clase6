package com.ingest.cosmosdb.app_ingest_cosmosdb.mapper;


import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;
import com.ingest.cosmosdb.app_ingest_cosmosdb.model.DocumentoEntity;

public class DocumentoMapper {

    public static DocumentoEntity toEntity(Documento doc) {
        return DocumentoEntity.builder()
                .id(doc.getId())
                .uniqueId(doc.getUniqueId())
                .timestamp(doc.getTimestamp())
                .data(doc.getData())
                .metadata(doc.getMetadata())
                .estado(doc.getEstado() != null 
                        ? DocumentoEntity.Estado.valueOf(doc.getEstado().name()) 
                        : DocumentoEntity.Estado.NUEVO)
                .build();
    }

    public static Documento toModel(DocumentoEntity entity) {
        Documento doc = new Documento(
                entity.getUniqueId(),
                entity.getData(),
                entity.getMetadata(),
                Documento.Estado.valueOf(entity.getEstado().name())
        );
        // mantener mismo id y timestamp
        doc.setUniqueId(entity.getUniqueId());
        doc.setTimestamp(entity.getTimestamp());
        return doc;
    }
}
