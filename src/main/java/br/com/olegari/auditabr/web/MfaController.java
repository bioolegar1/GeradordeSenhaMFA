package br.com.olegari.auditabr.web;

import br.com.olegari.auditabr.service.MfaService;
import br.com.olegari.auditabr.web.dto.EnrolmentRequest;
import br.com.olegari.auditabr.web.dto.EnrolmentResponse;
import br.com.olegari.auditabr.web.dto.ValidationRequest;
import br.com.olegari.auditabr.web.dto.ValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mfa")
public class MfaController {

    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    /**
     * Extrai o endereço IP da requisição.
     * Tenta pegar primeiro do header X-Forwarded-For (comum em ambientes com proxy/load balancer)
     * e, se não encontrar, pega o IP da conexão remota direta.
     */

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    @PostMapping("/enrol")
    public ResponseEntity<EnrolmentResponse> enrol(@Valid @RequestBody EnrolmentRequest request) {
        EnrolmentResponse response = mfaService.initiateEnrolment(request.getUserId());
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateCode(@Valid @RequestBody ValidationRequest validationRequest, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");


        boolean isValid = mfaService.validateCode(
                validationRequest.getUserId(),
                validationRequest.getCode(),
                ipAddress,
                userAgent

        );

        return ResponseEntity.ok().body(new ValidationResponse(isValid));
    }
}
