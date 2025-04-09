package org.app.assignment.productrest.service.impl;

import lombok.RequiredArgsConstructor;
import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.dto.ProductResponse;
import org.app.assignment.productrest.dto.ProductResponseDTO;
import org.app.assignment.productrest.entity.Product;
import org.app.assignment.productrest.repository.ProductRepository;
import org.app.assignment.productrest.service.CurrencyConverter;
import org.app.assignment.productrest.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CurrencyConverter currencyConverter;

    @Value("${hnb.usd.converter.code}")
    private String usdCurrencyCode;

    @Override
    public ProductResponse getAllProducts(int page, int size, String sortBy, String sortDirection) {

        Sort sorting = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(Sort.Direction.DESC, sortBy) : Sort.by(Sort.Direction.ASC, sortBy);

        Page<Product> products = productRepository.findAll(PageRequest.of(page, size, sorting));

        List<ProductResponseDTO> productResponseDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
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

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO product) {

        if (productRepository.existsByCode(product.getCode())) {
            throw new IllegalArgumentException("Product with code " + product.getCode() + " already exists.");
        }

        BigDecimal productPriceConverted = currencyConverter.convert(product.getPriceEuro(), usdCurrencyCode);

        Product entity = modelMapper.map(product, Product.class);
        entity.setPriceUsd(productPriceConverted);

        return modelMapper.map(productRepository.save(entity), ProductResponseDTO.class);
    }

    @Override
    public ProductResponseDTO getProductByCode(String code) {

        return productRepository.findByCode(code).map(p -> modelMapper.map(p, ProductResponseDTO.class))
                .orElseThrow(() -> new IllegalArgumentException("Product with code " + code + " not found."));
    }
}
