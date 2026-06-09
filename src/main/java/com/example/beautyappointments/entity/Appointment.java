package com.example.beautyappointments.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.example.beautyappointments.entity.Treatment;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private LocalDateTime dateTime;
    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;
    @ManyToOne
    private Customer customer;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
}
