package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.DocumentDTO;
import com.archivex.archivex.business.enums.DocumentStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentService {
    DocumentDTO createDocument(DocumentDTO dto, MultipartFile file);
    DocumentDTO getDocumentById(Long id, Long organizationId);
    List<DocumentDTO> getDocumentsByOrganization(Long organizationId);
    List<DocumentDTO> filterDocuments(Long organizationId, DocumentStatus status,
                                      Long documentTypeId, LocalDateTime from, LocalDateTime to);
    DocumentDTO updateDocumentMetadata(Long id, DocumentDTO dto, Long organizationId, Long performedById);
    DocumentDTO changeDocumentStatus(Long id, DocumentStatus newStatus, Long organizationId, Long performedById);
    void deleteDocument(Long id, Long organizationId, Long performedById);
    byte[] downloadDocument(Long id, Long organizationId, Long performedById);
}
