package com.laundry.service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.service.config.JwtUtil;
import com.laundry.service.dto.AuthRequest;
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
}
