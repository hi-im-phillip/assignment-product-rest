package org.app.assignment.productrest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.entity.Product;
import org.app.assignment.productrest.repository.ProductRepository;
import org.app.assignment.productrest.service.CurrencyConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CurrencyConverter currencyConverter;

    @BeforeEach
    void setUp() {
        given(currencyConverter.convert(any(BigDecimal.class), eq("USD")))
                .willReturn(new BigDecimal("32.99"));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveProduct() throws Exception {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .code("TEST123890")
                .name("Integration Test Product")
                .priceEuro("29.99")
                .isAvailable(true)
                .build();

        ResultActions createResult = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        createResult
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("TEST123890")))
                .andExpect(jsonPath("$.name", is("Integration Test Product")))
                .andExpect(jsonPath("$.priceEuro", is(29.99)))
                .andExpect(jsonPath("$.priceUsd", is(32.99)));

        ResultActions getResult = mockMvc.perform(get("/api/v1/products/TEST123890"));

        getResult
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("TEST123890")))
                .andExpect(jsonPath("$.name", is("Integration Test Product")))
                .andExpect(jsonPath("$.priceEuro", is(29.99)))
                .andExpect(jsonPath("$.priceUsd", is(32.99)));
    }

    @Test
    void shouldReturnPaginatedProducts() throws Exception {
        createTestProduct("PROD112345", "Product 1", new BigDecimal("10.00"));
        createTestProduct("PROD212345", "Product 2", new BigDecimal("20.00"));
        createTestProduct("PROD312345", "Product 3", new BigDecimal("30.00"));

        ResultActions result = mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "2")
                .param("sort", "priceEuro")
                .param("direction", "asc"));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.size()", is(2)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.products[0].priceEuro", is(10.0)))
                .andExpect(jsonPath("$.products[1].priceEuro", is(20.0)));

        ResultActions secondPageResult = mockMvc.perform(get("/api/v1/products")
                .param("page", "1")
                .param("size", "2")
                .param("sort", "priceEuro")
                .param("direction", "asc"));

        secondPageResult
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.size()", is(1)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.products[0].priceEuro", is(30.0)));
    }

    @Test
    void shouldFailToCreateProductWithDuplicateCode() throws Exception {
        createTestProduct("DUPLICATE1", "Original Product", new BigDecimal("15.99"));

        ProductRequestDTO duplicateRequest = ProductRequestDTO.builder()
                .code("DUPLICATE1")
                .name("Duplicate Product")
                .priceEuro("25.99")
                .isAvailable(true)
                .build();

        ResultActions result = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)));

        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    void shouldReturnNotFoundForNonExistentProductCode() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/products/NONEXISTENT"));

        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Produkt nije pronađen")));
    }

    @Test
    void shouldFailToCreateProductWithInvalidData() throws Exception {
        ProductRequestDTO invalidRequest = ProductRequestDTO.builder()
                .code("")
                .name("")
                .priceEuro("-5.99")
                .build();

        ResultActions result = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        result
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void createTestProduct(String code, String name, BigDecimal priceEuro) {
        Product product = Product.builder()
                .code(code)
                .name(name)
                .priceEuro(priceEuro)
                .priceUsd(new BigDecimal("32.99"))
                .isAvailable(true)
                .build();

        productRepository.save(product);
    }
}
