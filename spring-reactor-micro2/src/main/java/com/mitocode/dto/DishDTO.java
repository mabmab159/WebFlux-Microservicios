package com.mitocode.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DishDTO {

    private String id;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 3)
    private String nameDish;

    @Min(value = 1)
    @Max(value = 999)
    private Double priceDish;

    @NotNull
    private Boolean statusDish;

    /*@Email
    @Pattern(regexp = "[0-9]+")
    private String email;*/
}
