package com.archivex.archivex.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de trazabilidad/auditoría.
 * Registra TODAS las acciones realizadas sobre un documento: creación, edición, cambio de estado, etc.
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"document", "performedBy"})
@EqualsAndHashCode(exclude = {"document", "performedBy"})
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Acción realizada: CREATED, UPDATED, STATUS_CHANGED, APPROVED, REJECTED, DOWNLOADED, DELETED
    @Column(nullable = false, length = 50)
    private String action;

    // Descripción detallada de la acción
    @Column(length = 1000)
    private String details;

    // Estado anterior del documento (para cambios de estado)
    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    // Estado nuevo del documento
    @Column(name = "new_status", length = 30)
    private String newStatus;

    // IP del cliente que realizó la acción
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id", nullable = false)
    private UserEntity performedBy;

    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
}
