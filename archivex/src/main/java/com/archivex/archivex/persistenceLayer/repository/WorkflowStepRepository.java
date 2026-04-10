package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.persistenceLayer.entity.WorkflowStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStepEntity, Long> {
    List<WorkflowStepEntity> findByDocumentTypeIdOrderByStepOrderAsc(Long documentTypeId);
    List<WorkflowStepEntity> findByDocumentTypeIdAndIsActiveOrderByStepOrderAsc(Long documentTypeId, Boolean isActive);
}
