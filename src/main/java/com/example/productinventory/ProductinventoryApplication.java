package com.example.productinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductinventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductinventoryApplication.class, args);
        System.out.println("Microservicio productos iniciado correctamente");
    }

}
