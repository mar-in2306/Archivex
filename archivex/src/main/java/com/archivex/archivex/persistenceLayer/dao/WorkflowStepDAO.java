package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.WorkflowStepDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import com.archivex.archivex.persistenceLayer.entity.WorkflowStepEntity;
import com.archivex.archivex.persistenceLayer.mapper.WorkflowStepMapper;
import com.archivex.archivex.persistenceLayer.repository.DocumentTypeRepository;
import com.archivex.archivex.persistenceLayer.repository.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkflowStepDAO {

    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowStepMapper workflowStepMapper;
    private final DocumentTypeRepository documentTypeRepository;

    public WorkflowStepDTO save(WorkflowStepDTO dto) {
        WorkflowStepEntity entity = workflowStepMapper.toEntity(dto);
        DocumentTypeEntity type = documentTypeRepository.findById(dto.getDocumentTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo documental no encontrado: " + dto.getDocumentTypeId()));
        entity.setDocumentType(type);
        return workflowStepMapper.toDTO(workflowStepRepository.save(entity));
    }

    public Optional<WorkflowStepDTO> findById(Long id) {
        return workflowStepRepository.findById(id).map(workflowStepMapper::toDTO);
    }

    public List<WorkflowStepDTO> findByDocumentTypeId(Long documentTypeId) {
        return workflowStepMapper.toDTOList(
                workflowStepRepository.findByDocumentTypeIdOrderByStepOrderAsc(documentTypeId));
    }

    public List<WorkflowStepDTO> findActiveByDocumentTypeId(Long documentTypeId) {
        return workflowStepMapper.toDTOList(
                workflowStepRepository.findByDocumentTypeIdAndIsActiveOrderByStepOrderAsc(documentTypeId, true));
    }

    public Optional<WorkflowStepDTO> update(Long id, WorkflowStepDTO dto) {
        return workflowStepRepository.findById(id).map(existing -> {
            workflowStepMapper.updateEntityFromDTO(dto, existing);
            return workflowStepMapper.toDTO(workflowStepRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (workflowStepRepository.existsById(id)) {
            workflowStepRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Long id) {
        return workflowStepRepository.existsById(id);
    }
}
