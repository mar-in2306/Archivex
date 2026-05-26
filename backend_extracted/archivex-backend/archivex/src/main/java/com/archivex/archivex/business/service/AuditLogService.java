package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.AuditLogDTO;
import java.util.List;

public interface AuditLogService {
    AuditLogDTO registerAction(Long documentId, Long performedById,
                               String action, String details,
                               String previousStatus, String newStatus, String ipAddress);
    List<AuditLogDTO> getHistoryByDocument(Long documentId);
    List<AuditLogDTO> getHistoryByUser(Long userId);
    List<AuditLogDTO> getHistoryByOrganization(Long organizationId);
}
