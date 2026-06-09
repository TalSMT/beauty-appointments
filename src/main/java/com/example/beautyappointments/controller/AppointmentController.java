package com.example.beautyappointments.controller;

import com.example.beautyappointments.dto.AppointmentDTO;
import com.example.beautyappointments.dto.TimeSlotDTO;
import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.AppointmentStatus;
import com.example.beautyappointments.service.AppointmentService;
import org.springframework.stereotype.Service;

import com.example.beautyappointments.entity.Customer;
import com.example.beautyappointments.repository.AppointmentRepository;
import com.example.beautyappointments.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDate;
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;


    public AppointmentController(AppointmentService service) {
        this.service = service;

    }

    // GET - מחזיר את כל התורים
   /* @GetMapping
    public List<AppointmentDTO> getAllAppointments() {

        return service.getAll();
    }*/
    //מחזיר סכום הפריטים עבור התור
    @GetMapping("/customer/{id}/total")
    public BigDecimal getTotalPrice(@PathVariable Long id){
        return service.getTotalPriceByCustomer(id);
    }



    // POST - מוסיף תור חדש
    @PostMapping
    /*public Appointment Create(@RequestBody Appointment appointment) {
        return service.create(appointment);
    }*/
    public AppointmentDTO Create(@RequestBody AppointmentDTO appointment) {
        System.out.println(">>>CONTROLLER HIT");
        return service.create(appointment);
    }
    @GetMapping("/{id}")
    public AppointmentDTO getById(@PathVariable Long id){
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
    @PutMapping("/{id}/cancel")
    public Appointment cancel (@PathVariable Long id){
        return service.cancel(id);
    }
    @GetMapping("/active")
    public  List<Appointment> getAllActive(){
        return service.getActiveAppointments();
    }
    @GetMapping("/date")
    public  List<Appointment> getByDate(@RequestParam LocalDate date){
        return service.getByDate(date);
    }
    @GetMapping("/available")
    public List<TimeSlotDTO> getAvailable (@RequestParam LocalDate date, @RequestParam Long treatmentId){
        return service.getAvailableSlots(date,treatmentId);
    }
    @GetMapping
    public List<Appointment> search (@RequestParam(required = false) LocalDate date, @RequestParam(required = false) AppointmentStatus status, @RequestParam(required = false) Long customerId){
        return service.search(date,status,customerId);
    }
}