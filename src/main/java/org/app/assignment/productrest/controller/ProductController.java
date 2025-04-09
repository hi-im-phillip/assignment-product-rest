package org.app.assignment.productrest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class ProductController {

    private final ProductService productService;


    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDto) {
        return new ResponseEntity<>(productService.createProduct(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ProductResponseDTO> getProductByCode(@PathVariable String code) {
        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    @GetMapping
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(value = "page", defaultValue = FilterConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
                                                          @RequestParam(value = "size", defaultValue = FilterConstants.DEFAULT_PAGE_SIZE, required = false) int size,
                                                          @RequestParam(value = "sort", defaultValue = FilterConstants.DEFAULT_SORT, required = false) String sort,
                                                          @RequestParam(value = "direction", defaultValue = FilterConstants.DEFAULT_DIRECTION, required = false) String direction) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sort, direction));
    }
}
