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
    date = "2026-04-09T21:23:26-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.0.0.jar, environment: Java 24.0.2 (Eclipse Adoptium)"
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
        emailTemplateDTO.setId( entity.getId() );
        emailTemplateDTO.setEventType( entity.getEventType() );
        emailTemplateDTO.setSubject( entity.getSubject() );
        emailTemplateDTO.setBodyHtml( entity.getBodyHtml() );
        emailTemplateDTO.setIsActive( entity.getIsActive() );
        emailTemplateDTO.setCreatedAt( entity.getCreatedAt() );
        emailTemplateDTO.setUpdatedAt( entity.getUpdatedAt() );

        return emailTemplateDTO;
    }

    @Override
    public EmailTemplateEntity toEntity(EmailTemplateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        EmailTemplateEntity emailTemplateEntity = new EmailTemplateEntity();

        emailTemplateEntity.setId( dto.getId() );
        emailTemplateEntity.setEventType( dto.getEventType() );
        emailTemplateEntity.setSubject( dto.getSubject() );
        emailTemplateEntity.setBodyHtml( dto.getBodyHtml() );
        emailTemplateEntity.setIsActive( dto.getIsActive() );
        emailTemplateEntity.setCreatedAt( dto.getCreatedAt() );
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

        if ( dto.getEventType() != null ) {
            entity.setEventType( dto.getEventType() );
        }
        if ( dto.getSubject() != null ) {
            entity.setSubject( dto.getSubject() );
        }
        if ( dto.getBodyHtml() != null ) {
            entity.setBodyHtml( dto.getBodyHtml() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
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
