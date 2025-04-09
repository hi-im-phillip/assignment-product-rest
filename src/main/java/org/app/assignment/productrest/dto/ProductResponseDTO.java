package org.app.assignment.productrest.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductResponseDTO {

    private String code;
    private String name;
    private BigDecimal priceEur;
    private BigDecimal priceUsd;
    private Boolean isAvailable;

}
