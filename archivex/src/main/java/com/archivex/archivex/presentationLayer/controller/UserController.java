package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.business.service.UserService;
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
 * Gestión de usuarios por organización.
 *
 * ENDPOINTS:
 *   GET    /api/v1/users/organization/{orgId}  — Listar usuarios de una org
 *   GET    /api/v1/users/{id}                  — Obtener usuario por ID
 *   POST   /api/v1/users                       — Crear usuario         (ADMIN)
 *   PUT    /api/v1/users/{id}                  — Actualizar usuario
 *   PATCH  /api/v1/users/{id}/toggle-status    — Activar/inactivar     (ADMIN)
 *   DELETE /api/v1/users/{id}                  — Eliminar usuario      (ADMIN)
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Gestión de usuarios por organización")
public class UserController {

    private final UserService userService;

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Listar usuarios por organización",
               description = "Retorna todos los usuarios pertenecientes a la organización indicada.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida")
    public ResponseEntity<List<UserDTO>> getByOrganization(
            @Parameter(description = "ID de la organización", example = "1")
            @PathVariable Long organizationId) {
        log.debug("GET /api/v1/users/organization/{}", organizationId);
        return ResponseEntity.ok(userService.getUsersByOrganization(organizationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.debug("GET /api/v1/users/{}", id);
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear usuario",
               description = "Crea un nuevo usuario dentro de la organización. Solo el ADMIN puede crear usuarios.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado — requiere rol ADMIN")
    })
    public ResponseEntity<?> create(@RequestBody UserDTO dto) {
        log.info("POST /api/v1/users — email: {}", dto.getEmail());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al crear el usuario"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario",
               description = "Actualiza nombre y rol del usuario. El email y la contraseña no se modifican aquí.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        log.info("PUT /api/v1/users/{}", id);
        try {
            return ResponseEntity.ok(userService.updateUser(id, dto));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar / inactivar usuario",
               description = "Alterna el estado activo/inactivo del usuario. Un usuario inactivo no puede autenticarse.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado cambiado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        log.info("PATCH /api/v1/users/{}/toggle-status", id);
        try {
            return ResponseEntity.ok(userService.toggleUserStatus(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
