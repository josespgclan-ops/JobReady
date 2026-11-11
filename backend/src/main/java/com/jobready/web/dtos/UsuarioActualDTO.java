package com.jobready.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos mínimos de la cuenta autenticada; no expone email, teléfono ni contraseña. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioActualDTO {
    private String username;
    private String nombreCompleto;
}
