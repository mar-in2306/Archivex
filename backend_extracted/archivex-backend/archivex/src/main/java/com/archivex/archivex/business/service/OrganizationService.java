package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.OrganizationDTO;
import java.util.List;

public interface OrganizationService {
    OrganizationDTO createOrganization(OrganizationDTO dto);
    OrganizationDTO getOrganizationById(Long id);
    List<OrganizationDTO> getAllOrganizations();
    OrganizationDTO updateOrganization(Long id, OrganizationDTO dto);
    void deleteOrganization(Long id);
}
