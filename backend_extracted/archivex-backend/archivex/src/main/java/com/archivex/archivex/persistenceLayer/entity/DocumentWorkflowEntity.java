package com.archivex.archivex.persistenceLayer.entity;

import com.archivex.archivex.business.enums.WorkflowTaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una tarea de flujo de trabajo asignada a un usuario para un documento.
 * Registra quién debe revisar/aprobar el documento en cada paso del flujo.
 */
@Entity
@Table(name = "document_workflows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"document", "workflowStep", "assignedTo"})
@EqualsAndHashCode(exclude = {"document", "workflowStep", "assignedTo"})
public class DocumentWorkflowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkflowTaskStatus status;

    @Column(length = 1000)
    private String comments;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_step_id", nullable = false)
    private WorkflowStepEntity workflowStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", nullable = false)
    private UserEntity assignedTo;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = WorkflowTaskStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
