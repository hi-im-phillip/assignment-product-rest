package org.app.assignment.productrest;


import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.dto.ProductResponse;
import org.app.assignment.productrest.dto.ProductResponseDTO;
import org.app.assignment.productrest.entity.Product;
import org.app.assignment.productrest.exception.ApiException;
import org.app.assignment.productrest.exception.ResourceNotFoundException;
import org.app.assignment.productrest.repository.ProductRepository;
import org.app.assignment.productrest.service.CurrencyConverter;
import org.app.assignment.productrest.service.impl.ProductServiceImpl;
import org.app.assignment.productrest.utils.ProductFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductFactory productFactory;

    @Mock
    private CurrencyConverter currencyConverter;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productService, "usdCurrencyCode", "USD");

        product = Product.builder()
                .id(1L)
                .code("PROD001091")
                .name("Test Product")
                .isAvailable(true)
                .priceEuro(new BigDecimal("29.99"))
                .priceUsd(new BigDecimal("32.99"))
                .build();

        productRequestDTO = ProductRequestDTO.builder()
                .code("PROD001091")
                .name("Test Product")
                .priceEuro("29.99")
                .isAvailable(true)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .code("PROD001091")
                .name("Test Product")
                .priceEuro(new BigDecimal("29.99"))
                .priceUsd(new BigDecimal("32.99"))
                .isAvailable(true)
                .build();
    }

    @Test
    void shouldGetAllProducts() {
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDir = "asc";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products, pageRequest, products.size());

        given(productRepository.findAll(any(PageRequest.class))).willReturn(productPage);
        given(productFactory.toProductResponseDTO(product)).willReturn(productResponseDTO);

        ProductResponse result = productService.getAllProducts(page, size, sortBy, sortDir);

        assertNotNull(result);
        assertEquals(1, result.getProducts().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());

        verify(productRepository).findAll(any(PageRequest.class));
        verify(productFactory, times(products.size())).toProductResponseDTO(any(Product.class));
    }

    @Test
    void shouldGetAllProductsWithDescSorting() {
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDir = "desc";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        List<Product> products = Collections.emptyList();
        Page<Product> productPage = new PageImpl<>(products, pageRequest, 0);

        given(productRepository.findAll(any(PageRequest.class))).willReturn(productPage);

        ProductResponse result = productService.getAllProducts(page, size, sortBy, sortDir);

        assertNotNull(result);
        assertEquals(0, result.getProducts().size());
        assertEquals(0, result.getTotalElements());

        verify(productRepository).findAll(any(PageRequest.class));
    }

    @Test
    void shouldCreateProduct() {
        given(productRepository.existsByCode(anyString())).willReturn(false);
        given(productFactory.toProduct(any(ProductRequestDTO.class))).willReturn(product);
        given(currencyConverter.convert(any(BigDecimal.class), anyString())).willReturn(new BigDecimal("32.99"));
        given(productRepository.save(any(Product.class))).willReturn(product);
        given(productFactory.toProductResponseDTO(any(Product.class))).willReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals("PROD001091", result.getCode());
        assertEquals("Test Product", result.getName());
        assertEquals(new BigDecimal("29.99"), result.getPriceEuro());
        assertEquals(new BigDecimal("32.99"), result.getPriceUsd());

        verify(productRepository).existsByCode(productRequestDTO.getCode());
        verify(productFactory).toProduct(productRequestDTO);
        verify(currencyConverter).convert(product.getPriceEuro(), "USD");
        verify(productRepository).save(product);
        verify(productFactory).toProductResponseDTO(product);
    }

    @Test
    void shouldThrowExceptionWhenProductWithCodeAlreadyExists() {
        given(productRepository.existsByCode(anyString())).willReturn(true);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> productService.createProduct(productRequestDTO)
        );

        assertThat(exception.getMessage()).contains("already exists");
        verify(productRepository).existsByCode(productRequestDTO.getCode());
        verify(productFactory, never()).toProduct(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldGetProductByCode() {
        String productCode = "PROD001091";
        given(productRepository.findByCode(productCode)).willReturn(Optional.of(product));
        given(productFactory.toProductResponseDTO(product)).willReturn(productResponseDTO);

        ProductResponseDTO result = productService.getProductByCode(productCode);

        assertNotNull(result);
        assertEquals(productCode, result.getCode());
        assertEquals("Test Product", result.getName());

        verify(productRepository).findByCode(productCode);
        verify(productFactory).toProductResponseDTO(product);
    }

    @Test
    void shouldThrowExceptionWhenProductWithCodeNotFound() {
        String productCode = "NONEXISTENT";
        given(productRepository.findByCode(productCode)).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductByCode(productCode)
        );

        assertThat(exception.getMessage()).contains("Produkt nije pronađen");
        verify(productRepository).findByCode(productCode);
    }
}
