package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.DocumentDTO;
import com.archivex.archivex.business.enums.DocumentStatus;
import com.archivex.archivex.persistenceLayer.entity.DocumentEntity;
import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import com.archivex.archivex.persistenceLayer.mapper.DocumentMapper;
import com.archivex.archivex.persistenceLayer.repository.DocumentRepository;
import com.archivex.archivex.persistenceLayer.repository.DocumentTypeRepository;
import com.archivex.archivex.persistenceLayer.repository.OrganizationRepository;
import com.archivex.archivex.persistenceLayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DocumentDAO {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final OrganizationRepository organizationRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final UserRepository userRepository;

    public DocumentDTO save(DocumentDTO dto) {
        DocumentEntity entity = documentMapper.toEntity(dto);

        OrganizationEntity org = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada: " + dto.getOrganizationId()));
        DocumentTypeEntity type = documentTypeRepository.findById(dto.getDocumentTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo documental no encontrado: " + dto.getDocumentTypeId()));
        UserEntity creator = userRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getCreatedById()));

        entity.setOrganization(org);
        entity.setDocumentType(type);
        entity.setCreatedBy(creator);

        return documentMapper.toDTO(documentRepository.save(entity));
    }

    public Optional<DocumentDTO> findById(Long id) {
        return documentRepository.findById(id).map(documentMapper::toDTO);
    }

    public Optional<DocumentEntity> findEntityById(Long id) {
        return documentRepository.findById(id);
    }

    public Optional<DocumentDTO> findByIdAndOrganizationId(Long id, Long organizationId) {
        return documentRepository.findByIdAndOrganizationId(id, organizationId).map(documentMapper::toDTO);
    }

    public List<DocumentDTO> findByOrganizationId(Long organizationId) {
        return documentMapper.toDTOList(documentRepository.findByOrganizationId(organizationId));
    }

    public List<DocumentDTO> findByFilters(Long orgId, DocumentStatus status,
                                            Long documentTypeId, LocalDateTime from, LocalDateTime to) {
        return documentMapper.toDTOList(
                documentRepository.findByFilters(orgId, status, documentTypeId, from, to));
    }

    public Optional<DocumentDTO> update(Long id, DocumentDTO dto) {
        return documentRepository.findById(id).map(existing -> {
            documentMapper.updateEntityFromDTO(dto, existing);
            // Actualizar tipo documental si viene en el DTO
            if (dto.getDocumentTypeId() != null) {
                documentTypeRepository.findById(dto.getDocumentTypeId())
                        .ifPresent(existing::setDocumentType);
            }
            return documentMapper.toDTO(documentRepository.save(existing));
        });
    }

    public Optional<DocumentDTO> updateStatus(Long id, DocumentStatus newStatus) {
        return documentRepository.findById(id).map(existing -> {
            existing.setStatus(newStatus);
            return documentMapper.toDTO(documentRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Long id) {
        return documentRepository.existsById(id);
    }
}
