package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.business.enums.UserRole;
import com.archivex.archivex.business.service.impl.UserServiceImpl;
import com.archivex.archivex.persistenceLayer.dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PRUEBAS 7–12: UserService
 * Cubre: crear usuario, email duplicado, contraseña corta,
 *        obtener por ID, listar por org y toggle de estado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del servicio de Usuarios")
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new UserDTO();
        sampleUser.setId(1L);
        sampleUser.setName("Admin");
        sampleUser.setEmail("admin@miempresa.com");
        sampleUser.setRole(UserRole.ADMIN);
        sampleUser.setIsActive(true);
        sampleUser.setOrganizationId(1L);
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());
    }

    // ─── PRUEBA 7 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 7 - Crear usuario exitosamente")
    void testCrearUsuarioExitosamente() {
        // ARRANGE
        when(userDAO.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userDAO.save(any(UserDTO.class))).thenReturn(sampleUser);

        UserDTO input = new UserDTO();
        input.setName("Admin");
        input.setEmail("admin@miempresa.com");
        input.setPassword("123456");
        input.setRole(UserRole.ADMIN);
        input.setOrganizationId(1L);

        // ACT
        UserDTO result = userService.createUser(input);

        // ASSERT
        assertNotNull(result);
        assertEquals("admin@miempresa.com", result.getEmail());
        assertEquals(UserRole.ADMIN, result.getRole());
        verify(passwordEncoder, times(1)).encode("123456");
        verify(userDAO, times(1)).save(any(UserDTO.class));
    }

    // ─── PRUEBA 8 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 8 - No crear usuario con email duplicado")
    void testNoCrearUsuarioConEmailDuplicado() {
        // ARRANGE
        when(userDAO.existsByEmail("admin@miempresa.com")).thenReturn(true);

        UserDTO input = new UserDTO();
        input.setName("Otro Admin");
        input.setEmail("admin@miempresa.com");
        input.setPassword("123456");
        input.setRole(UserRole.USER);
        input.setOrganizationId(1L);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(input)
        );
        assertTrue(ex.getMessage().contains("email"));
        verify(userDAO, never()).save(any());
    }

    // ─── PRUEBA 9 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 9 - Rechazar contraseña menor a 6 caracteres")
    void testRechazarContrasenaCorta() {
        // ARRANGE
        UserDTO input = new UserDTO();
        input.setName("Usuario");
        input.setEmail("user@empresa.com");
        input.setPassword("123");   // muy corta
        input.setRole(UserRole.USER);
        input.setOrganizationId(1L);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(input)
        );
        assertTrue(ex.getMessage().contains("contraseña") || ex.getMessage().contains("6"));
        verify(userDAO, never()).save(any());
    }

    // ─── PRUEBA 10 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 10 - Obtener usuario por ID existente")
    void testObtenerUsuarioPorIdExistente() {
        // ARRANGE
        when(userDAO.findById(1L)).thenReturn(Optional.of(sampleUser));

        // ACT
        UserDTO result = userService.getUserById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Admin", result.getName());
    }

    // ─── PRUEBA 11 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 11 - Listar usuarios por organización")
    void testListarUsuariosPorOrganizacion() {
        // ARRANGE
        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setName("Usuario Normal");
        user2.setOrganizationId(1L);
        when(userDAO.findByOrganizationId(1L)).thenReturn(List.of(sampleUser, user2));

        // ACT
        List<UserDTO> result = userService.getUsersByOrganization(1L);

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getOrganizationId() == null
                || u.getOrganizationId().equals(1L)));
        verify(userDAO, times(1)).findByOrganizationId(1L);
    }

    // ─── PRUEBA 12 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 12 - Toggle de estado activo/inactivo del usuario")
    void testToggleEstadoUsuario() {
        // ARRANGE — usuario activo
        when(userDAO.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserDTO inactiveUser = new UserDTO();
        inactiveUser.setId(1L);
        inactiveUser.setIsActive(false); // toggle aplicado
        when(userDAO.update(eq(1L), any(UserDTO.class))).thenReturn(Optional.of(inactiveUser));

        // ACT
        UserDTO result = userService.toggleUserStatus(1L);

        // ASSERT
        assertNotNull(result);
        assertFalse(result.getIsActive(), "El usuario debería quedar inactivo tras el toggle");
        verify(userDAO, times(1)).update(eq(1L), any(UserDTO.class));
    }
}
