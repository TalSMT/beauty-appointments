package com.example.beautyappointments.repository;
import com.example.beautyappointments.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
}