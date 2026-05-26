package com.archivex.archivex.persistenceLayer.repository;

import com.archivex.archivex.persistenceLayer.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {
    Optional<OrganizationEntity> findByDomain(String domain);
    boolean existsByDomain(String domain);
    boolean existsByName(String name);
}
