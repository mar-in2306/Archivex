package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.EmailTemplateDTO;
import java.util.List;
import java.util.Map;

public interface EmailTemplateService {
    EmailTemplateDTO createTemplate(EmailTemplateDTO dto);
    EmailTemplateDTO getTemplateById(Long id);
    List<EmailTemplateDTO> getTemplatesByOrganization(Long organizationId);
    EmailTemplateDTO updateTemplate(Long id, EmailTemplateDTO dto);
    void deleteTemplate(Long id);

    /**
     * Envía un correo usando la plantilla activa para el eventType de la organización.
     * Las variables del mapa se reemplazan en el cuerpo y asunto del correo.
     * Ejemplo: variables = {"documentTitle": "Contrato X", "userName": "Ana"}
     */
    void sendNotification(Long organizationId, String eventType,
                          String recipientEmail, Map<String, String> variables);
}
