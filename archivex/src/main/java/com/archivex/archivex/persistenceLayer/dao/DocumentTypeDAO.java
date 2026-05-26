package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.DocumentTypeDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import com.archivex.archivex.persistenceLayer.mapper.DocumentTypeMapper;
import com.archivex.archivex.persistenceLayer.repository.DocumentTypeRepository;
import com.archivex.archivex.persistenceLayer.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DocumentTypeDAO {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;
    private final OrganizationRepository organizationRepository;

    public DocumentTypeDTO save(DocumentTypeDTO dto) {
        DocumentTypeEntity entity = documentTypeMapper.toEntity(dto);
        OrganizationEntity org = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con ID: " + dto.getOrganizationId()));
        entity.setOrganization(org);
        return documentTypeMapper.toDTO(documentTypeRepository.save(entity));
    }

    public Optional<DocumentTypeDTO> findById(Long id) {
        return documentTypeRepository.findById(id).map(documentTypeMapper::toDTO);
    }

    public Optional<DocumentTypeDTO> findByIdAndOrganizationId(Long id, Long organizationId) {
        return documentTypeRepository.findByIdAndOrganizationId(id, organizationId)
                .map(documentTypeMapper::toDTO);
    }

    public List<DocumentTypeDTO> findByOrganizationId(Long organizationId) {
        return documentTypeMapper.toDTOList(documentTypeRepository.findByOrganizationId(organizationId));
    }

    public List<DocumentTypeDTO> findActiveByOrganizationId(Long organizationId) {
        return documentTypeMapper.toDTOList(
                documentTypeRepository.findByOrganizationIdAndIsActive(organizationId, true));
    }

    public Optional<DocumentTypeDTO> update(Long id, DocumentTypeDTO dto) {
        return documentTypeRepository.findById(id).map(existing -> {
            documentTypeMapper.updateEntityFromDTO(dto, existing);
            return documentTypeMapper.toDTO(documentTypeRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (documentTypeRepository.existsById(id)) {
            documentTypeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByNameAndOrganizationId(String name, Long organizationId) {
        return documentTypeRepository.existsByNameAndOrganizationId(name, organizationId);
    }

    public boolean existsById(Long id) {
        return documentTypeRepository.existsById(id);
    }
}
