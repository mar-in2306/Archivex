package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.AuditLogDTO;
import com.archivex.archivex.persistenceLayer.entity.AuditLogEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "documentId", source = "document.id")
    @Mapping(target = "documentTitle", source = "document.title")
    @Mapping(target = "performedById", source = "performedBy.id")
    @Mapping(target = "performedByName", source = "performedBy.name")
    AuditLogDTO toDTO(AuditLogEntity entity);

    @Mapping(target = "document", ignore = true)
    @Mapping(target = "performedBy", ignore = true)
    AuditLogEntity toEntity(AuditLogDTO dto);

    List<AuditLogDTO> toDTOList(List<AuditLogEntity> entities);
}
