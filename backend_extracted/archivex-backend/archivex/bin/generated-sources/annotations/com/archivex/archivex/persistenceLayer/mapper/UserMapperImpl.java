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
    date = "2026-04-24T15:52:13-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        userDTO.setCreatedAt( entity.getCreatedAt() );
        userDTO.setEmail( entity.getEmail() );
        userDTO.setId( entity.getId() );
        userDTO.setIsActive( entity.getIsActive() );
        userDTO.setName( entity.getName() );
        userDTO.setRole( entity.getRole() );
        userDTO.setUpdatedAt( entity.getUpdatedAt() );

        return userDTO;
    }

    @Override
    public UserEntity toEntity(UserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setCreatedAt( dto.getCreatedAt() );
        userEntity.setEmail( dto.getEmail() );
        userEntity.setId( dto.getId() );
        userEntity.setIsActive( dto.getIsActive() );
        userEntity.setName( dto.getName() );
        userEntity.setPassword( dto.getPassword() );
        userEntity.setRole( dto.getRole() );
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

        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getRole() != null ) {
            entity.setRole( dto.getRole() );
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
