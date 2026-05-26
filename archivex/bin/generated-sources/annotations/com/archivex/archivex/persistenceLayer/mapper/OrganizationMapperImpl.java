package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-10T15:49:43-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class OrganizationMapperImpl implements OrganizationMapper {

    @Override
    public OrganizationDTO toDTO(OrganizationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        OrganizationDTO organizationDTO = new OrganizationDTO();

        organizationDTO.setContactEmail( entity.getContactEmail() );
        organizationDTO.setCreatedAt( entity.getCreatedAt() );
        organizationDTO.setDescription( entity.getDescription() );
        organizationDTO.setDomain( entity.getDomain() );
        organizationDTO.setId( entity.getId() );
        organizationDTO.setIsActive( entity.getIsActive() );
        organizationDTO.setName( entity.getName() );
        organizationDTO.setUpdatedAt( entity.getUpdatedAt() );

        return organizationDTO;
    }

    @Override
    public OrganizationEntity toEntity(OrganizationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        OrganizationEntity organizationEntity = new OrganizationEntity();

        organizationEntity.setContactEmail( dto.getContactEmail() );
        organizationEntity.setCreatedAt( dto.getCreatedAt() );
        organizationEntity.setDescription( dto.getDescription() );
        organizationEntity.setDomain( dto.getDomain() );
        organizationEntity.setId( dto.getId() );
        organizationEntity.setIsActive( dto.getIsActive() );
        organizationEntity.setName( dto.getName() );
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

        if ( dto.getContactEmail() != null ) {
            entity.setContactEmail( dto.getContactEmail() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getDomain() != null ) {
            entity.setDomain( dto.getDomain() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
    }
}
