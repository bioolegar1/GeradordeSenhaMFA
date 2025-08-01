package br.com.olegari.auditabr.domain.repository;

import br.com.olegari.auditabr.domain.entity.MfaSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MfaSecretRepository extends JpaRepository<MfaSecret, String> {

}
