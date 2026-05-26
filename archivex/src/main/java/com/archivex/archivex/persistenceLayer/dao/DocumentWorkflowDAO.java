package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.DocumentWorkflowDTO;
import com.archivex.archivex.business.enums.WorkflowTaskStatus;
import com.archivex.archivex.persistenceLayer.entity.DocumentWorkflowEntity;
import com.archivex.archivex.persistenceLayer.mapper.DocumentWorkflowMapper;
import com.archivex.archivex.persistenceLayer.repository.DocumentRepository;
import com.archivex.archivex.persistenceLayer.repository.DocumentWorkflowRepository;
import com.archivex.archivex.persistenceLayer.repository.UserRepository;
import com.archivex.archivex.persistenceLayer.repository.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DocumentWorkflowDAO {

    private final DocumentWorkflowRepository documentWorkflowRepository;
    private final DocumentWorkflowMapper documentWorkflowMapper;
    private final DocumentRepository documentRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final UserRepository userRepository;

    public DocumentWorkflowDTO save(DocumentWorkflowDTO dto) {
        DocumentWorkflowEntity entity = documentWorkflowMapper.toEntity(dto);

        entity.setDocument(documentRepository.findById(dto.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + dto.getDocumentId())));
        entity.setWorkflowStep(workflowStepRepository.findById(dto.getWorkflowStepId())
                .orElseThrow(() -> new RuntimeException("Paso de flujo no encontrado: " + dto.getWorkflowStepId())));
        entity.setAssignedTo(userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getAssignedToId())));

        return documentWorkflowMapper.toDTO(documentWorkflowRepository.save(entity));
    }

    public Optional<DocumentWorkflowDTO> findById(Long id) {
        return documentWorkflowRepository.findById(id).map(documentWorkflowMapper::toDTO);
    }

    public List<DocumentWorkflowDTO> findByDocumentId(Long documentId) {
        return documentWorkflowMapper.toDTOList(documentWorkflowRepository.findByDocumentId(documentId));
    }

    public List<DocumentWorkflowDTO> findByAssignedToId(Long userId) {
        return documentWorkflowMapper.toDTOList(documentWorkflowRepository.findByAssignedToId(userId));
    }

    public List<DocumentWorkflowDTO> findPendingByUserId(Long userId) {
        return documentWorkflowMapper.toDTOList(
                documentWorkflowRepository.findByAssignedToIdAndStatus(userId, WorkflowTaskStatus.PENDING));
    }

    public Optional<DocumentWorkflowDTO> updateTaskStatus(Long id, WorkflowTaskStatus newStatus, String comments) {
        return documentWorkflowRepository.findById(id).map(existing -> {
            existing.setStatus(newStatus);
            existing.setComments(comments);
            if (newStatus == WorkflowTaskStatus.COMPLETED || newStatus == WorkflowTaskStatus.REJECTED) {
                existing.setCompletedAt(LocalDateTime.now());
            }
            return documentWorkflowMapper.toDTO(documentWorkflowRepository.save(existing));
        });
    }

    public boolean existsById(Long id) {
        return documentWorkflowRepository.existsById(id);
    }
}
