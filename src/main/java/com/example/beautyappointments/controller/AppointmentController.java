package com.example.beautyappointments.controller;

import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.Customer;
import com.example.beautyappointments.repository.AppointmentRepository;
import com.example.beautyappointments.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository repository;
    private final CustomerRepository customerRepository;

    public AppointmentController(AppointmentRepository repository, CustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
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

        Long customerId = appointment.getCustomer().getId();
        Customer customer = customerRepository.findById(customerId).orElseThrow();
         appointment.setCustomer(customer);
        return repository.save(appointment);
    }
    @GetMapping("/customer/{id}")
    public List<Appointment> getByCustomer(@PathVariable Long id){
        return repository.findByCustomerId(id);
    }
}