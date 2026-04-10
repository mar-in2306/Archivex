package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.business.service.UserService;
import com.archivex.archivex.persistenceLayer.dao.UserDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO dto) {
        log.info("Creando usuario con email: {}", dto.getEmail());
        validateUserData(dto);
        if (userDAO.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        dto.setIsActive(true);
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        UserDTO result = userDAO.save(dto);
        log.info("Usuario creado con ID: {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.debug("Buscando usuario ID: {}", id);
        return userDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByOrganization(Long organizationId) {
        log.debug("Listando usuarios de la organización ID: {}", organizationId);
        return userDAO.findByOrganizationId(organizationId);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO dto) {
        log.info("Actualizando usuario ID: {}", id);
        getUserById(id);
        validateUserUpdateData(dto);
        return userDAO.update(id, dto)
                .orElseThrow(() -> new RuntimeException("Error al actualizar usuario ID: " + id));
    }

    /**
     * Activa o inactiva un usuario (toggle).
     * Un usuario inactivo no puede autenticarse en el sistema.
     */
    @Override
    public UserDTO toggleUserStatus(Long id) {
        log.info("Cambiando estado del usuario ID: {}", id);
        UserDTO user = getUserById(id);
        user.setIsActive(!user.getIsActive());
        return userDAO.update(id, user)
                .orElseThrow(() -> new RuntimeException("Error al cambiar estado del usuario ID: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Eliminando usuario ID: {}", id);
        getUserById(id);
        boolean deleted = userDAO.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("Error al eliminar usuario ID: " + id);
        }
        log.info("Usuario eliminado ID: {}", id);
    }

    private void validateUserData(UserDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email del usuario es obligatorio");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (dto.getRole() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio");
        }
        if (dto.getOrganizationId() == null) {
            throw new IllegalArgumentException("El ID de organización es obligatorio");
        }
    }

    private void validateUserUpdateData(UserDTO dto) {
        if (dto.getName() != null && dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }
}
