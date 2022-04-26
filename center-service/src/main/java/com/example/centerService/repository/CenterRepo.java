package com.example.centerService.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.centerService.model.Center;

@Repository
@Transactional
public interface CenterRepo extends JpaRepository<Center,Long> {
    public Integer deleteByName(String name);
}
