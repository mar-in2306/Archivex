package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.DocumentTypeDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-10T15:49:42-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DocumentTypeMapperImpl implements DocumentTypeMapper {

    @Override
    public DocumentTypeDTO toDTO(DocumentTypeEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();

        documentTypeDTO.setOrganizationId( entityOrganizationId( entity ) );
        documentTypeDTO.setCreatedAt( entity.getCreatedAt() );
        documentTypeDTO.setDescription( entity.getDescription() );
        documentTypeDTO.setId( entity.getId() );
        documentTypeDTO.setIsActive( entity.getIsActive() );
        documentTypeDTO.setName( entity.getName() );
        documentTypeDTO.setUpdatedAt( entity.getUpdatedAt() );

        return documentTypeDTO;
    }

    @Override
    public DocumentTypeEntity toEntity(DocumentTypeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DocumentTypeEntity documentTypeEntity = new DocumentTypeEntity();

        documentTypeEntity.setCreatedAt( dto.getCreatedAt() );
        documentTypeEntity.setDescription( dto.getDescription() );
        documentTypeEntity.setIsActive( dto.getIsActive() );
        documentTypeEntity.setName( dto.getName() );
        documentTypeEntity.setUpdatedAt( dto.getUpdatedAt() );

        return documentTypeEntity;
    }

    @Override
    public List<DocumentTypeDTO> toDTOList(List<DocumentTypeEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<DocumentTypeDTO> list = new ArrayList<DocumentTypeDTO>( entities.size() );
        for ( DocumentTypeEntity documentTypeEntity : entities ) {
            list.add( toDTO( documentTypeEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(DocumentTypeDTO dto, DocumentTypeEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
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

    private Long entityOrganizationId(DocumentTypeEntity documentTypeEntity) {
        OrganizationEntity organization = documentTypeEntity.getOrganization();
        if ( organization == null ) {
            return null;
        }
        return organization.getId();
    }
}
