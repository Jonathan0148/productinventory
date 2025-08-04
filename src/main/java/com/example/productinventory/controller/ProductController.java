package com.example.productinventory.controller;

import com.example.productinventory.dto.ApiResponse;
import com.example.productinventory.dto.PaginatedResponse;
import com.example.productinventory.model.Product;
import com.example.productinventory.service.ProductService;
import com.example.productinventory.util.SuccessResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Operaciones relacionadas con productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "Obtener todos los productos paginados",
            description = "Permite listar productos con paginación opcional mediante parámetros `page`, `size` y `sort`.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado paginado de productos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedResponse.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<Product>>> getAll(Pageable pageable) {
        Page<Product> page = productService.findAll(pageable);
        return SuccessResponseUtils.buildPaginated(page, "Lista de productos obtenida correctamente");
    }

    @Operation(
            summary = "Obtener producto por ID",
            description = "Retorna un producto dado su identificador.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(
            @Parameter(description = "ID del producto a buscar", example = "1")
            @PathVariable Long id
    ) {
        Product product = productService.findById(id);
        return SuccessResponseUtils.buildOk(product, "Producto obtenido exitosamente");
    }

    @Operation(
            summary = "Crear un nuevo producto",
            description = "Permite crear un producto enviando su nombre, descripción y precio.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación")
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto producto a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Product.class))
            )
            @Valid @RequestBody Product product
    ) {
        Product created = productService.create(product);
        return SuccessResponseUtils.buildCreated(created, "Producto creado exitosamente");
    }

    @Operation(
            summary = "Actualizar producto existente",
            description = "Permite actualizar los datos de un producto existente mediante su ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(
            @Parameter(description = "ID del producto a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Product updatedProduct
    ) {
        Product updated = productService.update(id, updatedProduct);
        return SuccessResponseUtils.buildOk(updated, "Producto actualizado correctamente");
    }

    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina un producto existente dado su ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID del producto a eliminar", example = "1")
            @PathVariable Long id
    ) {
        productService.delete(id);
        return SuccessResponseUtils.buildOk(null, "Producto eliminado correctamente");
    }
}
