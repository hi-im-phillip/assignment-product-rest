package org.app.assignment.productrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Product", description = "Product Request DTO Model information")
public class ProductRequestDTO {

    @NotNull(message = "Kod proizvoda je obavezan")
    @Size(min = 10, max = 10, message = "Kod proizvoda mora imati 10 znakova")
    @Schema(description = "Jedinstveni kod proizvoda", example = "CODE123456")
    private String code;

    @NotBlank(message = "Naziv proizvoda je obavezan")
    @Schema(description = "Naziv proizvoda", example = "Super proizvod")
    private String name;

    @NotNull(message = "Cijena u eurima je obavezna")
    @Schema(description = "Cijena proizvoda u eurima", example = "15.99")
    private String priceEuro;

    @NotNull(message = "Dostupnost proizvoda je obavezna")
    @Schema(description = "Dostupnost proizvoda", example = "true")
    private Boolean isAvailable;

}
