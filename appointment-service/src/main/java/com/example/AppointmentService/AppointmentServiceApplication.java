package com.example.AppointmentService;

import com.example.AppointmentService.Appointment.Appointment;
import com.example.AppointmentService.Appointment.AppointmentRepository;
import com.example.AppointmentService.Schedule.Schedule;
import com.example.AppointmentService.Schedule.ScheduleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
public class AppointmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppointmentServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner testData(AppointmentRepository appointmentRepository, ScheduleRepository scheduleRepository){
		return args -> {
			Calendar calendar = Calendar.getInstance();
			calendar.set(2022,4,6);
			Time time = new Time(5,30,0);
			Schedule schedule = new Schedule(new Date(calendar.getTimeInMillis()),time,1,1);
			Schedule schedule2 = new Schedule(new Date(calendar.getTimeInMillis()),new Time(6,0,0),2,2);
			Appointment appointment = new Appointment(schedule,0,1,"none","none","open",null);
			Appointment appointment2 = new Appointment(schedule,1,1,"none","none","completed",null);
			Appointment appointment3 = new Appointment(schedule2,0,1,"none","none","open",null);
			Appointment appointment4 = new Appointment(schedule,0,1,"none","none","open",null);

			scheduleRepository.save(schedule);
			scheduleRepository.save(schedule2);
			appointmentRepository.save(appointment);
			appointmentRepository.save(appointment2);
			appointmentRepository.save(appointment3);
			appointmentRepository.save(appointment4);

		};
	}
}
