package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.business.service.OrganizationService;
import com.archivex.archivex.persistenceLayer.dao.OrganizationDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationDAO organizationDAO;

    @Override
    public OrganizationDTO createOrganization(OrganizationDTO dto) {
        log.info("Creando organización: {}", dto.getName());
        validateOrganizationData(dto);
        if (organizationDAO.existsByDomain(dto.getDomain())) {
            throw new IllegalArgumentException("Ya existe una organización con el dominio: " + dto.getDomain());
        }
        if (organizationDAO.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Ya existe una organización con el nombre: " + dto.getName());
        }
        dto.setIsActive(true);
        OrganizationDTO result = organizationDAO.save(dto);
        log.info("Organización creada con ID: {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationDTO getOrganizationById(Long id) {
        log.debug("Buscando organización ID: {}", id);
        return organizationDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationDTO> getAllOrganizations() {
        log.debug("Listando todas las organizaciones");
        return organizationDAO.findAll();
    }

    @Override
    public OrganizationDTO updateOrganization(Long id, OrganizationDTO dto) {
        log.info("Actualizando organización ID: {}", id);
        getOrganizationById(id); // Verifica existencia
        return organizationDAO.update(id, dto)
                .orElseThrow(() -> new RuntimeException("Error al actualizar organización ID: " + id));
    }

    @Override
    public void deleteOrganization(Long id) {
        log.info("Eliminando organización ID: {}", id);
        getOrganizationById(id);
        boolean deleted = organizationDAO.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("Error al eliminar organización ID: " + id);
        }
        log.info("Organización eliminada ID: {}", id);
    }

    private void validateOrganizationData(OrganizationDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la organización es obligatorio");
        }
        if (dto.getDomain() == null || dto.getDomain().isBlank()) {
            throw new IllegalArgumentException("El dominio de la organización es obligatorio");
        }
        if (dto.getContactEmail() == null || dto.getContactEmail().isBlank()) {
            throw new IllegalArgumentException("El email de contacto es obligatorio");
        }
    }
}
