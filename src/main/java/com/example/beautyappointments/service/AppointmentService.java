package com.example.beautyappointments.service;

import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.Customer;
import com.example.beautyappointments.repository.AppointmentRepository;
import com.example.beautyappointments.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              CustomerRepository customerRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
    }

    // קבלת כל התורים
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    // קבלת תור לפי id
    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    // יצירת תור חדש
    public Appointment create(Appointment appointment) {

        Long customerId = appointment.getCustomer().getId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

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
            throw new RuntimeException("Appointment not found");
        }

        appointmentRepository.deleteById(id);
    }
    public Double getTotalPriceByCustomer(Long customerId){
        List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);
        return appointments.stream().mapToDouble(Appointment::getPrice).sum();

    }
}