package com.example.centerService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.centerService.model.Inventory;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory,Long>{

}
