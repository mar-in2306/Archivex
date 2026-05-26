package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.DocumentDTO;
import com.archivex.archivex.business.enums.DocumentStatus;
import com.archivex.archivex.business.service.impl.DocumentServiceImpl;
import com.archivex.archivex.persistenceLayer.dao.DocumentDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PRUEBAS 13–18: DocumentService
 * Cubre: crear documento, validar título vacío, obtener por ID,
 *        listar por org, cambiar estado válido e inválido.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del servicio de Documentos")
class xDocumentServiceTest {

    @Mock
    private DocumentDAO documentDAO;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private DocumentDTO sampleDocument;

    @BeforeEach
    void setUp() {
        // Inyectar storagePath vía ReflectionTestUtils (simula @Value)
        ReflectionTestUtils.setField(documentService, "storagePath", "target/test-uploads/");

        sampleDocument = new DocumentDTO();
        sampleDocument.setId(1L);
        sampleDocument.setTitle("Contrato de Servicios");
        sampleDocument.setDescription("Contrato principal");
        sampleDocument.setStatus(DocumentStatus.CREATED);
        sampleDocument.setOrganizationId(1L);
        sampleDocument.setDocumentTypeId(1L);
        sampleDocument.setCreatedById(1L);
        sampleDocument.setCreatedByName("Admin");
        sampleDocument.setCreatedAt(LocalDateTime.now());
        sampleDocument.setUpdatedAt(LocalDateTime.now());
    }

    // ─── PRUEBA 13 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 13 - Crear documento exitosamente sin archivo")
    void testCrearDocumentoExitosamenteSinArchivo() {
        // ARRANGE
        when(documentDAO.save(any(DocumentDTO.class))).thenReturn(sampleDocument);
        when(auditLogService.registerAction(anyLong(), anyLong(), anyString(),
                anyString(), any(), any(), any())).thenReturn(null);

        DocumentDTO input = new DocumentDTO();
        input.setTitle("Contrato de Servicios");
        input.setDocumentTypeId(1L);
        input.setOrganizationId(1L);
        input.setCreatedById(1L);

        // ACT
        DocumentDTO result = documentService.createDocument(input, null);

        // ASSERT
        assertNotNull(result);
        assertEquals("Contrato de Servicios", result.getTitle());
        assertEquals(DocumentStatus.CREATED, result.getStatus());
        verify(documentDAO, times(1)).save(any(DocumentDTO.class));
    }

    // ─── PRUEBA 14 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 14 - Rechazar documento con título vacío")
    void testRechazarDocumentoConTituloVacio() {
        // ARRANGE
        DocumentDTO input = new DocumentDTO();
        input.setTitle("");   // título vacío
        input.setDocumentTypeId(1L);
        input.setOrganizationId(1L);
        input.setCreatedById(1L);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.createDocument(input, null)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("título")
                || ex.getMessage().toLowerCase().contains("titulo"));
        verify(documentDAO, never()).save(any());
    }

    // ─── PRUEBA 15 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 15 - Obtener documento por ID y organización")
    void testObtenerDocumentoPorIdYOrganizacion() {
        // ARRANGE
        when(documentDAO.findByIdAndOrganizationId(1L, 1L))
                .thenReturn(Optional.of(sampleDocument));

        // ACT
        DocumentDTO result = documentService.getDocumentById(1L, 1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Contrato de Servicios", result.getTitle());
    }

    // ─── PRUEBA 16 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 16 - Listar documentos por organización")
    void testListarDocumentosPorOrganizacion() {
        // ARRANGE
        DocumentDTO doc2 = new DocumentDTO();
        doc2.setId(2L);
        doc2.setTitle("Factura 001");
        doc2.setOrganizationId(1L);
        when(documentDAO.findByOrganizationId(1L)).thenReturn(List.of(sampleDocument, doc2));

        // ACT
        List<DocumentDTO> result = documentService.getDocumentsByOrganization(1L);

        // ASSERT
        assertEquals(2, result.size());
        verify(documentDAO, times(1)).findByOrganizationId(1L);
    }

    // ─── PRUEBA 17 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 17 - Cambiar estado CREATED → UNDER_REVIEW (transición válida)")
    void testCambiarEstadoCreatedAUnderReview() {
        // ARRANGE
        when(documentDAO.findByIdAndOrganizationId(1L, 1L))
                .thenReturn(Optional.of(sampleDocument));

        DocumentDTO underReviewDoc = new DocumentDTO();
        underReviewDoc.setId(1L);
        underReviewDoc.setTitle("Contrato de Servicios");
        underReviewDoc.setStatus(DocumentStatus.UNDER_REVIEW);
        underReviewDoc.setOrganizationId(1L);
        underReviewDoc.setCreatedById(1L);
        underReviewDoc.setCreatedByName("Admin");

        when(documentDAO.updateStatus(1L, DocumentStatus.UNDER_REVIEW))
                .thenReturn(Optional.of(underReviewDoc));
        when(auditLogService.registerAction(anyLong(), anyLong(), anyString(),
                anyString(), any(), any(), any())).thenReturn(null);

        // ACT
        DocumentDTO result = documentService.changeDocumentStatus(
                1L, DocumentStatus.UNDER_REVIEW, 1L, 1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(DocumentStatus.UNDER_REVIEW, result.getStatus());
        verify(documentDAO, times(1)).updateStatus(1L, DocumentStatus.UNDER_REVIEW);
    }

    // ─── PRUEBA 18 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PRUEBA 18 - Rechazar transición de estado inválida (APPROVED → CREATED)")
    void testRechazarTransicionDeEstadoInvalida() {
        // ARRANGE — documento ya aprobado
        DocumentDTO approvedDoc = new DocumentDTO();
        approvedDoc.setId(1L);
        approvedDoc.setTitle("Contrato Aprobado");
        approvedDoc.setStatus(DocumentStatus.APPROVED);
        approvedDoc.setOrganizationId(1L);
        approvedDoc.setCreatedById(1L);

        when(documentDAO.findByIdAndOrganizationId(1L, 1L))
                .thenReturn(Optional.of(approvedDoc));

        // ACT & ASSERT — intentar volver a CREATED desde APPROVED es inválido
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> documentService.changeDocumentStatus(
                        1L, DocumentStatus.CREATED, 1L, 1L)
        );
        assertTrue(ex.getMessage().contains("inválida") || ex.getMessage().contains("invalida")
                || ex.getMessage().contains("APPROVED"));
        verify(documentDAO, never()).updateStatus(any(), any());
    }
}
