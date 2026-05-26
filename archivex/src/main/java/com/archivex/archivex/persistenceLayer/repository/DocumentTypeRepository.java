package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.persistenceLayer.entity.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity, Long> {
    List<DocumentTypeEntity> findByOrganizationId(Long organizationId);
    List<DocumentTypeEntity> findByOrganizationIdAndIsActive(Long organizationId, Boolean isActive);
    Optional<DocumentTypeEntity> findByIdAndOrganizationId(Long id, Long organizationId);
    boolean existsByNameAndOrganizationId(String name, Long organizationId);
}
