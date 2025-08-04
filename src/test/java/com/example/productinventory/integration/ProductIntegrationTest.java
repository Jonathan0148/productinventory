package com.example.productinventory.integration;

import com.example.productinventory.model.Product;
import com.example.productinventory.repository.ProductRepository;
import com.example.productinventory.dto.ApiResponse;
import com.example.productinventory.dto.PaginatedResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase
public class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private String baseUrl;

    @Value("${api.keys.frontend}")
    private String apiKey;

    private static final String API_KEY = "pk_f8c1e7c9d7a8411b8a2c3b92fa1d91e5";


    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/products";
        productRepository.deleteAll();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", API_KEY);
        return headers;
    }

    private <T> HttpEntity<T> buildRequestWithKey(T body) {
        return new HttpEntity<>(body, buildHeaders());
    }

    @Test
    void testCreateProduct() {
        Product newProduct = new Product("Producto de prueba", "Descripción", 10.0);

        ResponseEntity<ApiResponse<Product>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                buildRequestWithKey(newProduct),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getId()).isNotNull();
    }

    @Test
    void testCreateProductWithInvalidInput() {
        Product invalidProduct = new Product("a", "", null);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                buildRequestWithKey(invalidProduct),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("El precio es obligatorio", "El nombre debe tener entre 3 y 100 caracteres");
    }

    @Test
    void testGetAllProductsWithPagination() {
        for (int i = 1; i <= 15; i++) {
            productRepository.save(new Product("Producto " + i, "Desc", 10.0 + i));
        }

        String url = baseUrl + "?page=0&size=10";

        ResponseEntity<ApiResponse<PaginatedResponse<Product>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PaginatedResponse<Product> paginated = response.getBody().getData();
        assertThat(paginated.getItems()).hasSize(10);
        assertThat(paginated.getMeta().getTotalElements()).isEqualTo(15);
        assertThat(paginated.getMeta().getTotalPages()).isEqualTo(2);
        assertThat(paginated.getMeta().getPage()).isEqualTo(0);
        assertThat(paginated.getMeta().getSize()).isEqualTo(10);
    }

    @Test
    void testGetProductById() {
        Product saved = productRepository.save(new Product("Individual", "Detalle", 40.0));

        ResponseEntity<ApiResponse<Product>> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.GET,
                new HttpEntity<>(buildHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getName()).isEqualTo("Individual");
    }

    @Test
    void testGetProductByIdNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.GET,
                new HttpEntity<>(buildHeaders()),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Producto con ID 9999 no encontrado");
    }

    @Test
    void testUpdateProduct() {
        Product saved = productRepository.save(new Product("Viejo", "desc", 100.0));
        saved.setName("Actualizado");

        HttpEntity<Product> request = buildRequestWithKey(saved);

        ResponseEntity<ApiResponse<Product>> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getName()).isEqualTo("Actualizado");
    }

    @Test
    void testUpdateProductNotFound() {
        Product product = new Product("No existe", "desc", 10.0);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.PUT,
                buildRequestWithKey(product),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteProduct() {
        Product saved = productRepository.save(new Product("A eliminar", "desc", 99.0));

        ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(buildHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Producto eliminado correctamente");
        assertThat(response.getBody().getData()).isNull();
        assertThat(productRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void testDeleteProductNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.DELETE,
                new HttpEntity<>(buildHeaders()),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Producto con ID 9999 no encontrado");
    }

    @Test
    void testRequestWithInvalidApiKeyShouldFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", "clave-invalida-12345");

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("API key inválida");
    }

    @Test
    void testRequestWithoutApiKeyShouldFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Falta el header X-API-KEY");
    }
}
