package org.app.assignment.productrest;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.Retryable;

@SpringBootApplication
@Retryable
@EnableCaching
@OpenAPIDefinition(
        info = @Info(
                title = "Rest API",
                version = "1.0",
                description = "Documentation Product REST API v1.0",
                contact = @Contact(
                        name = "Phillip",
                        email = "test@test.com"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Rest API",
                url = ""
        )
)
public class ProductRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductRestApplication.class, args);
    }

}
