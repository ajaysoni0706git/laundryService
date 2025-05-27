package com.laundry.service.service;

import java.util.Optional;

import com.laundry.service.model.User;

public interface IUserService {
	
	public User saveUser(User user);
	
	public Optional<User> findByEmail(String email);
}
