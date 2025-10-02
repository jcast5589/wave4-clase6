package com.expose.data.app_expose_queue_solace;

import java.util.function.Function;

import com.expose.data.app_expose_queue_solace.service.DocumentProcessor;
import com.guatemaltek.curso.model.datamodel_documentos_guatemaltek.Documento;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppExposeQueueSolaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppExposeQueueSolaceApplication.class, args);
    }

    @Bean
    public DocumentProcessor documentProcessor() {
        return new DocumentProcessor();
    }

    /**
     * Function expuesta a Spring Cloud Stream.
     * Entrada: Documento “crudo” desde la cola/topic.
     * Salida: Documento normalizado, saneado y listo para downstream.
     */
    @Bean
    public Function<Documento, Documento> bussinessLogic(DocumentProcessor processor) {
        return reading -> {
            // “source” puede venir de config/headers; aquí fijo para demo:
            Documento cleaned = processor.process(reading, "app-expose-queue-solace");
            // Si quieres loguear en JSON:
            // log.info("CleanedDoc={}", processor.toJson(cleaned));
            return cleaned;
        };
    }
}
