package com.vuong.vmess.repository;

import com.vuong.vmess.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("SELECT r FROM Role r WHERE r.id = ?1")
    Optional<Role> findById(UUID id);

    @Query("SELECT r FROM Role r WHERE r.name LIKE CONCAT('%', :roleName, '%')")
    Role findByRoleName(String roleName);
}
