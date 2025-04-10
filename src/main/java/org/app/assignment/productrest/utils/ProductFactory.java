package org.app.assignment.productrest.utils;

import lombok.RequiredArgsConstructor;
import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.dto.ProductResponseDTO;
import org.app.assignment.productrest.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFactory {

    private final ModelMapper modelMapper;

    public Product toProduct(ProductRequestDTO request) {
        return modelMapper.map(request, Product.class);
    }

    public ProductResponseDTO toProductResponseDTO(Product product) {
        return modelMapper.map(product, ProductResponseDTO.class);
    }
}
