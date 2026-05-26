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
    date = "2026-04-24T15:52:13-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        auditLogDTO.setAction( entity.getAction() );
        auditLogDTO.setDetails( entity.getDetails() );
        auditLogDTO.setId( entity.getId() );
        auditLogDTO.setIpAddress( entity.getIpAddress() );
        auditLogDTO.setNewStatus( entity.getNewStatus() );
        auditLogDTO.setPerformedAt( entity.getPerformedAt() );
        auditLogDTO.setPreviousStatus( entity.getPreviousStatus() );

        return auditLogDTO;
    }

    @Override
    public AuditLogEntity toEntity(AuditLogDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AuditLogEntity auditLogEntity = new AuditLogEntity();

        auditLogEntity.setAction( dto.getAction() );
        auditLogEntity.setDetails( dto.getDetails() );
        auditLogEntity.setId( dto.getId() );
        auditLogEntity.setIpAddress( dto.getIpAddress() );
        auditLogEntity.setNewStatus( dto.getNewStatus() );
        auditLogEntity.setPerformedAt( dto.getPerformedAt() );
        auditLogEntity.setPreviousStatus( dto.getPreviousStatus() );

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
