package com.archivex.archivex.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa un tipo documental dentro de una organización.
 * Ejemplos: Factura, Contrato, Informe, Oficio.
 * Cada tipo documental puede tener pasos de workflow asociados.
 */
@Entity
@Table(name = "document_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"organization", "documents", "workflowSteps"})
@EqualsAndHashCode(exclude = {"organization", "documents", "workflowSteps"})
public class DocumentTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY)
    private List<DocumentEntity> documents;

    @OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("stepOrder ASC")
    private List<WorkflowStepEntity> workflowSteps;

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
