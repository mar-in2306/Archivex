package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.AuditLogDTO;
import com.archivex.archivex.business.service.AuditLogService;
import com.archivex.archivex.persistenceLayer.dao.AuditLogDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogDAO auditLogDAO;

    @Override
    public AuditLogDTO registerAction(Long documentId, Long performedById,
                                      String action, String details,
                                      String previousStatus, String newStatus, String ipAddress) {
        log.debug("Registrando auditoria: accion={} doc={}", action, documentId);
        AuditLogDTO dto = new AuditLogDTO();
        dto.setDocumentId(documentId);
        dto.setPerformedById(performedById);
        dto.setAction(action);
        dto.setDetails(details);
        dto.setPreviousStatus(previousStatus);
        dto.setNewStatus(newStatus);
        dto.setIpAddress(ipAddress);
        return auditLogDAO.save(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getHistoryByDocument(Long documentId) {
        log.debug("Consultando historial del documento: {}", documentId);
        return auditLogDAO.findByDocumentId(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getHistoryByUser(Long userId) {
        log.debug("Consultando historial del usuario: {}", userId);
        return auditLogDAO.findByPerformedById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getHistoryByOrganization(Long organizationId) {
        log.debug("Consultando historial de la organizacion: {}", organizationId);
        return auditLogDAO.findByOrganizationId(organizationId);
    }
}
