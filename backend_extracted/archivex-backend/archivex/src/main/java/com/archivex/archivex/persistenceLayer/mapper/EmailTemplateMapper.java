package com.archivex.archivex.persistenceLayer.mapper;

import com.archivex.archivex.business.dto.EmailTemplateDTO;
import com.archivex.archivex.persistenceLayer.entity.EmailTemplateEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmailTemplateMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    EmailTemplateDTO toDTO(EmailTemplateEntity entity);

    @Mapping(target = "organization", ignore = true)
    EmailTemplateEntity toEntity(EmailTemplateDTO dto);

    List<EmailTemplateDTO> toDTOList(List<EmailTemplateEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    void updateEntityFromDTO(EmailTemplateDTO dto, @MappingTarget EmailTemplateEntity entity);
}
