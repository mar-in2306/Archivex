package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.business.service.impl.OrganizationServiceImpl;
import com.archivex.archivex.persistenceLayer.dao.OrganizationDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PRUEBAS 1–6: OrganizationService
 * Cubre: crear, obtener, listar, actualizar, eliminar y dominio duplicado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del servicio de Organizaciones")
class OrganizationServiceTest {

    @Mock
    private OrganizationDAO organizationDAO;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private OrganizationDTO sampleOrg;

    @BeforeEach
    void setUp() {
        sampleOrg = new OrganizationDTO();
        sampleOrg.setId(1L);
        sampleOrg.setName("Mi Empresa");
        sampleOrg.setDomain("miempresa.com");
        sampleOrg.setContactEmail("admin@miempresa.com");
        sampleOrg.setIsActive(true);
        sampleOrg.setCreatedAt(LocalDateTime.now());
        sampleOrg.setUpdatedAt(LocalDateTime.now());
    }

    // ─── PRUEBA 1 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 1 - Crear organización exitosamente")
    void testCrearOrganizacionExitosamente() {
        // ARRANGE
        when(organizationDAO.existsByDomain(anyString())).thenReturn(false);
        when(organizationDAO.existsByName(anyString())).thenReturn(false);
        when(organizationDAO.save(any(OrganizationDTO.class))).thenReturn(sampleOrg);

        OrganizationDTO input = new OrganizationDTO();
        input.setName("Mi Empresa");
        input.setDomain("miempresa.com");
        input.setContactEmail("admin@miempresa.com");

        // ACT
        OrganizationDTO result = organizationService.createOrganization(input);

        // ASSERT
        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals("Mi Empresa", result.getName());
        assertEquals("miempresa.com", result.getDomain());
        verify(organizationDAO, times(1)).save(any(OrganizationDTO.class));
    }

    // ─── PRUEBA 2 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 2 - No crear organización con dominio duplicado")
    void testNoCrarOrganizacionConDominioDuplicado() {
        // ARRANGE
        when(organizationDAO.existsByDomain("miempresa.com")).thenReturn(true);

        OrganizationDTO input = new OrganizationDTO();
        input.setName("Otra Empresa");
        input.setDomain("miempresa.com");
        input.setContactEmail("otro@miempresa.com");

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> organizationService.createOrganization(input)
        );
        assertTrue(ex.getMessage().contains("dominio"));
        verify(organizationDAO, never()).save(any());
    }

    // ─── PRUEBA 3 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 3 - Obtener organización por ID existente")
    void testObtenerOrganizacionPorIdExistente() {
        // ARRANGE
        when(organizationDAO.findById(1L)).thenReturn(Optional.of(sampleOrg));

        // ACT
        OrganizationDTO result = organizationService.getOrganizationById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mi Empresa", result.getName());
    }

    // ─── PRUEBA 4 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 4 - Lanzar excepción si organización no existe")
    void testLanzarExcepcionSiOrganizacionNoExiste() {
        // ARRANGE
        when(organizationDAO.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> organizationService.getOrganizationById(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // ─── PRUEBA 5 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 5 - Listar todas las organizaciones")
    void testListarTodasLasOrganizaciones() {
        // ARRANGE
        OrganizationDTO org2 = new OrganizationDTO();
        org2.setId(2L);
        org2.setName("Segunda Empresa");
        when(organizationDAO.findAll()).thenReturn(List.of(sampleOrg, org2));

        // ACT
        List<OrganizationDTO> result = organizationService.getAllOrganizations();

        // ASSERT
        assertEquals(2, result.size());
        verify(organizationDAO, times(1)).findAll();
    }

    // ─── PRUEBA 6 ─────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 6 - Eliminar organización existente")
    void testEliminarOrganizacionExistente() {
        // ARRANGE
        when(organizationDAO.findById(1L)).thenReturn(Optional.of(sampleOrg));
        when(organizationDAO.deleteById(1L)).thenReturn(true);

        // ACT & ASSERT
        assertDoesNotThrow(() -> organizationService.deleteOrganization(1L));
        verify(organizationDAO, times(1)).deleteById(1L);
    }
}
