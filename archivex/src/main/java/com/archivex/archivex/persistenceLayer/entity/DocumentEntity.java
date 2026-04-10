package com.archivex.archivex.persistenceLayer.entity;

import com.archivex.archivex.business.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad principal del sistema: representa un documento digital.
 * Pertenece a una organización, tiene un tipo, un estado, metadatos y un archivo almacenado.
 */
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"organization", "documentType", "createdBy", "auditLogs", "workflowTasks"})
@EqualsAndHashCode(exclude = {"organization", "documentType", "createdBy", "auditLogs", "workflowTasks"})
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus status;

    // Ruta del archivo almacenado en el servidor
    @Column(name = "file_path", length = 500)
    private String filePath;

    // Nombre original del archivo subido
    @Column(name = "file_name", length = 255)
    private String fileName;

    // Tipo MIME del archivo (application/pdf, etc.)
    @Column(name = "file_type", length = 100)
    private String fileType;

    // Tamaño del archivo en bytes
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentTypeEntity documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AuditLogEntity> auditLogs;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DocumentWorkflowEntity> workflowTasks;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = DocumentStatus.CREATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
