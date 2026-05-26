package com.archivex.archivex.presentationLayer.controller;

import com.archivex.archivex.business.dto.LoginRequestDTO;
import com.archivex.archivex.business.dto.LoginResponseDTO;
import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.business.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación y registro de organizaciones.
 *
 * ENDPOINTS:
 *   POST /api/v1/auth/login            — Iniciar sesión y obtener JWT
 *   POST /api/v1/organizations/register — Registrar nueva organización + admin (público)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Login y registro de nuevas organizaciones")
public class AuthController {

    private final AuthService authService;

    // ─── Login ───────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/login")
    @Operation(
            summary     = "Iniciar sesión",
            description = "Autentica al usuario con email y contraseña. Retorna un token JWT y datos del perfil."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso — token JWT generado",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Datos incompletos")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        log.info("POST /api/v1/auth/login — email: {}", request.getEmail());
        try {
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email y contraseña son obligatorios"));
            }
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Login fallido para: {} — {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    // ─── Registro de organización ─────────────────────────────────────────────

    @PostMapping("/api/v1/organizations/register")
    @Operation(
            summary     = "Registrar nueva organización",
            description = "Crea una organización (tenant) nueva junto con su usuario administrador. " +
                          "Endpoint público — no requiere autenticación."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Organización y admin creados correctamente",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o dominio/nombre/email ya registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> registerOrganization(
            @RequestParam String orgName,
            @RequestParam String orgDomain,
            @RequestParam String orgContactEmail,
            @RequestParam(required = false) String orgDescription,
            @RequestParam String adminName,
            @RequestParam String adminEmail,
            @RequestParam String adminPassword
    ) {
        log.info("POST /api/v1/organizations/register — org: {}", orgName);
        try {
            OrganizationDTO orgDTO = new OrganizationDTO();
            orgDTO.setName(orgName);
            orgDTO.setDomain(orgDomain);
            orgDTO.setContactEmail(orgContactEmail);
            orgDTO.setDescription(orgDescription);

            UserDTO adminDTO = new UserDTO();
            adminDTO.setName(adminName);
            adminDTO.setEmail(adminEmail);
            adminDTO.setPassword(adminPassword);

            UserDTO created = authService.registerOrganizationWithAdmin(orgDTO, adminDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al registrar org: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al registrar organización: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al registrar la organización"));
        }
    }
}
