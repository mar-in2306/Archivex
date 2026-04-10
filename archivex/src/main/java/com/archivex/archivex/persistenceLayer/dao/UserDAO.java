package com.archivex.archivex.persistenceLayer.dao;

import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.business.enums.UserRole;
import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import com.archivex.archivex.persistenceLayer.mapper.UserMapper;
import com.archivex.archivex.persistenceLayer.repository.OrganizationRepository;
import com.archivex.archivex.persistenceLayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDAO {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrganizationRepository organizationRepository;

    public UserDTO save(UserDTO dto) {
        UserEntity entity = userMapper.toEntity(dto);
        // Asignar organización
        OrganizationEntity org = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con ID: " + dto.getOrganizationId()));
        entity.setOrganization(org);
        return userMapper.toDTO(userRepository.save(entity));
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDTO);
    }

    public Optional<UserEntity> findEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDTO);
    }

    public List<UserDTO> findByOrganizationId(Long organizationId) {
        return userMapper.toDTOList(userRepository.findByOrganizationId(organizationId));
    }

    public List<UserDTO> findByOrganizationIdAndRole(Long organizationId, UserRole role) {
        return userMapper.toDTOList(userRepository.findByOrganizationIdAndRole(organizationId, role));
    }

    public List<UserDTO> findByOrganizationIdAndIsActive(Long organizationId, Boolean isActive) {
        return userMapper.toDTOList(userRepository.findByOrganizationIdAndIsActive(organizationId, isActive));
    }

    public Optional<UserDTO> update(Long id, UserDTO dto) {
        return userRepository.findById(id).map(existing -> {
            userMapper.updateEntityFromDTO(dto, existing);
            return userMapper.toDTO(userRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
