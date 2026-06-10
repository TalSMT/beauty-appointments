package com.example.beautyappointments.Service;

import com.example.beautyappointments.dto.AppointmentDTO;
import com.example.beautyappointments.entity.*;
import com.example.beautyappointments.repository.AppointmentRepository;
import com.example.beautyappointments.repository.BusinessHoursRepository;
import com.example.beautyappointments.repository.CustomerRepository;
import com.example.beautyappointments.repository.TreatmentRepository;
import com.example.beautyappointments.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private TreatmentRepository treatmentRepository;
    @Mock
    private BusinessHoursRepository businessHoursRepository;
    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private CustomerRepository customerRepository;

    private Treatment createTreatment() {
        Treatment t = new Treatment();
        t.setId(1L);
        t.setDurationMinutes(60);
        return t;
    }

    private Customer createCustomer() {
        Customer c = new Customer();
        c.setId(1L);
        return c;
    }

    private BusinessHours createBusinessHours() {
        BusinessHours bh = new BusinessHours();
        bh.setDayOfWeek(DayOfWeek.TUESDAY);
        bh.setStartTime(LocalTime.of(9, 0));
        bh.setEndTime(LocalTime.of(18, 0));
        return bh;
    }
    @Test
    void shouldThrowConflictWhenAppointmentOverlaps() {

        Treatment treatment = createTreatment();
        Customer customer = createCustomer();
        BusinessHours businessHours = createBusinessHours();

        Appointment existing = new Appointment();
        existing.setId(1L);
        existing.setDateTime(LocalDateTime.of(2026, 6, 9, 10, 0));
        existing.setTreatment(treatment);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(treatmentRepository.getReferenceById(1L))
                .thenReturn(treatment);

        when(businessHoursRepository.findByDayOfWeek(DayOfWeek.TUESDAY))
                .thenReturn(List.of(businessHours));

        when(appointmentRepository.findByDateTimeBetween(any(), any()))
                .thenReturn(List.of(existing));

        AppointmentDTO dto = new AppointmentDTO();
        dto.setDateTime(LocalDateTime.of(2026, 6, 9, 10, 30));
        dto.setTreatmentId(1L);
        dto.setCustomerId(1L);

        assertThrows(ResponseStatusException.class,
                () -> appointmentService.create(dto));
    }



    @Test
    void shouldThrowBadRequestWhenAppointmentOutsideBusinessHours(){
        Treatment treatment = new Treatment();
        treatment.setId(1L);
        treatment.setDurationMinutes(60);

        Appointment existing = new Appointment();
        existing.setId(1L);
        existing.setDateTime(LocalDateTime.of(2026, 06, 9, 10, 00));
        existing.setTreatment(treatment);

        BusinessHours businessHours = new BusinessHours();
        businessHours.setDayOfWeek(DayOfWeek.TUESDAY);
        businessHours.setStartTime(LocalTime.of(9,0));
        businessHours.setEndTime(LocalTime.of(18,0));

        Customer customer = new Customer();
        customer.setId(1L);

        AppointmentDTO dto = new AppointmentDTO();
        dto.setDateTime(LocalDateTime.of(2026, 6, 9, 20, 0));
        dto.setTreatmentId(1L);
        dto.setCustomerId(1L);

        when(customerRepository.findById(1L)).
                thenReturn(Optional.of(customer));



        when(treatmentRepository.getReferenceById(1L)).
                thenReturn(treatment);

        when(businessHoursRepository.findByDayOfWeek(DayOfWeek.TUESDAY)).
                thenReturn(List.of(businessHours));


        ResponseStatusException ex =

        assertThrows(ResponseStatusException.class,() ->
            appointmentService.create(dto));

        assertEquals(HttpStatus.BAD_REQUEST,ex.getStatusCode());
    }


    @Test
    void shouldCreateAppointmentsSuccessfully() {

        // ===== Arrange common objects =====
        Treatment treatment = new Treatment();
        treatment.setId(1L);
        treatment.setDurationMinutes(60);

        Customer customer = new Customer();
        customer.setId(1L);

        BusinessHours businessHours = new BusinessHours();
        businessHours.setDayOfWeek(DayOfWeek.TUESDAY);
        businessHours.setStartTime(LocalTime.of(9, 0));
        businessHours.setEndTime(LocalTime.of(18, 0));

        Appointment existing = new Appointment();
        existing.setId(2L);
        existing.setDateTime(LocalDateTime.of(2026, 6, 9, 17, 0));
        existing.setTreatment(treatment);

        // ===== Mock dependencies =====
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(treatmentRepository.getReferenceById(1L))
                .thenReturn(treatment);

        when(businessHoursRepository.findByDayOfWeek(DayOfWeek.TUESDAY))
                .thenReturn(List.of(businessHours));

        when(appointmentRepository.findByDateTimeBetween(any(), any()))
                .thenReturn(List.of(existing));

        // ===== DTO input =====
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDateTime(LocalDateTime.of(2026, 6, 9, 12, 30));
        dto.setTreatmentId(1L);
        dto.setCustomerId(1L);

        // ===== IMPORTANT FIX: saved entity returned from repository =====
        Appointment saved = new Appointment();
        saved.setId(1L);
        saved.setDateTime(dto.getDateTime());
        saved.setCustomer(customer);
        saved.setTreatment(treatment);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(saved);

        // ===== Act =====
        AppointmentDTO result = appointmentService.create(dto);

        // ===== Assert =====
        assertNotNull(result);
        assertEquals(dto.getCustomerId(), result.getCustomerId());
        assertEquals(dto.getDateTime(), result.getDateTime());

        verify(appointmentRepository).save(any(Appointment.class));
    }
}
