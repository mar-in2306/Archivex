package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.EmailTemplateDTO;
import com.archivex.archivex.persistenceLayer.entity.EmailTemplateEntity;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
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
public class EmailTemplateMapperImpl implements EmailTemplateMapper {

    @Override
    public EmailTemplateDTO toDTO(EmailTemplateEntity entity) {
        if ( entity == null ) {
            return null;
        }

        EmailTemplateDTO emailTemplateDTO = new EmailTemplateDTO();

        emailTemplateDTO.setOrganizationId( entityOrganizationId( entity ) );
        emailTemplateDTO.setBodyHtml( entity.getBodyHtml() );
        emailTemplateDTO.setCreatedAt( entity.getCreatedAt() );
        emailTemplateDTO.setEventType( entity.getEventType() );
        emailTemplateDTO.setId( entity.getId() );
        emailTemplateDTO.setIsActive( entity.getIsActive() );
        emailTemplateDTO.setSubject( entity.getSubject() );
        emailTemplateDTO.setUpdatedAt( entity.getUpdatedAt() );

        return emailTemplateDTO;
    }

    @Override
    public EmailTemplateEntity toEntity(EmailTemplateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        EmailTemplateEntity emailTemplateEntity = new EmailTemplateEntity();

        emailTemplateEntity.setBodyHtml( dto.getBodyHtml() );
        emailTemplateEntity.setCreatedAt( dto.getCreatedAt() );
        emailTemplateEntity.setEventType( dto.getEventType() );
        emailTemplateEntity.setId( dto.getId() );
        emailTemplateEntity.setIsActive( dto.getIsActive() );
        emailTemplateEntity.setSubject( dto.getSubject() );
        emailTemplateEntity.setUpdatedAt( dto.getUpdatedAt() );

        return emailTemplateEntity;
    }

    @Override
    public List<EmailTemplateDTO> toDTOList(List<EmailTemplateEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<EmailTemplateDTO> list = new ArrayList<EmailTemplateDTO>( entities.size() );
        for ( EmailTemplateEntity emailTemplateEntity : entities ) {
            list.add( toDTO( emailTemplateEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(EmailTemplateDTO dto, EmailTemplateEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getBodyHtml() != null ) {
            entity.setBodyHtml( dto.getBodyHtml() );
        }
        if ( dto.getEventType() != null ) {
            entity.setEventType( dto.getEventType() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getSubject() != null ) {
            entity.setSubject( dto.getSubject() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
    }

    private Long entityOrganizationId(EmailTemplateEntity emailTemplateEntity) {
        OrganizationEntity organization = emailTemplateEntity.getOrganization();
        if ( organization == null ) {
            return null;
        }
        return organization.getId();
    }
}
