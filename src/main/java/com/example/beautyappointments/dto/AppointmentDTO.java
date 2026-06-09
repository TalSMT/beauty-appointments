package com.example.beautyappointments.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long id;
    private Long treatmentId;
    private LocalDateTime dateTime;
    private Long customerId;
}