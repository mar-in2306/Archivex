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
    date = "2026-05-26T01:54:39-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.0.0.jar, environment: Java 21.0.2 (Oracle Corporation)"
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
        documentTypeDTO.setId( entity.getId() );
        documentTypeDTO.setName( entity.getName() );
        documentTypeDTO.setDescription( entity.getDescription() );
        documentTypeDTO.setIsActive( entity.getIsActive() );
        documentTypeDTO.setCreatedAt( entity.getCreatedAt() );
        documentTypeDTO.setUpdatedAt( entity.getUpdatedAt() );

        return documentTypeDTO;
    }

    @Override
    public DocumentTypeEntity toEntity(DocumentTypeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DocumentTypeEntity documentTypeEntity = new DocumentTypeEntity();

        documentTypeEntity.setName( dto.getName() );
        documentTypeEntity.setDescription( dto.getDescription() );
        documentTypeEntity.setIsActive( dto.getIsActive() );
        documentTypeEntity.setCreatedAt( dto.getCreatedAt() );
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

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
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
