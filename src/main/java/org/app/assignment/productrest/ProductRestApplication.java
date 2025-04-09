package org.app.assignment.productrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.Retryable;

@SpringBootApplication
@Retryable
@EnableCaching
public class ProductRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductRestApplication.class, args);
    }

}
