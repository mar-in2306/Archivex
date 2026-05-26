package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.AuditLogDTO;
import com.archivex.archivex.business.service.AuditLogService;
import com.archivex.archivex.securityLayer.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Trazabilidad / Auditoría", description = "Consulta del historial completo de acciones sobre documentos")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final JwtUtil jwtUtil;

    private Long extractOrgId(String authHeader) {
        return jwtUtil.extractOrganizationId(authHeader.substring(7));
    }

    @GetMapping("/document/{documentId}")
    @Operation(summary = "Historial de un documento",
            description = "Retorna todas las acciones registradas sobre el documento, ordenadas por fecha descendente.")
    @ApiResponse(responseCode = "200", description = "Historial del documento")
    public ResponseEntity<List<AuditLogDTO>> getHistoryByDocument(
            @Parameter(description = "ID del documento") @PathVariable Long documentId) {
        log.debug("GET /api/v1/audit/document/{}", documentId);
        return ResponseEntity.ok(auditLogService.getHistoryByDocument(documentId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Acciones realizadas por un usuario",
            description = "Lista todas las acciones hechas por un usuario específico en cualquier documento.")
    @ApiResponse(responseCode = "200", description = "Historial del usuario")
    public ResponseEntity<List<AuditLogDTO>> getHistoryByUser(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        log.debug("GET /api/v1/audit/user/{}", userId);
        return ResponseEntity.ok(auditLogService.getHistoryByUser(userId));
    }

    @GetMapping("/organization")
    @Operation(summary = "Auditoría completa de la organización",
            description = "Retorna todas las entradas de auditoría de la organización del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Auditoría de la organización")
    public ResponseEntity<List<AuditLogDTO>> getHistoryByOrganization(
            @RequestHeader("Authorization") String authHeader) {
        Long orgId = extractOrgId(authHeader);
        log.debug("GET /api/v1/audit/organization - org: {}", orgId);
        return ResponseEntity.ok(auditLogService.getHistoryByOrganization(orgId));
    }
}
