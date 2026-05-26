package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.EmailTemplateDTO;
import com.archivex.archivex.business.service.EmailTemplateService;
import com.archivex.archivex.securityLayer.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/email-templates")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Plantillas de Correo", description = "Gestión de plantillas de notificación por email configurables por organización")
@SecurityRequirement(name = "bearerAuth")
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;
    private final JwtUtil jwtUtil;

    private Long extractOrgId(String authHeader) {
        return jwtUtil.extractOrganizationId(authHeader.substring(7));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear plantilla de correo",
            description = "Crea una plantilla para un evento (DOCUMENT_CREATED, STATUS_CHANGED, TASK_ASSIGNED). " +
                    "El cuerpo soporta variables: {{documentTitle}}, {{userName}}, {{status}}, {{organizationName}}.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Plantilla creada",
                content = @Content(schema = @Schema(implementation = EmailTemplateDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Solo administradores")
    })
    public ResponseEntity<EmailTemplateDTO> createTemplate(
            @RequestBody EmailTemplateDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        log.info("POST /api/v1/email-templates - evento: {}", dto.getEventType());
        try {
            dto.setOrganizationId(extractOrgId(authHeader));
            EmailTemplateDTO created = emailTemplateService.createTemplate(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear plantilla: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar plantillas de la organización")
    @ApiResponse(responseCode = "200", description = "Lista de plantillas")
    public ResponseEntity<List<EmailTemplateDTO>> getTemplatesByOrganization(
            @RequestHeader("Authorization") String authHeader) {
        Long orgId = extractOrgId(authHeader);
        log.debug("GET /api/v1/email-templates - org: {}", orgId);
        return ResponseEntity.ok(emailTemplateService.getTemplatesByOrganization(orgId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener plantilla por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plantilla encontrada",
                content = @Content(schema = @Schema(implementation = EmailTemplateDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plantilla no encontrada")
    })
    public ResponseEntity<EmailTemplateDTO> getTemplateById(
            @Parameter(description = "ID de la plantilla") @PathVariable Long id) {
        log.debug("GET /api/v1/email-templates/{}", id);
        try {
            return ResponseEntity.ok(emailTemplateService.getTemplateById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar plantilla de correo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plantilla actualizada",
                content = @Content(schema = @Schema(implementation = EmailTemplateDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plantilla no encontrada")
    })
    public ResponseEntity<EmailTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @RequestBody EmailTemplateDTO dto) {
        log.info("PUT /api/v1/email-templates/{}", id);
        try {
            return ResponseEntity.ok(emailTemplateService.updateTemplate(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar plantilla de correo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Plantilla eliminada"),
        @ApiResponse(responseCode = "404", description = "Plantilla no encontrada")
    })
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        log.info("DELETE /api/v1/email-templates/{}", id);
        try {
            emailTemplateService.deleteTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
