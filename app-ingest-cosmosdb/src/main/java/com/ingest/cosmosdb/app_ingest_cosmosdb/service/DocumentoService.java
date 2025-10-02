package com.ingest.cosmosdb.app_ingest_cosmosdb.service;

import com.ingest.cosmosdb.app_ingest_cosmosdb.model.DocumentoEntity;
import com.ingest.cosmosdb.app_ingest_cosmosdb.repository.DocumentoRepository;
import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;
import com.ingest.cosmosdb.app_ingest_cosmosdb.mapper.DocumentoMapper;
import org.springframework.stereotype.Service;

@Service
public class DocumentoService {

    private final DocumentoRepository repo;

    public DocumentoService(DocumentoRepository repo) {
        this.repo = repo;
    }

    public DocumentoEntity save(Documento doc) {
        DocumentoEntity entity = DocumentoMapper.toEntity(doc);
        return repo.save(entity);
    }
}
