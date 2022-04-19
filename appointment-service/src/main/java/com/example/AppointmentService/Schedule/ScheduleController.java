package com.example.AppointmentService.Schedule;

import com.example.AppointmentService.Appointment.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/vaccine")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/schedules")
    private ResponseEntity<List<Schedule>> getAllSchedules(){
        return scheduleService.getAllSchedules();
    }
    @GetMapping("/schedule/{id}")
    public ResponseEntity<Schedule> getAppointment(@PathVariable int id){
        return scheduleService.getSchedule(id);
    }
    @GetMapping("/schedule")
    public ResponseEntity<List<Schedule>> getByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        return scheduleService.getByDate(date);
    }
    @PostMapping("/appointments/book")
    public ResponseEntity<Map<String,Object>> book(@RequestParam int scheduleId, @RequestParam int userId){
        return scheduleService.book(scheduleId,userId);
    }
    @GetMapping("/appointments/report")
    public ResponseEntity<List<Map<String,Object>>> report(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        return scheduleService.report(date);
    }
    @GetMapping("/appointments/getFreeSlot")
    public ResponseEntity<List<Schedule>> getFreeSlot(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @RequestParam int centerId,@RequestParam int vaccineId){
        return scheduleService.getFreeSlot(date, centerId, vaccineId);
    }
}
