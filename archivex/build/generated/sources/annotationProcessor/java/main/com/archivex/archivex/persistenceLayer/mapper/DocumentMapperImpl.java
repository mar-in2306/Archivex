package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.DocumentDTO;
import com.archivex.archivex.persistenceLayer.entity.DocumentEntity;
import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-09T21:23:26-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.0.0.jar, environment: Java 24.0.2 (Eclipse Adoptium)"
)
@Component
public class DocumentMapperImpl implements DocumentMapper {

    @Override
    public DocumentDTO toDTO(DocumentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DocumentDTO documentDTO = new DocumentDTO();

        documentDTO.setOrganizationId( entityOrganizationId( entity ) );
        documentDTO.setDocumentTypeId( entityDocumentTypeId( entity ) );
        documentDTO.setDocumentTypeName( entityDocumentTypeName( entity ) );
        documentDTO.setCreatedById( entityCreatedById( entity ) );
        documentDTO.setCreatedByName( entityCreatedByName( entity ) );
        documentDTO.setId( entity.getId() );
        documentDTO.setTitle( entity.getTitle() );
        documentDTO.setDescription( entity.getDescription() );
        documentDTO.setStatus( entity.getStatus() );
        documentDTO.setFilePath( entity.getFilePath() );
        documentDTO.setFileName( entity.getFileName() );
        documentDTO.setFileType( entity.getFileType() );
        documentDTO.setFileSize( entity.getFileSize() );
        documentDTO.setCreatedAt( entity.getCreatedAt() );
        documentDTO.setUpdatedAt( entity.getUpdatedAt() );

        return documentDTO;
    }

    @Override
    public DocumentEntity toEntity(DocumentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();

        documentEntity.setId( dto.getId() );
        documentEntity.setTitle( dto.getTitle() );
        documentEntity.setDescription( dto.getDescription() );
        documentEntity.setStatus( dto.getStatus() );
        documentEntity.setFilePath( dto.getFilePath() );
        documentEntity.setFileName( dto.getFileName() );
        documentEntity.setFileType( dto.getFileType() );
        documentEntity.setFileSize( dto.getFileSize() );
        documentEntity.setCreatedAt( dto.getCreatedAt() );
        documentEntity.setUpdatedAt( dto.getUpdatedAt() );

        return documentEntity;
    }

    @Override
    public List<DocumentDTO> toDTOList(List<DocumentEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<DocumentDTO> list = new ArrayList<DocumentDTO>( entities.size() );
        for ( DocumentEntity documentEntity : entities ) {
            list.add( toDTO( documentEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(DocumentDTO dto, DocumentEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getTitle() != null ) {
            entity.setTitle( dto.getTitle() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getFilePath() != null ) {
            entity.setFilePath( dto.getFilePath() );
        }
        if ( dto.getFileName() != null ) {
            entity.setFileName( dto.getFileName() );
        }
        if ( dto.getFileType() != null ) {
            entity.setFileType( dto.getFileType() );
        }
        if ( dto.getFileSize() != null ) {
            entity.setFileSize( dto.getFileSize() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
    }

    private Long entityOrganizationId(DocumentEntity documentEntity) {
        OrganizationEntity organization = documentEntity.getOrganization();
        if ( organization == null ) {
            return null;
        }
        return organization.getId();
    }

    private Long entityDocumentTypeId(DocumentEntity documentEntity) {
        DocumentTypeEntity documentType = documentEntity.getDocumentType();
        if ( documentType == null ) {
            return null;
        }
        return documentType.getId();
    }

    private String entityDocumentTypeName(DocumentEntity documentEntity) {
        DocumentTypeEntity documentType = documentEntity.getDocumentType();
        if ( documentType == null ) {
            return null;
        }
        return documentType.getName();
    }

    private Long entityCreatedById(DocumentEntity documentEntity) {
        UserEntity createdBy = documentEntity.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        return createdBy.getId();
    }

    private String entityCreatedByName(DocumentEntity documentEntity) {
        UserEntity createdBy = documentEntity.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        return createdBy.getName();
    }
}
