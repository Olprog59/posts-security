package com.formation.blog_security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record AuthenticationDto(
        @Email @NotNull
        String email,
        @NotNull @Size(min = 8, max = 64, message = "Le mot de passe doit contenir entre 8 et 64 caractères")
        String password
) {
}
