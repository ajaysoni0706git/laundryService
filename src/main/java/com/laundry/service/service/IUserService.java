package com.laundry.service.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.dto.UserDTO;
import com.laundry.service.model.User;

public interface IUserService {
	
	public User saveUser(User user);
	
	public Optional<User> findByEmail(String email);
	
	public Optional<User> findById(int id);
	
	public String uploadProfileImage(int userId, MultipartFile file);

	public User updateUser(int id, UserDTO dto);
}
