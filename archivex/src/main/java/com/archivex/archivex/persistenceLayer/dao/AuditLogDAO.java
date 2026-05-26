package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.AuditLogDTO;
import com.archivex.archivex.persistenceLayer.entity.AuditLogEntity;
import com.archivex.archivex.persistenceLayer.mapper.AuditLogMapper;
import com.archivex.archivex.persistenceLayer.repository.AuditLogRepository;
import com.archivex.archivex.persistenceLayer.repository.DocumentRepository;
import com.archivex.archivex.persistenceLayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuditLogDAO {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    /**
     * Registra una nueva entrada de auditoría.
     * Se llama desde el servicio de documentos en cada acción relevante.
     */
    public AuditLogDTO save(AuditLogDTO dto) {
        AuditLogEntity entity = auditLogMapper.toEntity(dto);

        entity.setDocument(documentRepository.findById(dto.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + dto.getDocumentId())));
        entity.setPerformedBy(userRepository.findById(dto.getPerformedById())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getPerformedById())));

        return auditLogMapper.toDTO(auditLogRepository.save(entity));
    }

    /**
     * Historial completo de un documento, ordenado por fecha desc.
     */
    public List<AuditLogDTO> findByDocumentId(Long documentId) {
        return auditLogMapper.toDTOList(
                auditLogRepository.findByDocumentIdOrderByPerformedAtDesc(documentId));
    }

    /**
     * Todas las acciones realizadas por un usuario específico.
     */
    public List<AuditLogDTO> findByPerformedById(Long userId) {
        return auditLogMapper.toDTOList(
                auditLogRepository.findByPerformedByIdOrderByPerformedAtDesc(userId));
    }

    /**
     * Todas las entradas de auditoría de una organización.
     */
    public List<AuditLogDTO> findByOrganizationId(Long organizationId) {
        return auditLogMapper.toDTOList(
                auditLogRepository.findByDocumentOrganizationIdOrderByPerformedAtDesc(organizationId));
    }
}
