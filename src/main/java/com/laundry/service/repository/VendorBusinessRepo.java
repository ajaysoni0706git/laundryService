package com.laundry.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.service.model.VendorBusiness;

public interface VendorBusinessRepo extends JpaRepository<VendorBusiness, Long> {

	Optional<VendorBusiness> findByUserId(int user_id);
	
	Optional<VendorBusiness> findByUserEmail(String email);

}
