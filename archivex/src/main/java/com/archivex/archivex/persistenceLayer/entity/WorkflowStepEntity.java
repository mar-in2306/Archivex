package com.archivex.archivex.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un paso de flujo de trabajo para un tipo documental.
 * Define la secuencia de aprobación parametrizable por tipo de documento.
 */
@Entity
@Table(name = "workflow_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"documentType"})
@EqualsAndHashCode(exclude = {"documentType"})
public class WorkflowStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentTypeEntity documentType;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
