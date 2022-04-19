package com.example.centerService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.centerService.model.Vaccine;

@Repository
public interface VaccineRepo extends JpaRepository<Vaccine,Long> {

}
