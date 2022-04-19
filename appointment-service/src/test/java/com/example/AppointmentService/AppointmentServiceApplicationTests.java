package com.example.AppointmentService;

import com.example.AppointmentService.Appointment.Appointment;
import com.example.AppointmentService.Appointment.AppointmentController;

import com.example.AppointmentService.Appointment.AppointmentRepository;
import com.example.AppointmentService.Schedule.Schedule;
import com.example.AppointmentService.Schedule.ScheduleController;
import com.example.AppointmentService.Schedule.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppointmentServiceApplicationTests {

	@Autowired
	private AppointmentController appointmentController;

	@Autowired
	private ScheduleController scheduleController;

	@Test
	void contextLoads() {
	}

	@Test
	public void appointmentIdTest(){
		ResponseEntity<Appointment> response = appointmentController.getAppointment(1);
		assertEquals(1,response.getBody().getConfirmationId());
	}
	@Test
	public void reportTest(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(2022,4,6);
		ResponseEntity<List<Map<String,Object>>> response = scheduleController.report(new Date(calendar.getTimeInMillis()));
		assertEquals(1,response.getBody().get(0).get("shotsAdministered"));
		assertEquals(0,response.getBody().get(1).get("shotsAdministered"));
	}
	@Test
	public void setReminderTest(){
		ResponseEntity<Map<String,Object>> response = appointmentController.setReminder(1,"email","test@email.com");
		assertEquals("test@email.com",response.getBody().get("notifyString"));
		assertEquals("email",response.getBody().get("notifyType"));
		assertEquals(1,response.getBody().get("confirmationId"));
	}
	@Test
	public void cancelTest(){
		ResponseEntity<Map<String,Object>> response = appointmentController.cancel(1);
		assertEquals(1,response.getBody().get("confirmationId"));
		assertEquals("Cancelled",response.getBody().get("cancelConfirmId"));
	}

	@Test
	public void bookTest(){
		ResponseEntity<Map<String,Object>> response = scheduleController.book(2,2);
		assertEquals(2,response.getBody().get("centerId"));
		assertEquals(true,response.getBody().get("booked"));
		assertEquals(3,response.getBody().get("confirmId"));
	}

	@Test
	public void getFreeSlotTest(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(2022,4,6);
		ResponseEntity<List<Schedule>> response = scheduleController.getFreeSlot(new Date(calendar.getTimeInMillis()),1,1);
		assertTrue(response.getBody().get(0).getOpenSlot() >0);
	}
}
