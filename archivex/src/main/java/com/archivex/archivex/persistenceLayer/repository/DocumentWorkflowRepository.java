package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.business.enums.WorkflowTaskStatus;
import com.archivex.archivex.persistenceLayer.entity.DocumentWorkflowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentWorkflowRepository extends JpaRepository<DocumentWorkflowEntity, Long> {
    List<DocumentWorkflowEntity> findByDocumentId(Long documentId);
    List<DocumentWorkflowEntity> findByAssignedToId(Long userId);
    List<DocumentWorkflowEntity> findByAssignedToIdAndStatus(Long userId, WorkflowTaskStatus status);
    List<DocumentWorkflowEntity> findByDocumentIdAndStatus(Long documentId, WorkflowTaskStatus status);
}
