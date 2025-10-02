package com.ingest.postgres.app_ingest_postgres;

import java.util.function.Consumer;

import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;
import com.ingest.postgres.app_ingest_postgres.service.DocumentoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppIngestPostgresApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppIngestPostgresApplication.class, args);
    }

    @Bean
    public Consumer<Documento> ingestPostgres(DocumentoService service) {
        return doc -> {
            service.save(doc);
            // System.out.println("Guardado en Postgres: " + doc.getUniqueId());
        };
    }
}
