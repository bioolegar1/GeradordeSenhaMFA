package br.com.olegari.auditabr.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnrolmentRequest {

    @NotBlank(message = "O ID do usuário n pode ser vazio.")
    private String userId;
}
