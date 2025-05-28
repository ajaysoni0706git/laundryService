package com.laundry.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.service.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
	
	Optional<User> findById(Integer id);
}
