package com.example.beautyappointments.controller;

import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.service.AppointmentService;
import org.springframework.stereotype.Service;

import com.example.beautyappointments.entity.Customer;
import com.example.beautyappointments.repository.AppointmentRepository;
import com.example.beautyappointments.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;


    public AppointmentController(AppointmentService service) {
        this.service = service;

    }

    // GET - מחזיר את כל התורים
    @GetMapping
    public List<Appointment> getAllAppointments() {

        return service.getAll();
    }
    //מחזיר סכום הפריטים עבור התור
    @GetMapping("/customer/{id}/total")
    public Double getTotalPrice(@PathVariable Long id){
        return service.getTotalPriceByCustomer(id);
    }



    // POST - מוסיף תור חדש
    @PostMapping
    public Appointment Create(@RequestBody Appointment appointment) {
        return service.create(appointment);
    }
    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id){
        return service.getById(id);
    }

    @DeleteMapping ("/{id}")
    public  void deleteAppointment(@PathVariable Long id){
      service.delete(id);
    }
    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment){
        return service.update(id,appointment);
    }
}