package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationMapper {

    OrganizationDTO toDTO(OrganizationEntity entity);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "documentTypes", ignore = true)
    @Mapping(target = "documents", ignore = true)
    OrganizationEntity toEntity(OrganizationDTO dto);

    List<OrganizationDTO> toDTOList(List<OrganizationEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "documentTypes", ignore = true)
    @Mapping(target = "documents", ignore = true)
    void updateEntityFromDTO(OrganizationDTO dto, @MappingTarget OrganizationEntity entity);
}
