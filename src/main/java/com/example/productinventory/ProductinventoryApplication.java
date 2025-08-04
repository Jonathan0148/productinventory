package com.example.productinventory;

import com.example.productinventory.config.ApiKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApiKeyProperties.class)
public class ProductinventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductinventoryApplication.class, args);
        System.out.println("Microservicio productos iniciado correctamente");
    }
}
