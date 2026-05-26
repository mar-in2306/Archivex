package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.DocumentWorkflowDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentWorkflowEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentWorkflowMapper {

    @Mapping(target = "documentId", source = "document.id")
    @Mapping(target = "documentTitle", source = "document.title")
    @Mapping(target = "workflowStepId", source = "workflowStep.id")
    @Mapping(target = "workflowStepName", source = "workflowStep.name")
    @Mapping(target = "stepOrder", source = "workflowStep.stepOrder")
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", source = "assignedTo.name")
    DocumentWorkflowDTO toDTO(DocumentWorkflowEntity entity);

    @Mapping(target = "document", ignore = true)
    @Mapping(target = "workflowStep", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    DocumentWorkflowEntity toEntity(DocumentWorkflowDTO dto);

    List<DocumentWorkflowDTO> toDTOList(List<DocumentWorkflowEntity> entities);
}
