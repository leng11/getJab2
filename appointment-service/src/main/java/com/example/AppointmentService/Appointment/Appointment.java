package com.example.AppointmentService.Appointment;

import com.example.AppointmentService.Schedule.Schedule;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Table
@Entity
@Data
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int confirmationId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "scheduleId")
    private Schedule schedule;

    private int userId;
    private int centerId;
    private String notifyType;
    private String notifyString;
    private String status;
    private String cancelId;

    public Appointment(Schedule schedule,int userId, int centerId, String notifyType, String notifyString, String status, String cancelId) {
        this.schedule = schedule;
        this.userId = userId;
        this.centerId = centerId;
        this.notifyType = notifyType;
        this.notifyString = notifyString;
        this.status = status;
        this.cancelId = cancelId;
    }
}
