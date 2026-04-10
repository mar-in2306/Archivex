package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "organizationName", source = "organization.name")
    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(UserEntity entity);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "workflowTasks", ignore = true)
    UserEntity toEntity(UserDTO dto);

    List<UserDTO> toDTOList(List<UserEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "workflowTasks", ignore = true)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget UserEntity entity);
}
