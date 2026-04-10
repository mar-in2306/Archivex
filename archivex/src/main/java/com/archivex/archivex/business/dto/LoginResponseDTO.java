package com.archivex.archivex.business.dto;

import com.archivex.archivex.business.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String userName;
    private String userEmail;
    private UserRole role;
    private Long organizationId;
    private String organizationName;

    public LoginResponseDTO(String token, Long userId, String userName,
                            String userEmail, UserRole role,
                            Long organizationId, String organizationName) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.role = role;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
    }
}
