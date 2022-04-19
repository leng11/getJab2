package com.example.AppointmentService.Schedule;

import com.example.AppointmentService.Appointment.Appointment;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleId;

    @Temporal(TemporalType.DATE)
    private Date date;
    private Time time;
    private int centerId;
    private int vaccineTypeId;

    @Column(name = "totalSlot")
    @Formula("(SELECT COUNT(*) FROM APPOINTMENT a WHERE a.schedule_id = schedule_id)")
    private int totalSlot;

    @Formula("(SELECT COUNT(*) FROM APPOINTMENT a WHERE a.schedule_id = schedule_id AND a.status = 'open')")
    private int openSlot;
    @Formula("(SELECT COUNT(*) FROM APPOINTMENT a WHERE a.schedule_id = schedule_id AND a.status = 'completed')")
    private int completedSlot;
    @JsonManagedReference
    @NonNull
    @OneToMany(mappedBy = "schedule",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Appointment> appointmentList = new ArrayList<>();

    public Schedule(Date date, Time time,int centerId, int vaccineTypeId) {
        this.date = date;
        this.centerId = centerId;
        this.vaccineTypeId = vaccineTypeId;
        this.time = time;

    }
}
