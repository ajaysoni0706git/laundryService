package com.laundry.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.service.model.ItemMaster;

public interface ItemMasterRepo extends JpaRepository<ItemMaster, Long> {

	// Additional query methods can be defined here if needed

}
