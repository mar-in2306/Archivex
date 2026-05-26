package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.WorkflowStepDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import com.archivex.archivex.persistenceLayer.entity.WorkflowStepEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T15:52:13-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class WorkflowStepMapperImpl implements WorkflowStepMapper {

    @Override
    public WorkflowStepDTO toDTO(WorkflowStepEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WorkflowStepDTO workflowStepDTO = new WorkflowStepDTO();

        workflowStepDTO.setDocumentTypeId( entityDocumentTypeId( entity ) );
        workflowStepDTO.setDocumentTypeName( entityDocumentTypeName( entity ) );
        workflowStepDTO.setCreatedAt( entity.getCreatedAt() );
        workflowStepDTO.setDescription( entity.getDescription() );
        workflowStepDTO.setId( entity.getId() );
        workflowStepDTO.setIsActive( entity.getIsActive() );
        workflowStepDTO.setName( entity.getName() );
        workflowStepDTO.setStepOrder( entity.getStepOrder() );
        workflowStepDTO.setUpdatedAt( entity.getUpdatedAt() );

        return workflowStepDTO;
    }

    @Override
    public WorkflowStepEntity toEntity(WorkflowStepDTO dto) {
        if ( dto == null ) {
            return null;
        }

        WorkflowStepEntity workflowStepEntity = new WorkflowStepEntity();

        workflowStepEntity.setCreatedAt( dto.getCreatedAt() );
        workflowStepEntity.setDescription( dto.getDescription() );
        workflowStepEntity.setId( dto.getId() );
        workflowStepEntity.setIsActive( dto.getIsActive() );
        workflowStepEntity.setName( dto.getName() );
        workflowStepEntity.setStepOrder( dto.getStepOrder() );
        workflowStepEntity.setUpdatedAt( dto.getUpdatedAt() );

        return workflowStepEntity;
    }

    @Override
    public List<WorkflowStepDTO> toDTOList(List<WorkflowStepEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<WorkflowStepDTO> list = new ArrayList<WorkflowStepDTO>( entities.size() );
        for ( WorkflowStepEntity workflowStepEntity : entities ) {
            list.add( toDTO( workflowStepEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(WorkflowStepDTO dto, WorkflowStepEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getStepOrder() != null ) {
            entity.setStepOrder( dto.getStepOrder() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
    }

    private Long entityDocumentTypeId(WorkflowStepEntity workflowStepEntity) {
        DocumentTypeEntity documentType = workflowStepEntity.getDocumentType();
        if ( documentType == null ) {
            return null;
        }
        return documentType.getId();
    }

    private String entityDocumentTypeName(WorkflowStepEntity workflowStepEntity) {
        DocumentTypeEntity documentType = workflowStepEntity.getDocumentType();
        if ( documentType == null ) {
            return null;
        }
        return documentType.getName();
    }
}
