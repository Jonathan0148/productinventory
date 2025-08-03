package com.example.productinventory.service;

import com.example.productinventory.exception.ResourceNotFoundException;
import com.example.productinventory.model.Product;
import com.example.productinventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setPrice(new BigDecimal("10.00"));
        sampleProduct.setDescription("Sample description");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    void testFindById_ProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
    }

    @Test
    void testFindById_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producto con ID 1 no encontrado");
    }

    @Test
    void testCreate() {
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);

        Product created = productService.create(sampleProduct);

        assertThat(created.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void testUpdateFound() {
        Product updated = new Product();
        updated.setName("Updated");
        updated.setPrice(new BigDecimal("20.00"));
        updated.setDescription("Updated Desc");

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("20.00"));
        assertThat(result.getDescription()).isEqualTo("Updated Desc");
    }

    @Test
    void testUpdateNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(999L, sampleProduct))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producto con ID 999 no encontrado");
    }

    @Test
    void testDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        doNothing().when(productRepository).delete(sampleProduct);

        productService.delete(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(sampleProduct);
    }

    @Test
    void testDeleteNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producto con ID 999 no encontrado");
    }
}
