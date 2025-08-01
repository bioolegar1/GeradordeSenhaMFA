package br.com.olegari.auditabr.service;

import br.com.olegari.auditabr.web.dto.ValidationRequest;

public interface ValidationLogService {

    /**
    Registra uma tentativa de validação no
        @param userId - Usuario que tentou a validação
        @param wasSuccessful - true se o código for válido, false caos contrario
        @param ipAddress - O endereço de IP de origem da requisição
        @param userAgent - O User-Agent do cliente que fez a requisição
     **/

    void logValidationAttempt(String userId, boolean wasSuccessful, String ipAddress, String userAgent);
}
