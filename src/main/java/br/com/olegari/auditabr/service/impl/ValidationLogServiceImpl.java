package br.com.olegari.auditabr.service.impl;

import br.com.olegari.auditabr.domain.entity.ValidationLog;
import br.com.olegari.auditabr.domain.repository.ValidationLogRepository;
import br.com.olegari.auditabr.service.ValidationLogService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ValidationLogServiceImpl implements ValidationLogService {

    private final ValidationLogRepository validationLogRepository;

    public ValidationLogServiceImpl(ValidationLogRepository validationLogRepository) {
        this.validationLogRepository = validationLogRepository;
    }


    @Override
    public void logValidationAttempt(String userId, boolean wasSuccessful, String ipAddress, String userAgent) {
        ValidationLog log =  ValidationLog.builder()
                .userId(userId)
                .timestamp(Instant.now())
                .wasSuccessful(wasSuccessful)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        validationLogRepository.save(log);
    }

}
