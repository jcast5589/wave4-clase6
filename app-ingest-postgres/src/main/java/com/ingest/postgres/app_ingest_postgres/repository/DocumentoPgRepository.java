package com.ingest.postgres.app_ingest_postgres.repository;

import com.ingest.postgres.app_ingest_postgres.model.DocumentoPgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentoPgRepository extends JpaRepository<DocumentoPgEntity, String> {
    Optional<DocumentoPgEntity> findFirstByUniqueId(String uniqueId);
}
