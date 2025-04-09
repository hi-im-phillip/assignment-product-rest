package org.app.assignment.productrest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProductResponse {
    private List<ProductResponseDTO> products;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
