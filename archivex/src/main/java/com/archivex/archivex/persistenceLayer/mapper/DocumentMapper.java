package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.DocumentDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "documentTypeId", source = "documentType.id")
    @Mapping(target = "documentTypeName", source = "documentType.name")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.name")
    DocumentDTO toDTO(DocumentEntity entity);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "auditLogs", ignore = true)
    @Mapping(target = "workflowTasks", ignore = true)
    DocumentEntity toEntity(DocumentDTO dto);

    List<DocumentDTO> toDTOList(List<DocumentEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "auditLogs", ignore = true)
    @Mapping(target = "workflowTasks", ignore = true)
    void updateEntityFromDTO(DocumentDTO dto, @MappingTarget DocumentEntity entity);
}
