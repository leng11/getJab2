package com.example.AppointmentService.Appointment;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/vaccine")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/appointments/list")
    public ResponseEntity<List<Appointment>> getAll(){
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/appointment/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable int id){
        return appointmentService.getAppointment(id);
    }
    @PutMapping("/appointments/cancel")
    public ResponseEntity<Map<String,Object>> cancel(@RequestParam int confirmationId){
        return appointmentService.cancel(confirmationId);
    }
    @PutMapping("/appointments/completed")
    public ResponseEntity<Appointment> completed(@RequestParam int confirmationId,@RequestParam int vaccineId,@RequestParam String lot,@RequestParam int inventoryId){
        return appointmentService.completed(confirmationId);
    }
    @PutMapping("/appointments/setReminder")
    public ResponseEntity<Map<String,Object>> setReminder(@RequestParam int confirmationId,@RequestParam String notifyType,@RequestParam String notifyString){
        return appointmentService.setReminder(confirmationId, notifyType, notifyString);
    }
}
