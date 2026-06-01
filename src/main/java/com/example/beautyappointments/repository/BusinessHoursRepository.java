
package com.example.beautyappointments.repository;
import com.example.beautyappointments.entity.BusinessHours;
import com.example.beautyappointments.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {

    Optional<BusinessHours> findByDayOfWeek(DayOfWeek dayOfWeek);
}