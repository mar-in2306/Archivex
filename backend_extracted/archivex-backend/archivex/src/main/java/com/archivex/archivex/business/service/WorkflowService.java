package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.DocumentWorkflowDTO;
import com.archivex.archivex.business.dto.WorkflowStepDTO;
import com.archivex.archivex.business.enums.WorkflowTaskStatus;

import java.util.List;

public interface WorkflowService {
    // Pasos de flujo por tipo documental
    WorkflowStepDTO createWorkflowStep(WorkflowStepDTO dto);
    List<WorkflowStepDTO> getStepsByDocumentType(Long documentTypeId);
    WorkflowStepDTO updateWorkflowStep(Long id, WorkflowStepDTO dto);
    void deleteWorkflowStep(Long id);

    // Tareas de flujo por documento
    DocumentWorkflowDTO assignTask(DocumentWorkflowDTO dto);
    List<DocumentWorkflowDTO> getTasksByDocument(Long documentId);
    List<DocumentWorkflowDTO> getPendingTasksByUser(Long userId);
    DocumentWorkflowDTO resolveTask(Long taskId, WorkflowTaskStatus newStatus,
                                    String comments, Long organizationId, Long performedById);
}
