package br.com.olegari.auditabr.service.security;

import org.springframework.beans.factory.annotation.Value; // CORREÇÃO: Import correto
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    private final String key;
    private final String initVector;
    // CORREÇÃO: Usando o algoritmo CBC que requer IV e com o PADDING correto
    private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";

    // CORREÇÃO: Adicionado o '$' na anotação @Value
    public EncryptionService(@Value("${mfa.encryption.key}") String key,
                             @Value("${mfa.encryption.init-vector}") String initVector) {
        this.key = key;
        this.initVector = initVector;
    }

    public String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao criptografar o valor", ex);
        }
    }

    public String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // CORREÇÃO: O cipher.init agora funciona, pois o algoritmo CBC é compatível com IV
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao descriptografar", ex);
        }
    }
}