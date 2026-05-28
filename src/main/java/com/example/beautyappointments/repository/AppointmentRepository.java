package com.example.beautyappointments.repository;

import com.example.beautyappointments.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

}