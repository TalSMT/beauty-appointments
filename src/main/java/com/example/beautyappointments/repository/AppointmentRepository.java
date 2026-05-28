package com.example.beautyappointments.repository;

import com.example.beautyappointments.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCustomerId(Long customerId);

}