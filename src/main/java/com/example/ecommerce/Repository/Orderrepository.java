package com.example.ecommerce.Repository;

import com.example.ecommerce.Model.Order;
import org.aspectj.apache.bcel.util.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Orderrepository extends JpaRepository<Order, Long> {
}
