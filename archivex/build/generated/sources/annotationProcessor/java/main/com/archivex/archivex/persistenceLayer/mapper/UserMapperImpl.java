package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
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
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setOrganizationId( entityOrganizationId( entity ) );
        userDTO.setOrganizationName( entityOrganizationName( entity ) );
        userDTO.setId( entity.getId() );
        userDTO.setName( entity.getName() );
        userDTO.setEmail( entity.getEmail() );
        userDTO.setRole( entity.getRole() );
        userDTO.setIsActive( entity.getIsActive() );
        userDTO.setCreatedAt( entity.getCreatedAt() );
        userDTO.setUpdatedAt( entity.getUpdatedAt() );

        return userDTO;
    }

    @Override
    public UserEntity toEntity(UserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setId( dto.getId() );
        userEntity.setName( dto.getName() );
        userEntity.setEmail( dto.getEmail() );
        userEntity.setPassword( dto.getPassword() );
        userEntity.setRole( dto.getRole() );
        userEntity.setIsActive( dto.getIsActive() );
        userEntity.setCreatedAt( dto.getCreatedAt() );
        userEntity.setUpdatedAt( dto.getUpdatedAt() );

        return userEntity;
    }

    @Override
    public List<UserDTO> toDTOList(List<UserEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( entities.size() );
        for ( UserEntity userEntity : entities ) {
            list.add( toDTO( userEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(UserDTO dto, UserEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getRole() != null ) {
            entity.setRole( dto.getRole() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
    }

    private Long entityOrganizationId(UserEntity userEntity) {
        OrganizationEntity organization = userEntity.getOrganization();
        if ( organization == null ) {
            return null;
        }
        return organization.getId();
    }

    private String entityOrganizationName(UserEntity userEntity) {
        OrganizationEntity organization = userEntity.getOrganization();
        if ( organization == null ) {
            return null;
        }
        return organization.getName();
    }
}
