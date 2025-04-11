package org.app.assignment.productrest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.dto.ProductResponse;
import org.app.assignment.productrest.dto.ProductResponseDTO;
import org.app.assignment.productrest.entity.Product;
import org.app.assignment.productrest.exception.ApiException;
import org.app.assignment.productrest.exception.ResourceNotFoundException;
import org.app.assignment.productrest.repository.ProductRepository;
import org.app.assignment.productrest.service.CurrencyConverter;
import org.app.assignment.productrest.service.ProductService;
import org.app.assignment.productrest.utils.ProductFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductFactory factory;
    private final CurrencyConverter currencyConverter;

    @Value("${hnb.usd.converter.code}")
    private String usdCurrencyCode;

    @Cacheable(
            value = "allProducts",
            key = "{#page, #size, #sortBy, #sortDirection}",
            unless = "#result.totalElements == 0"
    )
    @Override
    public ProductResponse getAllProducts(int page, int size, String sortBy, String sortDirection) {

        log.info("Cache MISS - Dohvaćanje proizvoda iz baze: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDirection);
        Sort sorting = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Direction.DESC, sortBy) : Sort.by(Sort.Direction.ASC, sortBy);

        Page<Product> products = productRepository.findAll(PageRequest.of(page, size, sorting));

        List<ProductResponseDTO> productResponseDTOs = products.stream()
                .map(factory::toProductResponseDTO)
                .toList();

        return ProductResponse.builder()
                .products(productResponseDTOs)
                .page(products.getNumber())
                .size(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .last(products.isLast())
                .build();
    }

    @CacheEvict(value = {"allProducts", "productByCode"}, allEntries = true)
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO product) {

        if (productRepository.existsByCode(product.getCode())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product with code " + product.getCode() + " already exists.");
        }

        Product entity = factory.toProduct(product);

        BigDecimal productPriceConverted = currencyConverter.convert(entity.getPriceEuro(), usdCurrencyCode);

        entity.setPriceUsd(productPriceConverted);

        return factory.toProductResponseDTO(productRepository.save(entity));
    }

    @Cacheable(
            value = "productByCode",
            key = "{#code}",
            unless = "#result == null"
    )
    @Override
    public ProductResponseDTO getProductByCode(String code) {

        return productRepository.findByCode(code).map(factory::toProductResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Produkt", "code", code));
    }
}
