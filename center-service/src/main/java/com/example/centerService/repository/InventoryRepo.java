package com.example.centerService.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.centerService.model.Inventory;

@Repository
@Transactional
public interface InventoryRepo extends JpaRepository<Inventory,Long>{

}
