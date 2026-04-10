package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.DocumentTypeDTO;
import com.archivex.archivex.business.service.DocumentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Gestión de tipos documentales (factura, contrato, informe, etc.).
 *
 * ENDPOINTS:
 *   GET    /api/v1/document-types/organization/{orgId}  — Listar por organización
 *   GET    /api/v1/document-types/{id}                  — Obtener por ID
 *   POST   /api/v1/document-types                       — Crear       (ADMIN)
 *   PUT    /api/v1/document-types/{id}                  — Actualizar  (ADMIN)
 *   DELETE /api/v1/document-types/{id}                  — Eliminar    (ADMIN)
 */
@RestController
@RequestMapping("/api/v1/document-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Tipos Documentales", description = "Parametrización de tipos de documentos por organización")
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Listar tipos documentales por organización")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<DocumentTypeDTO>> getByOrganization(
            @Parameter(description = "ID de la organización", example = "1")
            @PathVariable Long organizationId) {
        log.debug("GET /api/v1/document-types/organization/{}", organizationId);
        return ResponseEntity.ok(documentTypeService.getDocumentTypesByOrganization(organizationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo documental por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo documental encontrado",
                    content = @Content(schema = @Schema(implementation = DocumentTypeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tipo documental no encontrado")
    })
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            @Parameter(description = "ID de la organización para validar aislamiento", example = "1")
            @RequestParam Long organizationId) {
        log.debug("GET /api/v1/document-types/{}", id);
        try {
            return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id, organizationId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear tipo documental",
               description = "Crea un nuevo tipo documental para la organización. Solo ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo documental creado",
                    content = @Content(schema = @Schema(implementation = DocumentTypeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado")
    })
    public ResponseEntity<?> create(@RequestBody DocumentTypeDTO dto) {
        log.info("POST /api/v1/document-types — nombre: {} | org: {}", dto.getName(), dto.getOrganizationId());
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(documentTypeService.createDocumentType(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear tipo documental: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar tipo documental")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo documental actualizado"),
            @ApiResponse(responseCode = "404", description = "No encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody DocumentTypeDTO dto,
                                    @RequestParam Long organizationId) {
        log.info("PUT /api/v1/document-types/{}", id);
        try {
            return ResponseEntity.ok(documentTypeService.updateDocumentType(id, dto, organizationId));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar tipo documental")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestParam Long organizationId) {
        log.info("DELETE /api/v1/document-types/{}", id);
        try {
            documentTypeService.deleteDocumentType(id, organizationId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
