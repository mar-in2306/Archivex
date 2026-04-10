package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.EmailTemplateDTO;
import com.archivex.archivex.business.service.EmailTemplateService;
import com.archivex.archivex.persistenceLayer.dao.EmailTemplateDAO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateDAO emailTemplateDAO;
    private final JavaMailSender mailSender;

    @Override
    public EmailTemplateDTO createTemplate(EmailTemplateDTO dto) {
        log.info("Creando plantilla de email — evento: {} | org: {}", dto.getEventType(), dto.getOrganizationId());
        validateTemplateData(dto);
        dto.setIsActive(true);
        EmailTemplateDTO result = emailTemplateDAO.save(dto);
        log.info("Plantilla creada con ID: {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EmailTemplateDTO getTemplateById(Long id) {
        return emailTemplateDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla de email no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> getTemplatesByOrganization(Long organizationId) {
        log.debug("Listando plantillas de la organización ID: {}", organizationId);
        return emailTemplateDAO.findByOrganizationId(organizationId);
    }

    @Override
    public EmailTemplateDTO updateTemplate(Long id, EmailTemplateDTO dto) {
        log.info("Actualizando plantilla ID: {}", id);
        getTemplateById(id);
        return emailTemplateDAO.update(id, dto)
                .orElseThrow(() -> new RuntimeException("Error al actualizar plantilla ID: " + id));
    }

    @Override
    public void deleteTemplate(Long id) {
        log.info("Eliminando plantilla ID: {}", id);
        getTemplateById(id);
        emailTemplateDAO.deleteById(id);
        log.info("Plantilla eliminada ID: {}", id);
    }

    /**
     * Envía un correo usando la plantilla activa para el eventType de la organización.
     *
     * Reemplaza variables en el cuerpo y asunto con el formato {{variable}}.
     * Ejemplo de variables: {"documentTitle": "Contrato ABC", "userName": "María"}
     *
     * Si no existe plantilla activa para el evento, solo registra un aviso y no falla.
     * La ejecución es asíncrona para no bloquear el hilo principal.
     */
    @Override
    @Async
    public void sendNotification(Long organizationId, String eventType,
                                  String recipientEmail, Map<String, String> variables) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.warn("sendNotification: recipientEmail vacío para evento '{}', se omite.", eventType);
            return;
        }

        emailTemplateDAO.findActiveByOrganizationIdAndEventType(organizationId, eventType)
                .ifPresentOrElse(template -> {
                    try {
                        String subject = replacePlaceholders(template.getSubject(), variables);
                        String body    = replacePlaceholders(template.getBodyHtml(), variables);

                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                        helper.setTo(recipientEmail);
                        helper.setSubject(subject);
                        helper.setText(body, true); // true = HTML

                        mailSender.send(message);
                        log.info("Correo '{}' enviado a: {}", eventType, recipientEmail);

                    } catch (Exception e) {
                        log.error("Error al enviar correo '{}' a {}: {}", eventType, recipientEmail, e.getMessage());
                    }
                }, () -> log.debug("No hay plantilla activa para evento '{}' en org: {}", eventType, organizationId));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Reemplaza marcadores {{key}} en el texto con los valores del mapa.
     */
    private String replacePlaceholders(String template, Map<String, String> variables) {
        if (template == null || variables == null) return template;
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }

    private void validateTemplateData(EmailTemplateDTO dto) {
        if (dto.getEventType() == null || dto.getEventType().isBlank()) {
            throw new IllegalArgumentException("El tipo de evento es obligatorio");
        }
        if (dto.getSubject() == null || dto.getSubject().isBlank()) {
            throw new IllegalArgumentException("El asunto de la plantilla es obligatorio");
        }
        if (dto.getBodyHtml() == null || dto.getBodyHtml().isBlank()) {
            throw new IllegalArgumentException("El cuerpo HTML de la plantilla es obligatorio");
        }
        if (dto.getOrganizationId() == null) {
            throw new IllegalArgumentException("La organización es obligatoria");
        }
    }
}
