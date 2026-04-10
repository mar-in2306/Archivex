package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.DocumentTypeDTO;
import com.archivex.archivex.business.service.DocumentTypeService;
import com.archivex.archivex.persistenceLayer.dao.DocumentTypeDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentTypeDAO documentTypeDAO;

    @Override
    public DocumentTypeDTO createDocumentType(DocumentTypeDTO dto) {
        log.info("Creando tipo documental: {} para org: {}", dto.getName(), dto.getOrganizationId());
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo documental es obligatorio");
        }
        if (documentTypeDAO.existsByNameAndOrganizationId(dto.getName(), dto.getOrganizationId())) {
            throw new IllegalArgumentException("Ya existe un tipo documental con el nombre: " + dto.getName());
        }
        dto.setIsActive(true);
        DocumentTypeDTO result = documentTypeDAO.save(dto);
        log.info("Tipo documental creado con ID: {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTypeDTO getDocumentTypeById(Long id, Long organizationId) {
        log.debug("Buscando tipo documental ID: {} en org: {}", id, organizationId);
        return documentTypeDAO.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Tipo documental no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentTypeDTO> getDocumentTypesByOrganization(Long organizationId) {
        log.debug("Listando tipos documentales de org: {}", organizationId);
        return documentTypeDAO.findByOrganizationId(organizationId);
    }

    @Override
    public DocumentTypeDTO updateDocumentType(Long id, DocumentTypeDTO dto, Long organizationId) {
        log.info("Actualizando tipo documental ID: {}", id);
        getDocumentTypeById(id, organizationId);
        return documentTypeDAO.update(id, dto)
                .orElseThrow(() -> new RuntimeException("Error al actualizar tipo documental ID: " + id));
    }

    @Override
    public void deleteDocumentType(Long id, Long organizationId) {
        log.info("Eliminando tipo documental ID: {}", id);
        getDocumentTypeById(id, organizationId);
        boolean deleted = documentTypeDAO.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("Error al eliminar tipo documental ID: " + id);
        }
        log.info("Tipo documental eliminado ID: {}", id);
    }
}
