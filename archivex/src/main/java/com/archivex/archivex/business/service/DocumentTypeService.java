package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.DocumentTypeDTO;
import java.util.List;

public interface DocumentTypeService {
    DocumentTypeDTO createDocumentType(DocumentTypeDTO dto);
    DocumentTypeDTO getDocumentTypeById(Long id, Long organizationId);
    List<DocumentTypeDTO> getDocumentTypesByOrganization(Long organizationId);
    DocumentTypeDTO updateDocumentType(Long id, DocumentTypeDTO dto, Long organizationId);
    void deleteDocumentType(Long id, Long organizationId);
}
