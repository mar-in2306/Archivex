package com.archivex.archivex.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa una organización (tenant) en el sistema Archivex.
 * Cada organización tiene sus propios usuarios, documentos y configuraciones aisladas.
 */
@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"users", "documentTypes", "documents"})
@EqualsAndHashCode(exclude = {"users", "documentTypes", "documents"})
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String domain;

    @Column(length = 500)
    private String description;

    @Column(name = "contact_email", nullable = false, length = 150)
    private String contactEmail;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<UserEntity> users;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<DocumentTypeEntity> documentTypes;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<DocumentEntity> documents;

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
