package com.archivex.archivex.securityLayer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Intercepta cada request HTTP y valida el token JWT del header Authorization.
     * Si el token es válido, autentica al usuario en el SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);

            // Solo autenticar si no hay autenticación previa en el contexto
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Usuario autenticado vía JWT: {}", email);

                } catch (Exception e) {
                    log.warn("Error al autenticar usuario desde token: {}", e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token Bearer del header Authorization.
     * Formato esperado: "Authorization: Bearer <token>"
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
