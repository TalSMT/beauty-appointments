package com.example.beautyappointments.service;

import com.example.beautyappointments.dto.AppointmentDTO;
import com.example.beautyappointments.dto.TimeSlotDTO;
import com.example.beautyappointments.entity.*;
import com.example.beautyappointments.repository.AppointmentRepository;
import com.example.beautyappointments.repository.BusinessHoursRepository;
import com.example.beautyappointments.repository.CustomerRepository;
import com.example.beautyappointments.repository.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.stream.Collectors;


import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final TreatmentRepository treatmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              CustomerRepository customerRepository,
                              BusinessHoursRepository businessHoursRepository, TreatmentRepository treatmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.businessHoursRepository = businessHoursRepository;
        this.treatmentRepository = treatmentRepository;
    }

    //בדיקת תור בשעות הפעילות
    private void validateWorkingHours(Appointment appointment) {
        LocalDateTime dateTime = appointment.getDateTime();
        DayOfWeek day = dateTime.getDayOfWeek();
        List<BusinessHours> hours = businessHoursRepository.findByDayOfWeek(day);
        if (hours.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Business is close on this day");
        }

       LocalTime appointmentStart = appointment.getDateTime().toLocalTime();
       LocalTime appointmentEnd = appointmentStart.plusMinutes(appointment.getTreatment().getDurationMinutes());
        boolean valid = hours.stream().anyMatch(h-> !appointmentStart.isBefore(h.getStartTime())&& !appointmentEnd.isAfter(h.getEndTime()));
        if (!valid){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Appointment is outside business hours");
        }
    }

    // בדיקת כפל תורים

    private void validNoOverlap(LocalDateTime start, LocalDateTime end){

        List<Appointment> existing = appointmentRepository.findByDateTimeBetween(start.toLocalDate().atStartOfDay(),start.toLocalDate().atTime(23,59));
        boolean overlap = existing.stream().anyMatch(a-> {
            if (a.getTreatment() == null){
                throw new RuntimeException("NuLL TREATMENT in appointment id ="+a.getId());
            }
            LocalDateTime aStart = a.getDateTime();
            LocalDateTime aEnd = aStart.plusMinutes(a.getTreatment().getDurationMinutes());
            return start.isBefore(aEnd) && aStart.isBefore(end); });
            if(overlap){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Time slot already taken");
            }

        }


    //פונקציית המרה
    private AppointmentDTO toDto(Appointment appointment){
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        if(appointment.getTreatment()!= null) {
            dto.setTreatmentId(appointment.getTreatment().getId());
        }
        dto.setDateTime(appointment.getDateTime());

        if(appointment.getCustomer() != null){
            dto.setCustomerId(appointment.getCustomer().getId());
        }
        return dto;
    }
    private Appointment toEntity (AppointmentDTO dto){
        Appointment appointment = new Appointment();
        Treatment treatment = treatmentRepository.findById(dto.getTreatmentId()).orElseThrow();
        appointment.setTreatment(treatment);
        /* appointment.setPrice(dto.getPrice());*/
        appointment.setDateTime(dto.getDateTime());
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow(()
        -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found"));
        appointment.setCustomer(customer);
        return appointment;
    }

    private Appointment getAppointmentById (Long id)
    {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND
                                ,"Appointment not found"));

    }
    // קבלת כל התורים
    public List<AppointmentDTO> getAll() {
        return appointmentRepository.findAll().stream().map(this::toDto).toList();
    }
    // קבלת תור לפי id
    public AppointmentDTO getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND
                                , "Appointment not found"));
        return toDto(appointment);
    }

    // יצירת תור חדש
    public AppointmentDTO create(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setDateTime(dto.getDateTime());
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer not found"));
        Treatment treatment = treatmentRepository.getReferenceById(dto.getTreatmentId());
      /*  System.out.println("STEP 1 - treatment loaded");

        System.out.println("treatment price :" +treatment.getPrice());*/

        appointment.setCustomer(customer);
        appointment.setTreatment(treatment);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

       /* System.out.println("STEP 2 :before save" );
        System.out.println("appointment treatment class :" +appointment.getTreatment().getClass());

        System.out.println("appointment customer id :" +appointment.getCustomer().getId());*/

        LocalDateTime start = appointment.getDateTime();
        LocalDateTime end = start.plusMinutes(treatment.getDurationMinutes());

        validateWorkingHours(appointment);
        validNoOverlap (start,end);


        /*System.out.println("STEP 3 :saving appointment" );
        System.out.println("Treatment object class=" +appointment.getTreatment().getClass());

        System.out.println("is new?=" +(appointment.getTreatment().getId()==null));*/

        Appointment saved = appointmentRepository.save(appointment);

        //System.out.println("STEP 4 :saved OK, id=" +saved.getId());

        return toDto(saved);
    }

    // עדכון תור
    public Appointment update(Long id, Appointment updated) {

        Appointment existing = appointmentRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        Treatment treatment = treatmentRepository.findById(updated.getTreatment().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Treatment not found"));
        LocalDateTime start = updated.getDateTime();
        LocalDateTime end = start.plusMinutes(treatment.getDurationMinutes());
        validateWorkingHours(updated);
        List<Appointment> existingAppointments = appointmentRepository.findByDateTimeBetween(start.minusHours(1),end.plusHours(1));
        boolean overlap = existingAppointments.stream().filter(a -> !a.getId().equals(id)).anyMatch(a -> {
            LocalDateTime aStart = a.getDateTime();
            LocalDateTime aEnd = aStart.plusMinutes(a.getTreatment().getDurationMinutes());
            return start.isBefore(aEnd) && aStart.isBefore(end);
        });
        if (overlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot already taken");

        }
        Customer customer = customerRepository.findById(updated.getCustomer().getId()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setCustomer(customer);
        existing.setDateTime(updated.getDateTime());
        //existing.setCustomer(updated.getCustomer());
        existing.setTreatment(treatment);
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
    public BigDecimal getTotalPriceByCustomer(Long customerId){
        List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);
        return appointments.stream().map(a->a.getTreatment().getPrice()).reduce(BigDecimal.ZERO,BigDecimal::add);

    }

    //ביטול תור
    public Appointment cancel (Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }
    //הצגת תורים בסטטוס פעיל בלבד
    public List<Appointment> getActiveAppointments(){
        return appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED);

    }
   //הצגת תורים בתאריך ספציפי
   public List<Appointment> getByDate(LocalDate date){
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23,59,59);
        return appointmentRepository.findByDateTimeBetween(startOfDay,endOfDay)
                .stream().filter(a->a.getStatus()==AppointmentStatus.
                        SCHEDULED).collect(Collectors.toList());

    }
   //שעות פנויות בהתאם לבקשה
   public List<TimeSlotDTO> getAvailableSlots(LocalDate date, Long treatmentId) {

       // 1. טיפול
       Treatment treatment = treatmentRepository.findById(treatmentId)
               .orElseThrow(() -> new ResponseStatusException(
                       HttpStatus.NOT_FOUND, "Treatment not found"));

       int duration = treatment.getDurationMinutes();

       // 2. יום בשבוע
       DayOfWeek day = date.getDayOfWeek();

       // 3. משמרות
       List<BusinessHours> shifts =
               businessHoursRepository.findByDayOfWeek(day);

       if (shifts.isEmpty()) {
           throw new ResponseStatusException(
                   HttpStatus.BAD_REQUEST,
                   "Business is closed on this day");
       }

       // 4. תורים קיימים באותו יום (רק פעילים)
       LocalDateTime dayStart = date.atStartOfDay();
       LocalDateTime dayEnd = date.atTime(23, 59);

       List<Appointment> appointments =
               appointmentRepository.findByDateTimeBetween(dayStart, dayEnd)
                       .stream()
                       .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                       .toList();

       List<TimeSlotDTO>slots = new ArrayList<>();

       // 5. מעבר על משמרות
       for (BusinessHours shift : shifts) {

           LocalTime slot = shift.getStartTime();
           LocalTime shiftEnd = shift.getEndTime();

           // 6. יצירת משבצות זמן
           while (!slot.plusMinutes(duration).isAfter(shiftEnd)) {
               final LocalTime slotStart = slot;
               final LocalTime slotEnd = slot.plusMinutes(duration);

               // 7. בדיקת חפיפות
               boolean overlaps = appointments.stream().anyMatch(a -> {

                   LocalTime aStart = a.getDateTime().toLocalTime();
                   LocalTime aEnd = aStart.plusMinutes(
                           a.getTreatment().getDurationMinutes()
                   );

                   return slotStart.isBefore(aEnd) && aStart.isBefore(slotEnd);
               });

               // 8. אם פנוי → מוסיפים
               if (!overlaps) {
                   boolean isAvailable = !overlaps;
                   slots.add(new TimeSlotDTO(slotStart,isAvailable));
               }

               // 9. קפיצה לחצי שעה הבאה
               slot = slot.plusMinutes(30);
           }
       }

       return slots;
   }

    public List<Appointment> search(LocalDate date, AppointmentStatus status, Long customerId) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if(date !=null){
            start = date.atStartOfDay();
            end = date.atTime(23,59);
        }
        List<Appointment> result;
        //לפי תאריך וסטטוס
        if(date != null && status != null){
            result = appointmentRepository.findByDateTimeBetweenAndStatus(start,end,status);
        }
        //תאריך
        else if (date != null){
            result=appointmentRepository.findByDateTimeBetween(start,end);
        }
        //סטטוס
        else if (status!=null) {
            result=appointmentRepository.findByStatus(status);

        }
        //כלום -> הכול
        else {
            result=appointmentRepository.findAll();
        }
        //פילטר לקוח (לאחר שליפה)
        if (customerId != null){
            result =result.stream().filter(a->a.getCustomer().getId().equals(customerId)).toList();
        }
        return result;
    }
}