package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.EmailTemplateDTO;
import com.archivex.archivex.persistenceLayer.entity.EmailTemplateEntity;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import com.archivex.archivex.persistenceLayer.mapper.EmailTemplateMapper;
import com.archivex.archivex.persistenceLayer.repository.EmailTemplateRepository;
import com.archivex.archivex.persistenceLayer.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailTemplateDAO {

    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateMapper emailTemplateMapper;
    private final OrganizationRepository organizationRepository;

    public EmailTemplateDTO save(EmailTemplateDTO dto) {
        EmailTemplateEntity entity = emailTemplateMapper.toEntity(dto);
        OrganizationEntity org = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada: " + dto.getOrganizationId()));
        entity.setOrganization(org);
        return emailTemplateMapper.toDTO(emailTemplateRepository.save(entity));
    }

    public Optional<EmailTemplateDTO> findById(Long id) {
        return emailTemplateRepository.findById(id).map(emailTemplateMapper::toDTO);
    }

    public List<EmailTemplateDTO> findByOrganizationId(Long organizationId) {
        return emailTemplateMapper.toDTOList(emailTemplateRepository.findByOrganizationId(organizationId));
    }

    /**
     * Busca la plantilla activa para un tipo de evento específico de una organización.
     * Usado por el servicio de notificaciones para obtener la plantilla correcta.
     */
    public Optional<EmailTemplateDTO> findActiveByOrganizationIdAndEventType(Long organizationId, String eventType) {
        return emailTemplateRepository
                .findByOrganizationIdAndEventTypeAndIsActive(organizationId, eventType, true)
                .map(emailTemplateMapper::toDTO);
    }

    public Optional<EmailTemplateDTO> update(Long id, EmailTemplateDTO dto) {
        return emailTemplateRepository.findById(id).map(existing -> {
            emailTemplateMapper.updateEntityFromDTO(dto, existing);
            return emailTemplateMapper.toDTO(emailTemplateRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (emailTemplateRepository.existsById(id)) {
            emailTemplateRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Long id) {
        return emailTemplateRepository.existsById(id);
    }
}
