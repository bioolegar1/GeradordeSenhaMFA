package br.com.olegari.auditabr.service;

import br.com.olegari.auditabr.web.dto.EnrolmentResponse;

public interface MfaService {


    /**
     * Inicia o processo de cadastro de um dispositivo MFA para um usuário.
     * Gera um novo segredo, salva no banco e retorna um QR Code para o setup.
     *
     * @param userId O identificador do usuário.
     * @return um objeto EnrolmentResponse contendo o QR Code em Base64.
     */


    EnrolmentResponse initiateEnrolment(String userId);



    /**
     * Valida um código TOTP para um determinado usuário.
     *
     * @param userId O identificador do usuário.
     * @param code O código de 6 dígitos fornecido pelo usuário.
     * @param ipAddress O IP de origem para fins de auditoria.
     * @param userAgent O User-Agent para fins de auditoria.
     * @return true se o código for válido, false caso contrário.
     */



    boolean validateCode(String userId, String code, String ipAddress, String userAgent);
}
