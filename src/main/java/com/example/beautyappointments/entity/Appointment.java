package com.example.beautyappointments.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private LocalDateTime dateTime;
    private String treatmentName;
    private  double price;
}
