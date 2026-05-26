package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.DocumentDTO;
import com.archivex.archivex.business.enums.DocumentStatus;
import com.archivex.archivex.business.service.DocumentService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Documentos", description = "Gestión del ciclo de vida de documentos digitales")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;
    private final JwtUtil jwtUtil;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Long extractOrgId(String authHeader) {
        return jwtUtil.extractOrganizationId(authHeader.substring(7));
    }

    private Long extractUserId(String authHeader) {
        // El userId no está en el token, se delega al servicio; aquí retornamos organizationId como proxy
        // En un sistema real se añadiría userId al JWT claim
        return jwtUtil.extractOrganizationId(authHeader.substring(7));
    }

    // ─── Endpoints ───────────────────────────────────────────────────────────

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear documento", description = "Crea un nuevo documento. El archivo es opcional en este paso.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Documento creado exitosamente",
                content = @Content(schema = @Schema(implementation = DocumentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<DocumentDTO> createDocument(
            @RequestPart("document") DocumentDTO documentDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        log.info("POST /api/v1/documents - Creando documento: {}", documentDTO.getTitle());
        try {
            Long orgId = extractOrgId(authHeader);
            documentDTO.setOrganizationId(orgId);
            DocumentDTO created = documentService.createDocument(documentDTO, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear documento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al crear documento: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener documento por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documento encontrado",
                content = @Content(schema = @Schema(implementation = DocumentDTO.class))),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<DocumentDTO> getDocumentById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        log.debug("GET /api/v1/documents/{}", id);
        try {
            Long orgId = extractOrgId(authHeader);
            DocumentDTO doc = documentService.getDocumentById(id, orgId);
            return ResponseEntity.ok(doc);
        } catch (RuntimeException e) {
            log.warn("Documento no encontrado ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar documentos de la organización")
    @ApiResponse(responseCode = "200", description = "Lista de documentos")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByOrganization(
            @RequestHeader("Authorization") String authHeader) {

        log.debug("GET /api/v1/documents");
        Long orgId = extractOrgId(authHeader);
        return ResponseEntity.ok(documentService.getDocumentsByOrganization(orgId));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filtrar documentos",
            description = "Filtra documentos por estado, tipo documental y rango de fechas.")
    public ResponseEntity<List<DocumentDTO>> filterDocuments(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "Estado del documento") @RequestParam(required = false) DocumentStatus status,
            @Parameter(description = "ID del tipo documental") @RequestParam(required = false) Long documentTypeId,
            @Parameter(description = "Fecha inicio (ISO)") @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Fecha fin (ISO)") @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        log.debug("GET /api/v1/documents/filter - status:{} tipo:{}", status, documentTypeId);
        Long orgId = extractOrgId(authHeader);
        return ResponseEntity.ok(documentService.filterDocuments(orgId, status, documentTypeId, from, to));
    }

    @PutMapping("/{id}/metadata")
    @Operation(summary = "Actualizar metadatos del documento",
            description = "Modifica título, descripción y tipo documental. No reemplaza el archivo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metadatos actualizados",
                content = @Content(schema = @Schema(implementation = DocumentDTO.class))),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<DocumentDTO> updateMetadata(
            @PathVariable Long id,
            @RequestBody DocumentDTO documentDTO,
            @RequestHeader("Authorization") String authHeader) {

        log.info("PUT /api/v1/documents/{}/metadata", id);
        try {
            Long orgId = extractOrgId(authHeader);
            Long userId = extractUserId(authHeader);
            DocumentDTO updated = documentService.updateDocumentMetadata(id, documentDTO, orgId, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) return ResponseEntity.notFound().build();
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado del documento",
            description = "Transiciones válidas: CREATED→UNDER_REVIEW, UNDER_REVIEW→APPROVED|REJECTED, REJECTED→UNDER_REVIEW")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado",
                content = @Content(schema = @Schema(implementation = DocumentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Transición de estado inválida"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<DocumentDTO> changeStatus(
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado del documento", required = true)
            @RequestParam DocumentStatus newStatus,
            @RequestHeader("Authorization") String authHeader) {

        log.info("PATCH /api/v1/documents/{}/status -> {}", id, newStatus);
        try {
            Long orgId = extractOrgId(authHeader);
            Long userId = extractUserId(authHeader);
            DocumentDTO updated = documentService.changeDocumentStatus(id, newStatus, orgId, userId);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            log.warn("Transición inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar documento",
            description = "Elimina el documento y su archivo físico del servidor.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Documento eliminado"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        log.info("DELETE /api/v1/documents/{}", id);
        try {
            Long orgId = extractOrgId(authHeader);
            Long userId = extractUserId(authHeader);
            documentService.deleteDocument(id, orgId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Descargar archivo del documento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo descargado"),
        @ApiResponse(responseCode = "404", description = "Documento o archivo no encontrado")
    })
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        log.info("GET /api/v1/documents/{}/download", id);
        try {
            Long orgId = extractOrgId(authHeader);
            Long userId = extractUserId(authHeader);
            byte[] fileBytes = documentService.downloadDocument(id, orgId, userId);

            DocumentDTO doc = documentService.getDocumentById(id, orgId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(doc.getFileName() != null ? doc.getFileName() : "documento")
                    .build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn("Error al descargar documento ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
