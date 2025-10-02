package com.ingest.cosmosdb.app_ingest_cosmosdb;

import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;
import com.ingest.cosmosdb.app_ingest_cosmosdb.service.DocumentoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@SpringBootApplication
@EnableCosmosRepositories(basePackages = "com.ingest.cosmosdb.app_ingest_cosmosdb.repository")
public class AppIngestCosmosdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppIngestCosmosdbApplication.class, args);
    }

    @Bean
    public Consumer<Documento> ingestCosmosDb(DocumentoService service) {
        return doc -> {
            service.save(doc);
            // opcional: log
            // System.out.println("Guardado en Cosmos: " + doc.getUniqueId());
        };
    }
}
