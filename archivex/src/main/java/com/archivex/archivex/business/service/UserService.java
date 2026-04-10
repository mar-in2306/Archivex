package com.archivex.archivex.business.service;

import com.archivex.archivex.business.dto.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO dto);
    UserDTO getUserById(Long id);
    List<UserDTO> getUsersByOrganization(Long organizationId);
    UserDTO updateUser(Long id, UserDTO dto);
    UserDTO toggleUserStatus(Long id);
    void deleteUser(Long id);
}
