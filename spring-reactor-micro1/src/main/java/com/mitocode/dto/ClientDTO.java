package com.mitocode.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
//@Getter @Setter @EqualsAndHashCode @ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientDTO {

    private String id;
    private String nameClient;
    private String surnameClient;
    private LocalDate birthDateClient;
    private String urlPhotoClient;
}
