package com.example.beautyappointments.repository;
import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

}