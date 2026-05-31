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

        Long customerId = appointment.getCustomer().getId();
        Customer customer = customerRepository.findById(customerId).orElseThrow();
         appointment.setCustomer(customer);
        return repository.save(appointment);
    }
    @GetMapping("/customer/{id}")
    public List<Appointment> getByCustomer(@PathVariable Long id){
        return repository.findByCustomerId(id);
    }
    @GetMapping("/customer/{id}/total")
    public Double getTotalPrice(@PathVariable Long id) {

        List<Appointment> appointments =
                repository.findByCustomerId(id);

        return appointments.stream()
                .mapToDouble(Appointment::getPrice)
                .sum();
    }
    @DeleteMapping ("/{id}")
    public  void deleteAppointment(@PathVariable Long id){
        if (!repository.existsById(id)){
            throw new RuntimeException("Appointment not found");
        }
        repository.deleteById(id);
    }
    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment updatedAppointment){
        Appointment existing = repository.findById(id).orElseThrow(()->new RuntimeException("not found"));
        existing.setTreatmentName(updatedAppointment.getTreatmentName());
        existing.setPrice(updatedAppointment.getPrice());
        existing.setDateTime(updatedAppointment.getDateTime());
        return repository.save(existing);
    }
}