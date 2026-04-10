package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.DocumentTypeDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentTypeMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    DocumentTypeDTO toDTO(DocumentTypeEntity entity);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "workflowSteps", ignore = true)
    DocumentTypeEntity toEntity(DocumentTypeDTO dto);

    List<DocumentTypeDTO> toDTOList(List<DocumentTypeEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "workflowSteps", ignore = true)
    void updateEntityFromDTO(DocumentTypeDTO dto, @MappingTarget DocumentTypeEntity entity);
}
