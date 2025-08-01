// MfaSecret.java

package br.com.olegari.auditabr.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data // <-- ESSA ANOTAÇÃO CRIA OS GETTERS E SETTERS PÚBLICOS, COMO O isActive()
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mfa_secrets")
public class MfaSecret {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "encrypted_secret", nullable = false)
    private String encryptedSecret;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

}