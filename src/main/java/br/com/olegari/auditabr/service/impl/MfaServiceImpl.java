// MfaServiceImpl.java

package br.com.olegari.auditabr.service.impl;

import br.com.olegari.auditabr.domain.entity.MfaSecret;
import br.com.olegari.auditabr.domain.repository.MfaSecretRepository;
import br.com.olegari.auditabr.service.MfaService;
import br.com.olegari.auditabr.service.ValidationLogService;
import br.com.olegari.auditabr.service.security.EncryptionService;
import br.com.olegari.auditabr.web.dto.EnrolmentResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64; // <-- MUDANÇA 1: Importa a classe Base64 do Java
import java.util.Optional;

@Service
public class MfaServiceImpl implements MfaService {

    private final MfaSecretRepository mfaSecretRepository;
    private final ValidationLogService validationLogService;
    private final EncryptionService encryptionService; // <-- ADICIONAR

    // Modificar o construtor para receber o novo serviço
    public MfaServiceImpl(MfaSecretRepository mfaSecretRepository,
                          ValidationLogService validationLogService,
                          EncryptionService encryptionService) { // <-- ADICIONAR
        this.mfaSecretRepository = mfaSecretRepository;
        this.validationLogService = validationLogService;
        this.encryptionService = encryptionService; // <-- ADICIONAR
    }



    @Override
    public EnrolmentResponse initiateEnrolment(String userId) {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String rawSecret = secretGenerator.generate(); // Segredo em texto plano

        // CRIPTOGRAFA o segredo antes de salvar
        String encryptedSecret = encryptionService.encrypt(rawSecret);

        MfaSecret mfaSecret = new MfaSecret(userId, encryptedSecret, Instant.now(), true);
        mfaSecretRepository.save(mfaSecret);

        try {
            // IMPORTANTE: O QR Code deve ser gerado com o segredo em texto plano (rawSecret)!
            String qrCodeImage = generateQrCode(rawSecret, userId);
            return new EnrolmentResponse(qrCodeImage);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Erro ao gerar QR Code", e);
        }
    }
    private String generateQrCode(String secret, String userId) throws WriterException, IOException {
        String issuer = "AuditabrAPI";
        String qrCodeDataString = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, userId, secret, issuer);

        BitMatrix matrix = new MultiFormatWriter().encode(qrCodeDataString, BarcodeFormat.QR_CODE, 200, 200);

        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            // MUDANÇA 2: Usa o Base64.getEncoder() do Java para converter os bytes em string
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        }
    }

    @Override
    public boolean validateCode(String userId, String code, String ipAddress, String userAgent) {
        Optional<MfaSecret> secretOptional = mfaSecretRepository.findById(userId);

        if (secretOptional.isEmpty() || !secretOptional.get().isActive()) {
            validationLogService.logValidationAttempt(userId, false, ipAddress, userAgent);
            return false;
        }

        MfaSecret mfaSecret = secretOptional.get();

        // DESCRIPTOGRAFA o segredo recuperado do banco
        String rawSecret = encryptionService.decrypt(mfaSecret.getEncryptedSecret());

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        // Usa o segredo descriptografado (rawSecret) para validar o código
        boolean isValid = verifier.isValidCode(rawSecret, code);

        validationLogService.logValidationAttempt(userId, isValid, ipAddress, userAgent);

        return isValid;
    }

}