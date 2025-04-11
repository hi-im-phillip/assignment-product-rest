package org.app.assignment.productrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.assignment.productrest.controller.ProductController;
import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.dto.ProductResponse;
import org.app.assignment.productrest.dto.ProductResponseDTO;
import org.app.assignment.productrest.exception.ResourceNotFoundException;
import org.app.assignment.productrest.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {

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

        List<ProductResponseDTO> products = List.of(productResponseDTO);
        productResponse = ProductResponse.builder()
                .products(products)
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        given(productService.createProduct(any(ProductRequestDTO.class)))
                .willReturn(productResponseDTO);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("PROD001091")))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.priceEuro", is(29.99)))
                .andExpect(jsonPath("$.isAvailable", is(true)));

        verify(productService).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    void shouldFailCreateProductWithInvalidData() throws Exception {
        ProductRequestDTO invalidProductRequestDTO = ProductRequestDTO.builder()
                .code("")
                .priceEuro("-10")
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetProductByCode() throws Exception {
        String productCode = "PROD001";
        given(productService.getProductByCode(productCode))
                .willReturn(productResponseDTO);

        mockMvc.perform(get("/api/v1/products/{code}", productCode))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(productCode)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.priceEuro", is(29.99)));

        verify(productService).getProductByCode(productCode);
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        String nonExistingProductCode = "NONEXISTENT";
        given(productService.getProductByCode(nonExistingProductCode))
                .willThrow(new ResourceNotFoundException("Product", "code", nonExistingProductCode));

        mockMvc.perform(get("/api/v1/products/{code}", nonExistingProductCode))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(productService).getProductByCode(nonExistingProductCode);
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        given(productService.getAllProducts(anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(productResponse);

        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .param("direction", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.size()", is(1)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.last", is(true)));

        verify(productService).getAllProducts(eq(0), eq(10), eq("id"), eq("asc"));
    }

    @Test
    void shouldGetAllProductsWithDefaultPagination() throws Exception {
        given(productService.getAllProducts(anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(productResponse);

        mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.size()", is(1)));

        verify(productService).getAllProducts(eq(0), eq(10), eq("id"), eq("asc"));
    }
}
