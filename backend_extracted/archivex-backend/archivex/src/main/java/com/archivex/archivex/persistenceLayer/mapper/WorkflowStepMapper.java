package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.WorkflowStepDTO;
import com.archivex.archivex.persistenceLayer.entity.WorkflowStepEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkflowStepMapper {

    @Mapping(target = "documentTypeId", source = "documentType.id")
    @Mapping(target = "documentTypeName", source = "documentType.name")
    WorkflowStepDTO toDTO(WorkflowStepEntity entity);

    @Mapping(target = "documentType", ignore = true)
    WorkflowStepEntity toEntity(WorkflowStepDTO dto);

    List<WorkflowStepDTO> toDTOList(List<WorkflowStepEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    void updateEntityFromDTO(WorkflowStepDTO dto, @MappingTarget WorkflowStepEntity entity);
}
