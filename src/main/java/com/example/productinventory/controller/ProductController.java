package com.example.productinventory.controller;

import com.example.productinventory.dto.ApiResponse;
import com.example.productinventory.model.Product;
import com.example.productinventory.service.ProductService;
import com.example.productinventory.util.SuccessResponseUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.productinventory.dto.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<Product>>> getAll(Pageable pageable) {
        Page<Product> page = productService.findAll(pageable);
        return SuccessResponseUtils.buildPaginated(page, "Lista de productos obtenida correctamente");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return SuccessResponseUtils.buildOk(product, "Producto obtenido exitosamente");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@Valid @RequestBody Product product) {
        Product created = productService.create(product);
        return SuccessResponseUtils.buildCreated(created, "Producto creado exitosamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Long id, @Valid @RequestBody Product updatedProduct) {
        Product updated = productService.update(id, updatedProduct);
        return SuccessResponseUtils.buildOk(updated, "Producto actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return SuccessResponseUtils.buildOk(null, "Producto eliminado correctamente");
    }
}
