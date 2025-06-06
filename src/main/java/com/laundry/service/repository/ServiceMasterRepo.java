package com.laundry.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.laundry.service.model.ServiceMaster;

public interface ServiceMasterRepo extends JpaRepository<ServiceMaster, Long> {

    @Query("SELECT s FROM ServiceMaster s JOIN FETCH s.items")
    List<ServiceMaster> getAllServicesAndItems();

}