package br.com.olegari.auditabr.domain.repository;

import br.com.olegari.auditabr.domain.entity.ValidationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationLogRepository extends JpaRepository<ValidationLog, Long> {

}
