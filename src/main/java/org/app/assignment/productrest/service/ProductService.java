package org.app.assignment.productrest.service;

import org.app.assignment.productrest.dto.ProductRequestDTO;
import org.app.assignment.productrest.dto.ProductResponse;
import org.app.assignment.productrest.dto.ProductResponseDTO;

public interface ProductService {

    /**
     * Retrieves all products with pagination and sorting.
     *
     * @param page          the page number to retrieve
     * @param size          the number of products per page
     * @param sortBy        the field to sort by
     * @param sortDirection the direction to sort (asc or desc)
     * @return a paginated and sorted list of products
     */
    ProductResponse getAllProducts(int page, int size, String sortBy, String sortDirection);

    /**
     * Creates a new product.
     *
     * @param product the product to create
     * @return the created product
     */
    ProductResponseDTO createProduct(ProductRequestDTO product);


    /**
     * Retrieves a product by its code.
     *
     * @param code the code of the product to retrieve
     * @return the retrieved product
     */
    ProductResponseDTO getProductByCode(String code);
}
