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
    private long scheduleId;

    @Temporal(TemporalType.DATE)
    private Date date;
    private Time time;
    private int centerId;
    private int vaccineTypeId;

//    @Column(name = "totalSlot")
//    private int totalSlot;

    private int openSlot;
    private int completedSlot;
    @JsonManagedReference
    @NonNull
    @OneToMany(mappedBy = "schedule",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Appointment> appointmentList = new ArrayList<>();

    public Schedule(Date date, Time time,int centerId, int vaccineTypeId, int openSlot) {
        this.date = date;
        this.centerId = centerId;
        this.vaccineTypeId = vaccineTypeId;
        this.time = time;
        this.openSlot = openSlot;
    }
//    public void setTotalSlots(){
//        this.totalSlot= this.openSlot+this.completedSlot;
//    }
    public void setOpenSlots(){
        int openSlots = (int) appointmentList.stream()
                .filter(appointment -> appointment.getStatus().equalsIgnoreCase("open"))
                .count();
        this.openSlot = this.openSlot+openSlots;
    }
    public void decOpenSlot(){
        this.openSlot--;
    }
    public void setCompletedSlots(){
        int completedSlots = (int) appointmentList.stream()
                .filter(appointment -> appointment.getStatus().equalsIgnoreCase("completed"))
                .count();
        this.completedSlot = completedSlots;
    }
}
