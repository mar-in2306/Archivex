package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import com.archivex.archivex.persistenceLayer.mapper.OrganizationMapper;
import com.archivex.archivex.persistenceLayer.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrganizationDAO {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationDTO save(OrganizationDTO dto) {
        OrganizationEntity entity = organizationMapper.toEntity(dto);
        return organizationMapper.toDTO(organizationRepository.save(entity));
    }

    public Optional<OrganizationDTO> findById(Long id) {
        return organizationRepository.findById(id).map(organizationMapper::toDTO);
    }

    public Optional<OrganizationDTO> findByDomain(String domain) {
        return organizationRepository.findByDomain(domain).map(organizationMapper::toDTO);
    }

    public List<OrganizationDTO> findAll() {
        return organizationMapper.toDTOList(organizationRepository.findAll());
    }

    public Optional<OrganizationDTO> update(Long id, OrganizationDTO dto) {
        return organizationRepository.findById(id).map(existing -> {
            organizationMapper.updateEntityFromDTO(dto, existing);
            return organizationMapper.toDTO(organizationRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (organizationRepository.existsById(id)) {
            organizationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByDomain(String domain) {
        return organizationRepository.existsByDomain(domain);
    }

    public boolean existsByName(String name) {
        return organizationRepository.existsByName(name);
    }

    public boolean existsById(Long id) {
        return organizationRepository.existsById(id);
    }
}
