package com.example.AppointmentService.Appointment;

import com.example.AppointmentService.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

@Service
public class AppointmentService {
    @Autowired
    AppointmentRepository appointmentRepository;

    public ResponseEntity<List<Appointment>> getAllAppointments(){
        return ResponseEntity.ok(appointmentRepository.findAll());
    }

    public ResponseEntity<Appointment> getAppointment(int id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not Found"));
        return ResponseEntity.ok(appointment);
    }

    public ResponseEntity<Map<String,Object>> cancel(int confirmationId){
        Map<String,Object> map = new HashMap<>();
        Appointment appointment = appointmentRepository.findById(confirmationId).orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setCancelId("Cancelled");
        appointment.setStatus("Cancelled");
        appointmentRepository.save(appointment);
        map.put("confirmationId",appointment.getConfirmationId());
        map.put("cancelConfirmId",appointment.getCancelId());
        return ResponseEntity.ok(map);
    }

    public ResponseEntity<Appointment> completed(int confirmationId){
        Appointment appointment = appointmentRepository.findById(confirmationId).orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setStatus("Completed");
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(appointment);
    }

    public ResponseEntity<Map<String,Object>> setReminder(int confirmationId,String notifyType,String notifyString){
        Map<String,Object> map = new HashMap<>();
        Appointment appointment = appointmentRepository.findById(confirmationId).orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setNotifyType(notifyType);
        appointment.setNotifyString(notifyString);
        appointmentRepository.save(appointment);
        map.put("confirmationId",confirmationId);
        map.put("notifyType",notifyType);
        map.put("notifyString",notifyString);
        return ResponseEntity.ok(map);
    }
}
