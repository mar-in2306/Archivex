package com.archivex.archivex.business.dto;

import com.archivex.archivex.business.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    // La contraseña se incluye solo en creación; nunca se retorna en respuestas
    private String password;
    private UserRole role;
    private Boolean isActive;
    private Long organizationId;
    private String organizationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
