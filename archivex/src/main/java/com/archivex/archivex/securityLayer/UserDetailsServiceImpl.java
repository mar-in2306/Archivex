package com.archivex.archivex.securityLayer;

import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import com.archivex.archivex.persistenceLayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga el usuario por email para Spring Security.
     * El "username" en este sistema es el email del usuario.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Cargando usuario por email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });

        if (!user.getIsActive()) {
            log.warn("Intento de login con cuenta inactiva: {}", email);
            throw new UsernameNotFoundException("Cuenta de usuario inactiva: " + email);
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
