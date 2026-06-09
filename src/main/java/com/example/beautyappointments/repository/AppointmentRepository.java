package com.example.beautyappointments.repository;

import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCustomerId(Long customerId);
    boolean existsByDateTime (LocalDateTime dateTime);
    List<Appointment> findByDateTimeBetween (LocalDateTime start, LocalDateTime end);
    List<Appointment> findByStatus(AppointmentStatus status);


    List<Appointment> findByDateTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, AppointmentStatus status);
}