package com.laundry.service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.config.JwtUtil;
import com.laundry.service.dto.AuthRequest;
import com.laundry.service.dto.ImageUploadDTO;
import com.laundry.service.model.User;
import com.laundry.service.service.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/laundry")
public class HomeController {

	@Autowired
	private IUserService userService;	
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authManager;
	
	@PostMapping("/register")
	public ResponseEntity<?> saveUser(@Valid @RequestBody User user) {
		User model = userService.saveUser(user);
		return ResponseEntity.ok(model);
	}
	
	@GetMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {  	
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		//Optional<User> userOpt = repo.findByEmail(request.getEmail());
		Optional<User> userOpt = userService.findByEmail(request.getEmail());
		if (userOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Invalid email or password");
		}
		User user = userOpt.get();
		String token = jwtUtil.generateToken(user);
		//     return ResponseEntity.ok(new AuthResponse(token));
		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("user", Map.of(
				"id", user.getId(),
				"name", user.getFirst_name() + " " + user.getLast_name(),
				"email", user.getEmail()
				));

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/upload_userImage")
	public ResponseEntity<?> uploadRegImage(@RequestParam("image") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); //Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		//String email = jwtUtil.extractEmail(token);
		String userId = jwtUtil.extractClaim(token, "user_id");
		
		int user_id = Integer.parseInt(userId);
		String path = userService.uploadProfileImage(user_id, file);
		
		if (path == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
		}
		
		// Return the path in a DTO
		ImageUploadDTO dto = new ImageUploadDTO();
		dto.setPath(path);
		return ResponseEntity.ok(dto);
	}
	
}
