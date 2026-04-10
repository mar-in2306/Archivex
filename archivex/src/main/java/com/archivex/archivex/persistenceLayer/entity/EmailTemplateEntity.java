package com.archivex.archivex.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad para plantillas de correo electrónico configurables por organización.
 * Soporta variables como {{documentTitle}}, {{userName}}, {{status}}, {{organizationName}}.
 */
@Entity
@Table(name = "email_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"organization"})
@EqualsAndHashCode(exclude = {"organization"})
public class EmailTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre único del evento: DOCUMENT_CREATED, STATUS_CHANGED, TASK_ASSIGNED, etc.
    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

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
