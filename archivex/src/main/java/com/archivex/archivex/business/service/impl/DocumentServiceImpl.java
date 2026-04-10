package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.DocumentDTO;
import com.archivex.archivex.business.enums.DocumentStatus;
import com.archivex.archivex.business.service.AuditLogService;
import com.archivex.archivex.business.service.DocumentService;
import com.archivex.archivex.business.service.EmailTemplateService;
import com.archivex.archivex.persistenceLayer.dao.DocumentDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentDAO documentDAO;
    private final AuditLogService auditLogService;
    private final EmailTemplateService emailTemplateService;

    @Value("${archivex.storage.path}")
    private String storagePath;

    /**
     * Crea un documento y almacena el archivo en el servidor.
     * Registra auditoría y envía notificación por correo.
     */
    @Override
    public DocumentDTO createDocument(DocumentDTO dto, MultipartFile file) {
        log.info("Creando documento: '{}' en org: {}", dto.getTitle(), dto.getOrganizationId());

        validateDocumentData(dto);

        // Guardar archivo si viene adjunto
        if (file != null && !file.isEmpty()) {
            String storedPath = storeFile(file, dto.getOrganizationId());
            dto.setFilePath(storedPath);
            dto.setFileName(file.getOriginalFilename());
            dto.setFileType(file.getContentType());
            dto.setFileSize(file.getSize());
        }

        dto.setStatus(DocumentStatus.CREATED);
        DocumentDTO saved = documentDAO.save(dto);

        // Registrar auditoría
        auditLogService.registerAction(
                saved.getId(), dto.getCreatedById(),
                "CREATED", "Documento creado: " + saved.getTitle(),
                null, DocumentStatus.CREATED.name(), null
        );

        // Notificación asíncrona
        sendCreationNotification(saved);

        log.info("Documento creado con ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDTO getDocumentById(Long id, Long organizationId) {
        log.debug("Buscando documento ID: {} en org: {}", id, organizationId);
        return documentDAO.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByOrganization(Long organizationId) {
        log.debug("Listando documentos de org: {}", organizationId);
        return documentDAO.findByOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> filterDocuments(Long organizationId, DocumentStatus status,
                                              Long documentTypeId, LocalDateTime from, LocalDateTime to) {
        log.debug("Filtrando documentos org: {} | status: {} | tipo: {}", organizationId, status, documentTypeId);
        return documentDAO.findByFilters(organizationId, status, documentTypeId, from, to);
    }

    /**
     * Actualiza únicamente los metadatos del documento (título, descripción, tipo).
     * No reemplaza el archivo; para eso se debe eliminar y volver a crear.
     */
    @Override
    public DocumentDTO updateDocumentMetadata(Long id, DocumentDTO dto, Long organizationId, Long performedById) {
        log.info("Actualizando metadatos de documento ID: {}", id);
        DocumentDTO existing = getDocumentById(id, organizationId);

        DocumentDTO updated = documentDAO.update(id, dto)
                .orElseThrow(() -> new RuntimeException("Error al actualizar documento ID: " + id));

        auditLogService.registerAction(
                id, performedById,
                "UPDATED", "Metadatos actualizados",
                existing.getStatus().name(), updated.getStatus().name(), null
        );

        return updated;
    }

    /**
     * Cambia el estado del documento validando transiciones válidas.
     * Transiciones permitidas:
     *   CREATED → UNDER_REVIEW
     *   UNDER_REVIEW → APPROVED | REJECTED
     *   REJECTED → UNDER_REVIEW (reintentar)
     */
    @Override
    public DocumentDTO changeDocumentStatus(Long id, DocumentStatus newStatus,
                                             Long organizationId, Long performedById) {
        log.info("Cambiando estado de documento ID: {} a {}", id, newStatus);
        DocumentDTO existing = getDocumentById(id, organizationId);

        validateStatusTransition(existing.getStatus(), newStatus);

        DocumentDTO updated = documentDAO.updateStatus(id, newStatus)
                .orElseThrow(() -> new RuntimeException("Error al cambiar estado del documento ID: " + id));

        auditLogService.registerAction(
                id, performedById,
                "STATUS_CHANGED",
                "Estado cambiado de " + existing.getStatus() + " a " + newStatus,
                existing.getStatus().name(), newStatus.name(), null
        );

        sendStatusNotification(updated, existing.getStatus());

        return updated;
    }

    @Override
    public void deleteDocument(Long id, Long organizationId, Long performedById) {
        log.info("Eliminando documento ID: {}", id);
        DocumentDTO existing = getDocumentById(id, organizationId);

        auditLogService.registerAction(
                id, performedById,
                "DELETED", "Documento eliminado: " + existing.getTitle(),
                existing.getStatus().name(), null, null
        );

        // Eliminar archivo físico si existe
        if (existing.getFilePath() != null) {
            deleteFile(existing.getFilePath());
        }

        boolean deleted = documentDAO.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("Error al eliminar documento ID: " + id);
        }
        log.info("Documento eliminado ID: {}", id);
    }

    @Override
    public byte[] downloadDocument(Long id, Long organizationId, Long performedById) {
        log.info("Descargando documento ID: {}", id);
        DocumentDTO doc = getDocumentById(id, organizationId);

        if (doc.getFilePath() == null) {
            throw new RuntimeException("El documento no tiene archivo adjunto");
        }

        auditLogService.registerAction(
                id, performedById,
                "DOWNLOADED", "Documento descargado",
                null, null, null
        );

        try {
            Path filePath = Paths.get(doc.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Error al leer archivo del documento ID: {}", id, e);
            throw new RuntimeException("Error al descargar el archivo: " + e.getMessage());
        }
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    private String storeFile(MultipartFile file, Long organizationId) {
        try {
            String orgFolder = storagePath + "org_" + organizationId + "/";
            Path dirPath = Paths.get(orgFolder);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = dirPath.resolve(uniqueName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Archivo almacenado en: {}", destination);
            return destination.toString();
        } catch (IOException e) {
            log.error("Error al almacenar archivo: {}", e.getMessage());
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    private void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo: {}", filePath);
        }
    }

    private void validateDocumentData(DocumentDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título del documento es obligatorio");
        }
        if (dto.getDocumentTypeId() == null) {
            throw new IllegalArgumentException("El tipo documental es obligatorio");
        }
        if (dto.getOrganizationId() == null) {
            throw new IllegalArgumentException("La organización es obligatoria");
        }
        if (dto.getCreatedById() == null) {
            throw new IllegalArgumentException("El usuario creador es obligatorio");
        }
    }

    private void validateStatusTransition(DocumentStatus current, DocumentStatus next) {
        boolean valid = switch (current) {
            case CREATED -> next == DocumentStatus.UNDER_REVIEW;
            case UNDER_REVIEW -> next == DocumentStatus.APPROVED || next == DocumentStatus.REJECTED;
            case REJECTED -> next == DocumentStatus.UNDER_REVIEW;
            case APPROVED -> false;
        };
        if (!valid) {
            throw new IllegalStateException(
                    "Transición de estado inválida: " + current + " → " + next);
        }
    }

    @Async
    protected void sendCreationNotification(DocumentDTO doc) {
        try {
            emailTemplateService.sendNotification(
                    doc.getOrganizationId(),
                    "DOCUMENT_CREATED",
                    doc.getCreatedByName(),
                    Map.of(
                            "documentTitle", doc.getTitle(),
                            "userName", doc.getCreatedByName() != null ? doc.getCreatedByName() : "",
                            "status", doc.getStatus().name()
                    )
            );
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de creación: {}", e.getMessage());
        }
    }

    @Async
    protected void sendStatusNotification(DocumentDTO doc, DocumentStatus previousStatus) {
        try {
            emailTemplateService.sendNotification(
                    doc.getOrganizationId(),
                    "STATUS_CHANGED",
                    doc.getCreatedByName(),
                    Map.of(
                            "documentTitle", doc.getTitle(),
                            "previousStatus", previousStatus.name(),
                            "newStatus", doc.getStatus().name()
                    )
            );
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de cambio de estado: {}", e.getMessage());
        }
    }
}
