package org.app.assignment.productrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.assignment.productrest.aspect.annotation.PriceValidation;
import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.dto.ProductResponse;
import org.app.assignment.productrest.dto.ProductResponseDTO;
import org.app.assignment.productrest.service.ProductService;
import org.app.assignment.productrest.utils.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(
        name = "REST API for Product Resource",
        description = "Product API"
)
public class ProductController {

    private final ProductService productService;


    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Create a new product"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Product created successfully"
    )
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody @PriceValidation(fieldName = "priceEuro") ProductRequestDTO requestDto) {
        return new ResponseEntity<>(productService.createProduct(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{code}")
    @Operation(
            summary = "Get product by code",
            description = "Get product by code"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product retrieved successfully"
    )
    public ResponseEntity<ProductResponseDTO> getProductByCode(@PathVariable String code) {
        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Get all products"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully"
    )
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(value = "page", defaultValue = FilterConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
                                                          @RequestParam(value = "size", defaultValue = FilterConstants.DEFAULT_PAGE_SIZE, required = false) int size,
                                                          @RequestParam(value = "sort", defaultValue = FilterConstants.DEFAULT_SORT, required = false) String sort,
                                                          @RequestParam(value = "direction", defaultValue = FilterConstants.DEFAULT_DIRECTION, required = false) String direction) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sort, direction));
    }
}
