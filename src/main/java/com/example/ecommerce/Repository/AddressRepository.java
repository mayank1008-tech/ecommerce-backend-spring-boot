package com.example.ecommerce.Repository;

import com.example.ecommerce.Model.Address;
import com.example.ecommerce.Model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Page<Address> findByUser(User user, Pageable pagedetails);
}
