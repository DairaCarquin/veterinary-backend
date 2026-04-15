package com.clinic.auth_service.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @JsonAlias("username")
    @Email(message = "El email debe ser valido")
    @NotBlank(message = "El email es obligatorio")
    @Size(max = 100, message = "El email no debe exceder 100 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@masterdog\\.com$", message = "El email debe pertenecer al dominio @masterdog.com")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, max = 120, message = "La contrasena debe tener entre 8 y 120 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,120}$",
            message = "La contrasena debe incluir una mayuscula, un numero y un caracter especial")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String role;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\s'-]{1,100}$", message = "El nombre solo puede contener letras")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no debe exceder 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\s'-]{1,100}$", message = "El apellido solo puede contener letras")
    private String lastName;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 digitos")
    private String dni;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El telefono debe tener exactamente 9 digitos")
    private String phone;
}
