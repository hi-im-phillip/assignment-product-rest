package org.app.assignment.productrest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotNull(message = "Kod proizvoda je obavezan")
    @Size(min = 10, max = 10, message = "Kod proizvoda mora imati 10 znakova")
    private String code;

    @NotBlank(message = "Naziv proizvoda je obavezan")
    private String name;

    @NotNull(message = "Cijena u eurima je obavezna")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cijena mora biti veća od 0")
    private BigDecimal priceEur;

    @NotNull(message = "Dostupnost proizvoda je obavezna")
    private Boolean isAvailable;

}
