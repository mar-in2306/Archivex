package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.business.service.OrganizationService;
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
 * Gestión de organizaciones (tenants).
 *
 * ENDPOINTS:
 *   GET    /api/v1/organizations          — Listar todas   (ADMIN)
 *   GET    /api/v1/organizations/{id}     — Obtener por ID (ADMIN)
 *   POST   /api/v1/organizations          — Crear          (ADMIN)
 *   PUT    /api/v1/organizations/{id}     — Actualizar     (ADMIN)
 *   DELETE /api/v1/organizations/{id}     — Eliminar       (ADMIN)
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Organizaciones", description = "Gestión de organizaciones (tenants) del sistema SaaS")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las organizaciones", description = "Retorna la lista completa de organizaciones registradas.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<OrganizationDTO>> getAll() {
        log.debug("GET /api/v1/organizations");
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener organización por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Organización encontrada",
                    content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "ID de la organización", example = "1")
            @PathVariable Long id) {
        log.debug("GET /api/v1/organizations/{}", id);
        try {
            return ResponseEntity.ok(organizationService.getOrganizationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear organización", description = "Crea una nueva organización en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Organización creada",
                    content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicados")
    })
    public ResponseEntity<?> create(@RequestBody OrganizationDTO dto) {
        log.info("POST /api/v1/organizations — nombre: {}", dto.getName());
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(organizationService.createOrganization(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar organización")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Organización actualizada"),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody OrganizationDTO dto) {
        log.info("PUT /api/v1/organizations/{}", id);
        try {
            return ResponseEntity.ok(organizationService.updateOrganization(id, dto));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar organización")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Organización eliminada"),
            @ApiResponse(responseCode = "404", description = "Organización no encontrada")
    })
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/organizations/{}", id);
        try {
            organizationService.deleteOrganization(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
