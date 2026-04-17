package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.DocumentWorkflowDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentEntity;
import com.archivex.archivex.persistenceLayer.entity.DocumentWorkflowEntity;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import com.archivex.archivex.persistenceLayer.entity.WorkflowStepEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T22:33:58-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.0.0.jar, environment: Java 24.0.2 (Eclipse Adoptium)"
)
@Component
public class DocumentWorkflowMapperImpl implements DocumentWorkflowMapper {

    @Override
    public DocumentWorkflowDTO toDTO(DocumentWorkflowEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DocumentWorkflowDTO documentWorkflowDTO = new DocumentWorkflowDTO();

        documentWorkflowDTO.setDocumentId( entityDocumentId( entity ) );
        documentWorkflowDTO.setDocumentTitle( entityDocumentTitle( entity ) );
        documentWorkflowDTO.setWorkflowStepId( entityWorkflowStepId( entity ) );
        documentWorkflowDTO.setWorkflowStepName( entityWorkflowStepName( entity ) );
        documentWorkflowDTO.setStepOrder( entityWorkflowStepStepOrder( entity ) );
        documentWorkflowDTO.setAssignedToId( entityAssignedToId( entity ) );
        documentWorkflowDTO.setAssignedToName( entityAssignedToName( entity ) );
        documentWorkflowDTO.setId( entity.getId() );
        documentWorkflowDTO.setStatus( entity.getStatus() );
        documentWorkflowDTO.setComments( entity.getComments() );
        documentWorkflowDTO.setCompletedAt( entity.getCompletedAt() );
        documentWorkflowDTO.setCreatedAt( entity.getCreatedAt() );
        documentWorkflowDTO.setUpdatedAt( entity.getUpdatedAt() );

        return documentWorkflowDTO;
    }

    @Override
    public DocumentWorkflowEntity toEntity(DocumentWorkflowDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DocumentWorkflowEntity documentWorkflowEntity = new DocumentWorkflowEntity();

        documentWorkflowEntity.setId( dto.getId() );
        documentWorkflowEntity.setStatus( dto.getStatus() );
        documentWorkflowEntity.setComments( dto.getComments() );
        documentWorkflowEntity.setCompletedAt( dto.getCompletedAt() );
        documentWorkflowEntity.setCreatedAt( dto.getCreatedAt() );
        documentWorkflowEntity.setUpdatedAt( dto.getUpdatedAt() );

        return documentWorkflowEntity;
    }

    @Override
    public List<DocumentWorkflowDTO> toDTOList(List<DocumentWorkflowEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<DocumentWorkflowDTO> list = new ArrayList<DocumentWorkflowDTO>( entities.size() );
        for ( DocumentWorkflowEntity documentWorkflowEntity : entities ) {
            list.add( toDTO( documentWorkflowEntity ) );
        }

        return list;
    }

    private Long entityDocumentId(DocumentWorkflowEntity documentWorkflowEntity) {
        DocumentEntity document = documentWorkflowEntity.getDocument();
        if ( document == null ) {
            return null;
        }
        return document.getId();
    }

    private String entityDocumentTitle(DocumentWorkflowEntity documentWorkflowEntity) {
        DocumentEntity document = documentWorkflowEntity.getDocument();
        if ( document == null ) {
            return null;
        }
        return document.getTitle();
    }

    private Long entityWorkflowStepId(DocumentWorkflowEntity documentWorkflowEntity) {
        WorkflowStepEntity workflowStep = documentWorkflowEntity.getWorkflowStep();
        if ( workflowStep == null ) {
            return null;
        }
        return workflowStep.getId();
    }

    private String entityWorkflowStepName(DocumentWorkflowEntity documentWorkflowEntity) {
        WorkflowStepEntity workflowStep = documentWorkflowEntity.getWorkflowStep();
        if ( workflowStep == null ) {
            return null;
        }
        return workflowStep.getName();
    }

    private Integer entityWorkflowStepStepOrder(DocumentWorkflowEntity documentWorkflowEntity) {
        WorkflowStepEntity workflowStep = documentWorkflowEntity.getWorkflowStep();
        if ( workflowStep == null ) {
            return null;
        }
        return workflowStep.getStepOrder();
    }

    private Long entityAssignedToId(DocumentWorkflowEntity documentWorkflowEntity) {
        UserEntity assignedTo = documentWorkflowEntity.getAssignedTo();
        if ( assignedTo == null ) {
            return null;
        }
        return assignedTo.getId();
    }

    private String entityAssignedToName(DocumentWorkflowEntity documentWorkflowEntity) {
        UserEntity assignedTo = documentWorkflowEntity.getAssignedTo();
        if ( assignedTo == null ) {
            return null;
        }
        return assignedTo.getName();
    }
}
