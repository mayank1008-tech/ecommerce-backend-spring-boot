package com.example.ecommerce.Repository;

import com.example.ecommerce.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
