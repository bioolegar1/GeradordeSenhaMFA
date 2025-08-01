package br.com.olegari.auditabr.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ValidationRequest {

    @NotBlank(message = "O ID do usuário não pode ser vazio.")
    private String userId;

    @NotBlank(message = "O código TOTP não pode ser Vazio.")
    @Size(min = 6, max = 6, message = "O código TOTP dev ter 6 digitos")
    private String code;
}
