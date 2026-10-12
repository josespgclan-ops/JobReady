package com.jobready.web.security.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroCandidatoDTO {
    private String nombreCompleto;
    private String email;
    private String username;
    private String password;
    private String telefono;
}
