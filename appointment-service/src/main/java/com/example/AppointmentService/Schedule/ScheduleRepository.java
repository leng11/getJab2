package com.example.AppointmentService.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.stereotype.Repository;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Integer> {
    Optional<List<Schedule>> findByDateBetween(Date startDate,Date endDate);
    Optional<List<Schedule>> findByDate(Date date);
}
