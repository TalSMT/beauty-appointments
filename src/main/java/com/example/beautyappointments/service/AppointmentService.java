package com.example.beautyappointments.service;

import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.BusinessHours;
import com.example.beautyappointments.entity.Customer;
import com.example.beautyappointments.repository.AppointmentRepository;
import com.example.beautyappointments.repository.BusinessHoursRepository;
import com.example.beautyappointments.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final BusinessHoursRepository businessHoursRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              CustomerRepository customerRepository,
                              BusinessHoursRepository businessHoursRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.businessHoursRepository = businessHoursRepository;
    }

    //בדיקת תור בשעות הפעילות
    private void validateWorkingHours(Appointment appointment) {
        LocalDateTime dateTime = appointment.getDateTime();
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        BusinessHours hours = businessHoursRepository.findByDayOfWeek(day).orElseThrow(() ->
                new RuntimeException("Business is close on this day"));
        if (time.isBefore(hours.getStartTime()) || (time.isAfter(hours.getEndTime()))) {
            throw new RuntimeException("Appointment is outside working hours");
        }
    }

    // בדיקת כפל תורים

    private void validNoDuplicateAppointment(Appointment appointment){
        boolean exists = appointmentRepository.existsByDateTime(appointment.getDateTime());
            if(exists){
                throw new RuntimeException("Ther is already an appointment at this time");
            }
        }

    // קבלת כל התורים
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    // קבלת תור לפי id
    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND
                                ,"Appointment not found"));
    }

    // יצירת תור חדש
    public Appointment create(Appointment appointment) {
        validateWorkingHours(appointment);
        validNoDuplicateAppointment (appointment);
        Long customerId = appointment.getCustomer().getId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        ,"Customer not found"));

        appointment.setCustomer(customer);

        return appointmentRepository.save(appointment);
    }

    // עדכון תור
    public Appointment update(Long id, Appointment updated) {

        Appointment existing = getById(id);

        existing.setTreatmentName(updated.getTreatmentName());
        existing.setPrice(updated.getPrice());
        existing.setDateTime(updated.getDateTime());

        return appointmentRepository.save(existing);
    }

    // מחיקת תור
    public void delete(Long id) {

        if (!appointmentRepository.existsById(id)) {
            throw new  ResponseStatusException(HttpStatus.NOT_FOUND
                    ,"Appointment not found");
        }

        appointmentRepository.deleteById(id);
    }
    public Double getTotalPriceByCustomer(Long customerId){
        List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);
        return appointments.stream().mapToDouble(Appointment::getPrice).sum();

    }
}