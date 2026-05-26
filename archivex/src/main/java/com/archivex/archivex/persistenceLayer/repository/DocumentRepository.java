package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.business.enums.DocumentStatus;
import com.archivex.archivex.persistenceLayer.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByOrganizationId(Long organizationId);
    List<DocumentEntity> findByOrganizationIdAndStatus(Long organizationId, DocumentStatus status);
    List<DocumentEntity> findByOrganizationIdAndDocumentTypeId(Long organizationId, Long documentTypeId);
    List<DocumentEntity> findByOrganizationIdAndCreatedById(Long organizationId, Long userId);
    Optional<DocumentEntity> findByIdAndOrganizationId(Long id, Long organizationId);

    @Query("SELECT d FROM DocumentEntity d WHERE d.organization.id = :orgId " +
           "AND (:status IS NULL OR d.status = :status) " +
           "AND (:documentTypeId IS NULL OR d.documentType.id = :documentTypeId) " +
           "AND (:from IS NULL OR d.createdAt >= :from) " +
           "AND (:to IS NULL OR d.createdAt <= :to)")
    List<DocumentEntity> findByFilters(@Param("orgId") Long orgId,
                                       @Param("status") DocumentStatus status,
                                       @Param("documentTypeId") Long documentTypeId,
                                       @Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to);
}
