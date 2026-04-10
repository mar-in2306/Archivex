package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.DocumentWorkflowDTO;
import com.archivex.archivex.business.dto.WorkflowStepDTO;
import com.archivex.archivex.business.enums.WorkflowTaskStatus;
import com.archivex.archivex.business.service.WorkflowService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Flujos de Trabajo", description = "Gestión de pasos de workflow y tareas de aprobación de documentos")
@SecurityRequirement(name = "bearerAuth")
public class WorkflowController {

    private final WorkflowService workflowService;
    private final JwtUtil jwtUtil;

    private Long extractOrgId(String authHeader) {
        return jwtUtil.extractOrganizationId(authHeader.substring(7));
    }

    // ─── Pasos de flujo (configuración por tipo documental) ─────────────────

    @PostMapping("/steps")
    @Operation(summary = "Crear paso de flujo",
            description = "Define un nuevo paso del proceso de aprobación para un tipo documental.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Paso creado",
                content = @Content(schema = @Schema(implementation = WorkflowStepDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<WorkflowStepDTO> createStep(@RequestBody WorkflowStepDTO dto) {
        log.info("POST /api/v1/workflow/steps - tipo documental: {}", dto.getDocumentTypeId());
        try {
            WorkflowStepDTO created = workflowService.createWorkflowStep(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear paso: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/steps/document-type/{documentTypeId}")
    @Operation(summary = "Listar pasos de flujo por tipo documental",
            description = "Retorna los pasos ordenados por stepOrder ascendente.")
    @ApiResponse(responseCode = "200", description = "Lista de pasos")
    public ResponseEntity<List<WorkflowStepDTO>> getStepsByDocumentType(
            @Parameter(description = "ID del tipo documental") @PathVariable Long documentTypeId) {
        log.debug("GET /api/v1/workflow/steps/document-type/{}", documentTypeId);
        return ResponseEntity.ok(workflowService.getStepsByDocumentType(documentTypeId));
    }

    @PutMapping("/steps/{id}")
    @Operation(summary = "Actualizar paso de flujo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paso actualizado",
                content = @Content(schema = @Schema(implementation = WorkflowStepDTO.class))),
        @ApiResponse(responseCode = "404", description = "Paso no encontrado")
    })
    public ResponseEntity<WorkflowStepDTO> updateStep(
            @PathVariable Long id,
            @RequestBody WorkflowStepDTO dto) {
        log.info("PUT /api/v1/workflow/steps/{}", id);
        try {
            WorkflowStepDTO updated = workflowService.updateWorkflowStep(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/steps/{id}")
    @Operation(summary = "Eliminar paso de flujo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Paso eliminado"),
        @ApiResponse(responseCode = "404", description = "Paso no encontrado")
    })
    public ResponseEntity<Void> deleteStep(@PathVariable Long id) {
        log.info("DELETE /api/v1/workflow/steps/{}", id);
        try {
            workflowService.deleteWorkflowStep(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ─── Tareas de flujo (ejecución por documento) ──────────────────────────

    @PostMapping("/tasks")
    @Operation(summary = "Asignar tarea de flujo",
            description = "Asigna una tarea de revisión/aprobación a un usuario para un documento en un paso específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tarea asignada",
                content = @Content(schema = @Schema(implementation = DocumentWorkflowDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<DocumentWorkflowDTO> assignTask(@RequestBody DocumentWorkflowDTO dto) {
        log.info("POST /api/v1/workflow/tasks - doc: {} usuario: {}", dto.getDocumentId(), dto.getAssignedToId());
        try {
            DocumentWorkflowDTO created = workflowService.assignTask(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("Error al asignar tarea: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/tasks/document/{documentId}")
    @Operation(summary = "Listar tareas de un documento",
            description = "Muestra todas las tareas de flujo asignadas para un documento específico.")
    @ApiResponse(responseCode = "200", description = "Lista de tareas")
    public ResponseEntity<List<DocumentWorkflowDTO>> getTasksByDocument(
            @Parameter(description = "ID del documento") @PathVariable Long documentId) {
        log.debug("GET /api/v1/workflow/tasks/document/{}", documentId);
        return ResponseEntity.ok(workflowService.getTasksByDocument(documentId));
    }

    @GetMapping("/tasks/pending")
    @Operation(summary = "Mis tareas pendientes",
            description = "Retorna todas las tareas pendientes asignadas al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de tareas pendientes")
    public ResponseEntity<List<DocumentWorkflowDTO>> getMyPendingTasks(
            @RequestHeader("Authorization") String authHeader) {
        Long orgId = extractOrgId(authHeader);
        log.debug("GET /api/v1/workflow/tasks/pending - org: {}", orgId);
        // Usamos orgId como proxy de userId; en producción el userId vendría en el JWT
        return ResponseEntity.ok(workflowService.getPendingTasksByUser(orgId));
    }

    @PatchMapping("/tasks/{taskId}/resolve")
    @Operation(summary = "Resolver tarea de flujo",
            description = "Completa o rechaza una tarea de revisión. Incluye comentarios opcionales.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarea resuelta",
                content = @Content(schema = @Schema(implementation = DocumentWorkflowDTO.class))),
        @ApiResponse(responseCode = "400", description = "La tarea ya fue resuelta o datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    public ResponseEntity<DocumentWorkflowDTO> resolveTask(
            @Parameter(description = "ID de la tarea") @PathVariable Long taskId,
            @Parameter(description = "Nuevo estado: COMPLETED o REJECTED") @RequestParam WorkflowTaskStatus newStatus,
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        log.info("PATCH /api/v1/workflow/tasks/{}/resolve -> {}", taskId, newStatus);
        try {
            String comments = (body != null) ? body.getOrDefault("comments", "") : "";
            Long orgId = extractOrgId(authHeader);
            DocumentWorkflowDTO resolved = workflowService.resolveTask(taskId, newStatus, comments, orgId, orgId);
            return ResponseEntity.ok(resolved);
        } catch (IllegalStateException e) {
            log.warn("Error al resolver tarea: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
