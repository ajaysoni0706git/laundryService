package com.laundry.service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.dto.UserDTO;
import com.laundry.service.model.Address;
import com.laundry.service.model.User;
import com.laundry.service.repository.UserRepo;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepo repo;
	
	
	
	@Override
	public User saveUser(User user) {
	    if (repo.existsByEmail(user.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }
	    
	if (user.getAddress() == null || user.getAddress().isEmpty()) {
		throw new RuntimeException("At least one address must be provided");
		
	}else {
		Address addr = user.getAddress().get(0);
		
		addAddress(addr, user);	
	}

	    String password = user.getPassword();
	    if (password == null || password.trim().isEmpty()) {
	        throw new RuntimeException("Password cannot be empty");
	    }
	    String encodedPassword = passwordEncoder.encode(password);
	    user.setPassword(encodedPassword);

	    user.setCreated_date(java.time.LocalDateTime.now());
	    user.setIs_active(true);
	    user.setIs_disabled(false);
	    user.setIs_verified(false);

	    return repo.save(user);
	}

	private void addAddress(Address addr, User user) {
		System.out.println(addr);
	    Boolean isDefault = addr.getIsDefault();
	    if (Boolean.TRUE.equals(isDefault)) {
	        // Set all other addresses to false except the current one
	        if (user.getAddress() != null) {
	            for (Address a : user.getAddress()) {
	                if (a != addr) {
	                    a.setIsDefault(false);
	                }
	            }
	        }
	    }

	    addr.setUser(user);
	    
	}


	@Override
    public UserDetails loadUserByUsername(String email) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
    /*    Set<GrantedAuthority> authorities = new HashSet<>();
        String role = user.getRole();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        
        return new org.springframework.security.core.userdetails.User(
        		email,
        		user.getPassword(),
				authorities
				);*/
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().toUpperCase())
                .build();
    }

	@Override
	public Optional<User> findByEmail(String email) {
		Optional<User> userOpt = repo.findByEmail(email);
		return userOpt;
	}
	
	@Override
	public Optional<User> findById(int id) {
		Optional<User> userOpt = repo.findById(id);
		return userOpt;
	}
	
	
	@Value("${user.image.upload-dir}")
	private String uploadDir;

	@Override
	public String uploadProfileImage(int userId, MultipartFile file) {
	    try {
	    	
	        User user = repo.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	    	
	        // Create user-specific path
	        String folderPath = uploadDir  + "user_" + userId + "/profile_image/";
	        Path uploadPath = Paths.get(folderPath);

	        // Create directory if it doesn't exist
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }
	        
	        // Extract file extension (e.g., .jpg, .png)
	        String originalFilename = file.getOriginalFilename();
	        String extension = "";

	        if (originalFilename != null && originalFilename.contains(".")) {
	            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	        }

	        // Use fixed filename
	        String filename = userId + "profile_pic" + extension;
	        Path filePath = uploadPath.resolve(filename);

	        // Overwrite if file exists
	        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	        // Update DB with relative path
	        user.setProfileImagePath(folderPath + filename);
	        repo.save(user);

	        return user.getProfileImagePath();
	        
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to upload profile image: " + e.getMessage());
	    }
	}

	@Override
	public User updateUser(int id, UserDTO dto) {
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
		if (dto.getFirst_name() != null) {
			user.setFirst_name(dto.getFirst_name());
		}
		if (dto.getLast_name() != null) {
			user.setLast_name(dto.getLast_name());
		}
		if (dto.getPassword() != null) {
			String encodedPassword = passwordEncoder.encode(dto.getPassword());
			user.setPassword(encodedPassword);
		}
		if (dto.getPhone() != null) {
			user.setPhone(dto.getPhone());
		}
		if (dto.getRole() != null) {
			user.setRole(dto.getRole());
		}
		if (dto.getProfileImagePath() != null) {
			user.setProfileImagePath(dto.getProfileImagePath());
		}
		if (dto.getIs_vendor() != null && dto.getIs_vendor() && dto.getRole().equalsIgnoreCase("VENDOR")) {
			user.setIs_vendor(dto.getIs_vendor());
		}
		
		user.setUpdated_date(java.time.LocalDateTime.now());
		
		return repo.save(user);
	}

}
