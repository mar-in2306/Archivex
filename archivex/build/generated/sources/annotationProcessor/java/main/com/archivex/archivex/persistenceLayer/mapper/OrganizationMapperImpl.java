package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
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
public class OrganizationMapperImpl implements OrganizationMapper {

    @Override
    public OrganizationDTO toDTO(OrganizationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        OrganizationDTO organizationDTO = new OrganizationDTO();

        organizationDTO.setId( entity.getId() );
        organizationDTO.setName( entity.getName() );
        organizationDTO.setDomain( entity.getDomain() );
        organizationDTO.setDescription( entity.getDescription() );
        organizationDTO.setContactEmail( entity.getContactEmail() );
        organizationDTO.setIsActive( entity.getIsActive() );
        organizationDTO.setCreatedAt( entity.getCreatedAt() );
        organizationDTO.setUpdatedAt( entity.getUpdatedAt() );

        return organizationDTO;
    }

    @Override
    public OrganizationEntity toEntity(OrganizationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        OrganizationEntity organizationEntity = new OrganizationEntity();

        organizationEntity.setId( dto.getId() );
        organizationEntity.setName( dto.getName() );
        organizationEntity.setDomain( dto.getDomain() );
        organizationEntity.setDescription( dto.getDescription() );
        organizationEntity.setContactEmail( dto.getContactEmail() );
        organizationEntity.setIsActive( dto.getIsActive() );
        organizationEntity.setCreatedAt( dto.getCreatedAt() );
        organizationEntity.setUpdatedAt( dto.getUpdatedAt() );

        return organizationEntity;
    }

    @Override
    public List<OrganizationDTO> toDTOList(List<OrganizationEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<OrganizationDTO> list = new ArrayList<OrganizationDTO>( entities.size() );
        for ( OrganizationEntity organizationEntity : entities ) {
            list.add( toDTO( organizationEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(OrganizationDTO dto, OrganizationEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getDomain() != null ) {
            entity.setDomain( dto.getDomain() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getContactEmail() != null ) {
            entity.setContactEmail( dto.getContactEmail() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
    }
}
