package com.example.AppointmentService.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.AppointmentService.Appointment.Appointment;
import com.example.AppointmentService.Appointment.AppointmentRepository;
import com.example.AppointmentService.Exception.ResourceNotFoundException;
import com.example.commonUtility.event.KafkaConsumerEventService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public ScheduleService(@Autowired final IncomingEvent incomingEvent,
					@Value("${spring.application.event.incoming.topic.restock}") final String restockTopic,
					@Value("${spring.application.event.incoming.topic.reminder}") final String reminderTopic
					) {
		KafkaConsumerEventService.addHandler(restockTopic, incomingEvent);
		KafkaConsumerEventService.addHandler(reminderTopic, incomingEvent);
    }

    public ResponseEntity<List<Schedule>> getAllSchedules(){
        return ResponseEntity.ok(scheduleRepository.findAll());
    }
    public ResponseEntity<Schedule> getSchedule(long id){
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Schedule not Found"));
        return ResponseEntity.ok(schedule);
    }
    public ResponseEntity<List<Schedule>> getByDate(Date date) {
        List<Schedule> scheduleList = scheduleRepository.findByDate(date).orElseThrow(() ->new ResourceNotFoundException("Schedules not found"));
        if(scheduleList.isEmpty()) throw new ResourceNotFoundException("Schedules not found");
        return ResponseEntity.ok(scheduleList);
    }
    public ResponseEntity<List<Schedule>> getFreeSlot(Date date, int centerId,int vaccineId){
        List<Date> startAndEndDates = getWeek(date);
        log.info(""+startAndEndDates);
        List<Schedule> scheduleList = scheduleRepository.findByDateBetween(startAndEndDates.get(0),startAndEndDates.get(1)).orElseThrow(() -> new ResourceNotFoundException("Schedules not found"));
        if(scheduleList.isEmpty()) throw new ResourceNotFoundException("No Schedules within given parameters");

        List<Schedule> newList = scheduleList.stream()
                .filter(schedule -> schedule.getCenterId() ==centerId && schedule.getVaccineTypeId()==vaccineId && schedule.getOpenSlot()>0).collect(Collectors.toList());

        if(newList.isEmpty()) throw new ResourceNotFoundException("No Schedules within given parameters");

        return ResponseEntity.ok(newList);
    }
    public ResponseEntity<List<Map<String,Object>>> report(Date date){
        List<Schedule> scheduleList = scheduleRepository.findByDate(date).orElseThrow(() -> new ResourceNotFoundException("Schedule not Found"));
        if(scheduleList.isEmpty()) throw new ResourceNotFoundException("Schedules not found");

        List<Map<String,Object>> reportList = new ArrayList<>();
        for(Schedule schedule : scheduleList){
            Map<String, Object> map= new HashMap<>();
            map.put("center",schedule.getCenterId());
            map.put("vaccineTypeId",schedule.getVaccineTypeId());
            map.put("shotsAdministered",schedule.getCompletedSlot());
            reportList.add(map);
        }
        log.info(reportList.toString());
        return ResponseEntity.ok(reportList);
    }

    public ResponseEntity<Map<String,Object>> book(long scheduleId, int userId){
        Map<String,Object> map = new HashMap<>();
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        List<Appointment> appointmentList = schedule.getAppointmentList();
        Optional<Appointment> appointmentToSchedule = appointmentList.stream()
                .filter(appointment -> appointment.getStatus().equalsIgnoreCase("open"))
                .findFirst();
        appointmentToSchedule.ifPresent(appointment -> {
            appointment.setUserId(userId);
            appointment.setStatus("Scheduled");
        });
        if(appointmentToSchedule.isPresent()){
            appointmentRepository.save(appointmentToSchedule.get());
            map.put("booked",true);
            map.put("centerId",schedule.getCenterId());
            map.put("confirmId",appointmentToSchedule.get().getConfirmationId());
        }else{
            map.put("booked",false);
            map.put("error", "No appointments available");
        }
        return ResponseEntity.ok(map);
    }

    private List<Date> getWeek(Date date){
        log.info("Date: " +date.toString());
        List<Date> dateList = new ArrayList<>();
        Date startDate;
        Date endDate;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);

        startDate = new Date(calendar.getTimeInMillis());
        dateList.add(startDate);
        calendar.add(Calendar.DAY_OF_MONTH,7);

        endDate = new Date(calendar.getTimeInMillis());
        dateList.add(endDate);
        return dateList;
    }
}
