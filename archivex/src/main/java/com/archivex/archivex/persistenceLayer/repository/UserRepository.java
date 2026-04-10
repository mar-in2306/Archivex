package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.business.enums.UserRole;
import com.archivex.archivex.persistenceLayer.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserEntity> findByOrganizationId(Long organizationId);
    List<UserEntity> findByOrganizationIdAndRole(Long organizationId, UserRole role);
    List<UserEntity> findByOrganizationIdAndIsActive(Long organizationId, Boolean isActive);
}
