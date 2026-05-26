package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.AuditLogDTO;
import com.archivex.archivex.persistenceLayer.entity.AuditLogEntity;
import com.archivex.archivex.persistenceLayer.entity.DocumentEntity;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
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
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLogDTO toDTO(AuditLogEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AuditLogDTO auditLogDTO = new AuditLogDTO();

        auditLogDTO.setDocumentId( entityDocumentId( entity ) );
        auditLogDTO.setDocumentTitle( entityDocumentTitle( entity ) );
        auditLogDTO.setPerformedById( entityPerformedById( entity ) );
        auditLogDTO.setPerformedByName( entityPerformedByName( entity ) );
        auditLogDTO.setId( entity.getId() );
        auditLogDTO.setAction( entity.getAction() );
        auditLogDTO.setDetails( entity.getDetails() );
        auditLogDTO.setPreviousStatus( entity.getPreviousStatus() );
        auditLogDTO.setNewStatus( entity.getNewStatus() );
        auditLogDTO.setIpAddress( entity.getIpAddress() );
        auditLogDTO.setPerformedAt( entity.getPerformedAt() );

        return auditLogDTO;
    }

    @Override
    public AuditLogEntity toEntity(AuditLogDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AuditLogEntity auditLogEntity = new AuditLogEntity();

        auditLogEntity.setId( dto.getId() );
        auditLogEntity.setAction( dto.getAction() );
        auditLogEntity.setDetails( dto.getDetails() );
        auditLogEntity.setPreviousStatus( dto.getPreviousStatus() );
        auditLogEntity.setNewStatus( dto.getNewStatus() );
        auditLogEntity.setIpAddress( dto.getIpAddress() );
        auditLogEntity.setPerformedAt( dto.getPerformedAt() );

        return auditLogEntity;
    }

    @Override
    public List<AuditLogDTO> toDTOList(List<AuditLogEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AuditLogDTO> list = new ArrayList<AuditLogDTO>( entities.size() );
        for ( AuditLogEntity auditLogEntity : entities ) {
            list.add( toDTO( auditLogEntity ) );
        }

        return list;
    }

    private Long entityDocumentId(AuditLogEntity auditLogEntity) {
        DocumentEntity document = auditLogEntity.getDocument();
        if ( document == null ) {
            return null;
        }
        return document.getId();
    }

    private String entityDocumentTitle(AuditLogEntity auditLogEntity) {
        DocumentEntity document = auditLogEntity.getDocument();
        if ( document == null ) {
            return null;
        }
        return document.getTitle();
    }

    private Long entityPerformedById(AuditLogEntity auditLogEntity) {
        UserEntity performedBy = auditLogEntity.getPerformedBy();
        if ( performedBy == null ) {
            return null;
        }
        return performedBy.getId();
    }

    private String entityPerformedByName(AuditLogEntity auditLogEntity) {
        UserEntity performedBy = auditLogEntity.getPerformedBy();
        if ( performedBy == null ) {
            return null;
        }
        return performedBy.getName();
    }
}
