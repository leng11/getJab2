package com.example.AppointmentService.Schedule;

import com.example.AppointmentService.Appointment.Appointment;
import com.example.AppointmentService.Exception.ResourceNotFoundException;
import com.example.commonUtility.event.EventHandler;
import com.example.commonUtility.event.KafkaConsumerEventService;
import com.example.commonUtility.event.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
@Slf4j
public class IncomingEvent implements EventHandler {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void onEvent(Map<String, Object> headers, String payload) {
        try{
            Map<String, Object> msgMap = KafkaUtil.payloadToMap(payload);
            String topic = (String) headers.get(KafkaConsumerEventService.TOPIC_ATTR);
            //Restock topic
            if(topic.equalsIgnoreCase("vaccineInventoryTopic")){
                log.info("started processing msg from topic: {}", headers.get(KafkaConsumerEventService.TOPIC_ATTR));
                // inventoryId:Long,vaccineId:Long, lot:String, lotSize:Integer, centerId:Long
                Object inventoryId = msgMap.get("inventoryId");
                Object vaccineId = msgMap.get("vaccineId");
                Object lot = msgMap.get("lot");
                Object lotSize = msgMap.get("lotSize");
                Object centerId = msgMap.get("centerId");

                Schedule schedule = scheduleRepository.findByVaccineTypeId((int) vaccineId).orElseThrow(() -> new ResourceNotFoundException("Schedule not found with vaccine id " +vaccineId));
                List<Appointment> appointmentList = new ArrayList<>();
                for(int i = 0; i<(int)lotSize;i++){
                    appointmentList.add(new Appointment(schedule,0,(int) centerId,"none","none","open",null));
                }
                appointmentList.addAll(appointmentList);
            }
            //Publish reminder topic
            if(topic.equalsIgnoreCase("publishReminderTopic")){
                log.info("started processing msg from topic: {}", headers.get(KafkaConsumerEventService.TOPIC_ATTR));
            }
        }catch(NoSuchElementException nse){

        }catch (RuntimeException re){
            log.error("failed to convert msg content", re);
        }
    }
}
