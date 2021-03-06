package com.example.AppointmentService.Schedule;

import java.sql.Time;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.AppointmentService.Appointment.Appointment;
import com.example.AppointmentService.Appointment.AppointmentRepository;
import com.example.AppointmentService.Exception.ResourceNotFoundException;
import com.example.commonUtility.event.EventHandler;
import com.example.commonUtility.event.KafkaConsumerEventService;
import com.example.commonUtility.event.KafkaUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IncomingEvent implements EventHandler {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Value("${spring.application.event.incoming.topic.restock}")
    private String restockTopic;
    
	@Value("${spring.application.event.incoming.topic.reminder}")
	private String reminderTopic;

    @Override
    public void onEvent(Map<String, Object> headers, String payload) {
        try{
            Map<String, Object> msgMap = KafkaUtil.payloadToMap(payload);
            String topic = (String) headers.get(KafkaConsumerEventService.TOPIC_ATTR);
            //Restock topic
            if(topic.equalsIgnoreCase(restockTopic)){
                log.info("started processing msg from topic: {}", headers.get(KafkaConsumerEventService.TOPIC_ATTR));
                // inventoryId:Long,vaccineId:Long, lot:String, lotSize:Integer, centerId:Long
                Object inventoryId = msgMap.get("inventoryId");
                Object vaccineId = msgMap.get("vaccineId");
                Object lot = msgMap.get("lot");
                Object lotSize = msgMap.get("lotSize");
                Object centerId = msgMap.get("centerId");

                Schedule schedule = scheduleRepository.findFirstByOrderByDateDesc().orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
                Date date = schedule.getDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE,1);
                Date newDate = new Date(calendar.getTimeInMillis());

                Schedule newSchedule = new Schedule(newDate,new Time(12,30,0),(int) centerId,(int) vaccineId,(int) lotSize);
                scheduleRepository.save(newSchedule);
            }
            //Publish reminder topic
            if(topic.equalsIgnoreCase(reminderTopic)){
                log.info("started processing msg from topic: {}", headers.get(KafkaConsumerEventService.TOPIC_ATTR));
            }
        }catch(NoSuchElementException nse){

        }catch (RuntimeException re){
            log.error("failed to convert msg content", re);
        }
    }
}
