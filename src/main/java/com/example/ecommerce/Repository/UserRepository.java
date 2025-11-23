package com.example.ecommerce.Repository;

import com.example.ecommerce.Model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(@NotBlank @Size(min = 3, max = 30) String username);

    boolean existsByEmail(@Email @NotBlank @Size(min = 3, max = 30) String email);

}
