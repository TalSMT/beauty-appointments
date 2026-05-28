package com.example.beautyappointments.controller;

import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.repository.AppointmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository repository;

    public AppointmentController(AppointmentRepository repository) {
        this.repository = repository;
    }

    // GET - מחזיר את כל התורים
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return repository.findAll();
    }

    // POST - מוסיף תור חדש
    @PostMapping
    public Appointment addAppointment(@RequestBody Appointment appointment) {
        System.out.println(appointment);
        return repository.save(appointment);
    }
}