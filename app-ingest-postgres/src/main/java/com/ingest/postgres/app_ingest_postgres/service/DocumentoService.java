package com.ingest.postgres.app_ingest_postgres.service;

import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;
import com.ingest.postgres.app_ingest_postgres.mapper.DocumentoPgMapper;
import com.ingest.postgres.app_ingest_postgres.model.DocumentoPgEntity;
import com.ingest.postgres.app_ingest_postgres.repository.DocumentoPgRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentoService {

    private final DocumentoPgRepository repository;

    public DocumentoService(DocumentoPgRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DocumentoPgEntity save(Documento doc) {
        DocumentoPgEntity entity = DocumentoPgMapper.toEntity(doc);
        return repository.save(entity);
    }
}
