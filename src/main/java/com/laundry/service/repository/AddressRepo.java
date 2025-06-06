package com.laundry.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.laundry.service.model.Address;
import com.laundry.service.model.User;

public interface AddressRepo extends JpaRepository<Address, Long> {

	Optional<Address> findById(Long address_id);

	Optional<Address> findByUser_IdAndIsDefault(Long userId, Boolean isDefault);
	
	Optional<Address> findByUserAndIsDefaultTrue(User user);

	/*@Query("SELECT a FROM Address a WHERE a.user.id = ?1 AND a.is_default = true")
	Optional<Address> getDefaultAddress(long user_id);*/
	
}
