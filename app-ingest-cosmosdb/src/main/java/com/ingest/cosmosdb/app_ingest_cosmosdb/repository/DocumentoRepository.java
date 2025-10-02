package com.ingest.cosmosdb.app_ingest_cosmosdb.repository;

import org.springframework.stereotype.Repository;
import com.ingest.cosmosdb.app_ingest_cosmosdb.model.DocumentoEntity;
import com.azure.spring.data.cosmos.repository.CosmosRepository;

@Repository
public interface DocumentoRepository extends CosmosRepository<DocumentoEntity, String> {
    
}
