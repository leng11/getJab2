package com.example.centerService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.centerService.model.Center;

@Repository
public interface CenterRepo extends JpaRepository<Center,Long> {
}
