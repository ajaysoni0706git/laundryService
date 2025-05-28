package com.laundry.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.service.model.Address;

public interface AddressRepo extends JpaRepository<Address, Long> {

	Optional<Address> findById(Long address_id);

}
