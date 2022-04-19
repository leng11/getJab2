package com.example.AppointmentService.Appointment;

import com.example.AppointmentService.Exception.ResourceNotFoundException;
import com.example.commonUtility.event.KafkaProducerEventService;
import com.example.commonUtility.event.KafkaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

@Service
public class AppointmentService {
    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    private KafkaProducerEventService<Long> eventProducer;


    public ResponseEntity<List<Appointment>> getAllAppointments(){
        return ResponseEntity.ok(appointmentRepository.findAll());
    }

    public ResponseEntity<Appointment> getAppointment(long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not Found"));
        return ResponseEntity.ok(appointment);
    }

    public ResponseEntity<Map<String,Object>> cancel(long confirmationId){
        Map<String,Object> map = new HashMap<>();
        Appointment appointment = appointmentRepository.findById(confirmationId).orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setCancelId("Cancelled");
        appointment.setStatus("Cancelled");
        appointmentRepository.save(appointment);
        map.put("confirmationId",appointment.getConfirmationId());
        map.put("cancelConfirmId",appointment.getCancelId());
        return ResponseEntity.ok(map);
    }

    public ResponseEntity<Appointment> completed(long confirmationId,int vaccineId,String lot,long inventoryId){
        Appointment appointment = appointmentRepository.findById(confirmationId).orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if(appointment.getStatus().equalsIgnoreCase("completed")) return ResponseEntity.badRequest().body(appointment);

        Map<String, Object> msg = new HashMap<>();
        msg.put("vaccineId",vaccineId);
        msg.put("lot",lot);
        msg.put("inventoryId",inventoryId);
        msg.put("userId",appointment.getUserId());
        String payload = KafkaUtil.mapToPayload(msg);
        eventProducer.sendEvent("shotAdministrated", inventoryId, payload);

        appointment.setStatus("Completed");
        appointmentRepository.save(appointment);


        return ResponseEntity.ok(appointment);
    }

    public ResponseEntity<Map<String,Object>> setReminder(long confirmationId,String notifyType,String notifyString){
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
