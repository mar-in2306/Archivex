package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.DocumentWorkflowDTO;
import com.archivex.archivex.business.dto.WorkflowStepDTO;
import com.archivex.archivex.business.enums.WorkflowTaskStatus;
import com.archivex.archivex.business.service.AuditLogService;
import com.archivex.archivex.business.service.WorkflowService;
import com.archivex.archivex.persistenceLayer.dao.DocumentWorkflowDAO;
import com.archivex.archivex.persistenceLayer.dao.WorkflowStepDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowStepDAO workflowStepDAO;
    private final DocumentWorkflowDAO documentWorkflowDAO;
    private final AuditLogService auditLogService;

    // ─── Pasos de flujo ──────────────────────────────────────────────────────

    @Override
    public WorkflowStepDTO createWorkflowStep(WorkflowStepDTO dto) {
        log.info("Creando paso de flujo: '{}' para tipo documental: {}", dto.getName(), dto.getDocumentTypeId());
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del paso de flujo es obligatorio");
        }
        if (dto.getStepOrder() == null || dto.getStepOrder() < 1) {
            throw new IllegalArgumentException("El orden del paso debe ser mayor a 0");
        }
        dto.setIsActive(true);
        WorkflowStepDTO result = workflowStepDAO.save(dto);
        log.info("Paso de flujo creado con ID: {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowStepDTO> getStepsByDocumentType(Long documentTypeId) {
        log.debug("Listando pasos de flujo del tipo documental: {}", documentTypeId);
        return workflowStepDAO.findByDocumentTypeId(documentTypeId);
    }

    @Override
    public WorkflowStepDTO updateWorkflowStep(Long id, WorkflowStepDTO dto) {
        log.info("Actualizando paso de flujo ID: {}", id);
        workflowStepDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Paso de flujo no encontrado con ID: " + id));
        return workflowStepDAO.update(id, dto)
                .orElseThrow(() -> new RuntimeException("Error al actualizar paso de flujo ID: " + id));
    }

    @Override
    public void deleteWorkflowStep(Long id) {
        log.info("Eliminando paso de flujo ID: {}", id);
        workflowStepDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Paso de flujo no encontrado con ID: " + id));
        workflowStepDAO.deleteById(id);
        log.info("Paso de flujo eliminado ID: {}", id);
    }

    // ─── Tareas de flujo ─────────────────────────────────────────────────────

    /**
     * Asigna una tarea de flujo de trabajo a un usuario para un documento en un paso específico.
     * Valida que el documento y el paso pertenezcan al mismo tipo documental.
     */
    @Override
    public DocumentWorkflowDTO assignTask(DocumentWorkflowDTO dto) {
        log.info("Asignando tarea de flujo al usuario: {} para documento: {}", dto.getAssignedToId(), dto.getDocumentId());
        if (dto.getDocumentId() == null || dto.getWorkflowStepId() == null || dto.getAssignedToId() == null) {
            throw new IllegalArgumentException("documentId, workflowStepId y assignedToId son obligatorios");
        }
        dto.setStatus(WorkflowTaskStatus.PENDING);
        DocumentWorkflowDTO result = documentWorkflowDAO.save(dto);

        auditLogService.registerAction(
                dto.getDocumentId(), dto.getAssignedToId(),
                "TASK_ASSIGNED",
                "Tarea asignada al usuario ID: " + dto.getAssignedToId(),
                null, null, null
        );

        log.info("Tarea asignada con ID: {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentWorkflowDTO> getTasksByDocument(Long documentId) {
        log.debug("Listando tareas del documento: {}", documentId);
        return documentWorkflowDAO.findByDocumentId(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentWorkflowDTO> getPendingTasksByUser(Long userId) {
        log.debug("Listando tareas pendientes del usuario: {}", userId);
        return documentWorkflowDAO.findPendingByUserId(userId);
    }

    /**
     * Resuelve una tarea de flujo: la completa o rechaza.
     * Registra la auditoría del evento de resolución.
     */
    @Override
    public DocumentWorkflowDTO resolveTask(Long taskId, WorkflowTaskStatus newStatus,
                                            String comments, Long organizationId, Long performedById) {
        log.info("Resolviendo tarea ID: {} con estado: {}", taskId, newStatus);

        DocumentWorkflowDTO task = documentWorkflowDAO.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + taskId));

        if (task.getStatus() != WorkflowTaskStatus.PENDING && task.getStatus() != WorkflowTaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("La tarea ya fue resuelta con estado: " + task.getStatus());
        }

        DocumentWorkflowDTO updated = documentWorkflowDAO.updateTaskStatus(taskId, newStatus, comments)
                .orElseThrow(() -> new RuntimeException("Error al resolver tarea ID: " + taskId));

        auditLogService.registerAction(
                task.getDocumentId(), performedById,
                newStatus == WorkflowTaskStatus.COMPLETED ? "TASK_COMPLETED" : "TASK_REJECTED",
                "Tarea " + taskId + " resuelta como " + newStatus + ". Comentarios: " + comments,
                task.getStatus().name(), newStatus.name(), null
        );

        log.info("Tarea ID: {} resuelta como: {}", taskId, newStatus);
        return updated;
    }
}
