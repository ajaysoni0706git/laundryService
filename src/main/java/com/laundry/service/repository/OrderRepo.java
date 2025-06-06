package com.laundry.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.service.model.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {
	
	Optional<Order> findById(Long id);
	
	Optional<Order> findByOrderNumber(String orderNumber);

	List<Order> findAllByCustomerEmail(String email);

	// Custom query methods can be defined here if needed
	// For example, to find orders by user ID or status

}
