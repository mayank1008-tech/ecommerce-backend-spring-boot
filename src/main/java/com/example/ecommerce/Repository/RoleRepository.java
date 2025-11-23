package com.example.ecommerce.Repository;

import com.example.ecommerce.Model.AppRoles;
import com.example.ecommerce.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRoles appRoles);
}
