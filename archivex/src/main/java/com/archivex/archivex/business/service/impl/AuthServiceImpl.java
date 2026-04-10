package com.archivex.archivex.business.service.impl;

import com.archivex.archivex.business.dto.LoginRequestDTO;
import com.archivex.archivex.business.dto.LoginResponseDTO;
import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.business.dto.UserDTO;
import com.archivex.archivex.business.enums.UserRole;
import com.archivex.archivex.business.service.AuthService;
import com.archivex.archivex.persistenceLayer.dao.OrganizationDAO;
import com.archivex.archivex.persistenceLayer.dao.UserDAO;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import com.archivex.archivex.securityLayer.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDAO userDAO;
    private final OrganizationDAO organizationDAO;
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica al usuario y retorna el JWT con info del perfil.
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Intento de login para: {}", request.getEmail());

        // Spring Security valida credenciales (lanza excepción si son inválidas)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Cargar datos del usuario desde la BD
        UserEntity user = userDAO.findEntityByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getOrganization().getId(),
                user.getRole().name()
        );

        log.info("Login exitoso para: {} | Org: {}", user.getEmail(), user.getOrganization().getName());

        return new LoginResponseDTO(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getOrganization().getId(),
                user.getOrganization().getName()
        );
    }

    /**
     * Registra una nueva organización junto con su usuario administrador.
     * Punto de entrada SaaS: cada nueva empresa comienza aquí.
     */
    @Override
    public UserDTO registerOrganizationWithAdmin(OrganizationDTO organizationDTO, UserDTO adminDTO) {
        log.info("Registrando nueva organización: {}", organizationDTO.getName());

        // Validar unicidad
        if (organizationDAO.existsByDomain(organizationDTO.getDomain())) {
            throw new IllegalArgumentException("Ya existe una organización con el dominio: " + organizationDTO.getDomain());
        }
        if (organizationDAO.existsByName(organizationDTO.getName())) {
            throw new IllegalArgumentException("Ya existe una organización con el nombre: " + organizationDTO.getName());
        }
        if (userDAO.existsByEmail(adminDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + adminDTO.getEmail());
        }

        // Crear organización
        organizationDTO.setIsActive(true);
        OrganizationDTO savedOrg = organizationDAO.save(organizationDTO);
        log.info("Organización creada con ID: {}", savedOrg.getId());

        // Crear usuario administrador
        adminDTO.setOrganizationId(savedOrg.getId());
        adminDTO.setRole(UserRole.ADMIN);
        adminDTO.setIsActive(true);
        adminDTO.setPassword(passwordEncoder.encode(adminDTO.getPassword()));

        UserDTO savedAdmin = userDAO.save(adminDTO);
        log.info("Administrador creado con ID: {} para org: {}", savedAdmin.getId(), savedOrg.getName());

        return savedAdmin;
    }
}
