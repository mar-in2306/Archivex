package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.persistenceLayer.entity.EmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, Long> {
    List<EmailTemplateEntity> findByOrganizationId(Long organizationId);
    Optional<EmailTemplateEntity> findByOrganizationIdAndEventTypeAndIsActive(
            Long organizationId, String eventType, Boolean isActive);
}
