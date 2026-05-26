package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.persistenceLayer.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findByDocumentIdOrderByPerformedAtDesc(Long documentId);
    List<AuditLogEntity> findByPerformedByIdOrderByPerformedAtDesc(Long userId);
    List<AuditLogEntity> findByDocumentOrganizationIdOrderByPerformedAtDesc(Long organizationId);
}
