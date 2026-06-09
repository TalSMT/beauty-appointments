package com.example.beautyappointments.dto;

import java.time.LocalTime;

public class TimeSlotDTO {

    private LocalTime time;
    private boolean available;

    public TimeSlotDTO(LocalTime time, boolean available){
        this.available = available;
        this.time = time;
    }
    public LocalTime getTime(){
        return time;
    }
    public boolean isAvailable(){
        return available;
    }
}
