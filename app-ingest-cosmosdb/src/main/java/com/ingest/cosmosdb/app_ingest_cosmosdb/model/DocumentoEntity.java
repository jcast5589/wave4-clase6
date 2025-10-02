package com.ingest.cosmosdb.app_ingest_cosmosdb.model;


import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.util.Map;

/**
 * CosmosDB entity derived from Documento base model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Container(containerName = "documents", autoCreateContainer = true)
public class DocumentoEntity {

    @Id
    private String id;

    /**
     * uniqueId será la PartitionKey en Cosmos
     */
    @PartitionKey
    @NotBlank
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
}
