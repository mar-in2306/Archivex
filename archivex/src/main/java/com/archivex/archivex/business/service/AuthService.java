package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.LoginRequestDTO;
import com.archivex.archivex.business.dto.LoginResponseDTO;
import com.archivex.archivex.business.dto.OrganizationDTO;
import com.archivex.archivex.business.dto.UserDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    UserDTO registerOrganizationWithAdmin(OrganizationDTO organizationDTO, UserDTO adminDTO);
}
