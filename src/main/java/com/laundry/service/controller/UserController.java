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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.config.JwtUtil;
import com.laundry.service.dto.AddressDTO;
import com.laundry.service.dto.AuthRequest;
import com.laundry.service.dto.ImageUploadDTO;
import com.laundry.service.dto.UserDTO;
import com.laundry.service.model.User;
import com.laundry.service.service.IAddress;
import com.laundry.service.service.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/laundry/user")
public class UserController {

	@Autowired
	private IUserService userService;	
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private IAddress addressService;
	
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
	
	@GetMapping("/getUserData")
	public ResponseEntity<?> getUserImage(@RequestHeader("Authorization") String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String userId = jwtUtil.extractClaim(token, "user_id");

		int user_id = Integer.parseInt(userId);
		Optional<User> userOpt = userService.findById(user_id);

		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		User user = userOpt.get();

		return ResponseEntity.ok(user);
	}
	
	@PutMapping("/updateUser")
	public ResponseEntity<?> updateUser(@RequestBody UserDTO dto, @RequestHeader("Authorization") String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String userId = jwtUtil.extractClaim(token, "user_id");

		int id = Integer.parseInt(userId);
		
		User updatedUser = userService.updateUser(id, dto);
		
		return ResponseEntity.ok(updatedUser);
	}
	
	@PostMapping("/addAddress")
	public ResponseEntity<?> addAddress(@RequestHeader("Authorization") String authHeader, @RequestBody AddressDTO dto){
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}
		
		String token = authHeader.substring(7);
		
		if(!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
		
		String userId = jwtUtil.extractClaim(token, "user_id");
		int user_id = Integer.parseInt(userId);
		
		String result = addressService.saveUpdateAddress(user_id, dto);
		
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/updateAddress")
	public ResponseEntity<?> updateAddress(@RequestHeader("Authorization") String authHeader, @RequestBody AddressDTO dto){
		
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}
		
		String token = authHeader.substring(7);
		
		if(!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
		
		String userId = jwtUtil.extractClaim(token, "user_id");
		int user_id = Integer.parseInt(userId);
		
		String result = addressService.saveUpdateAddress(user_id, dto);
		
		return ResponseEntity.ok(result);
	}

	@GetMapping("/getDefaultAddress")
	public ResponseEntity<?> getDefaultAddress(@RequestHeader("Authorization") String authHeader){
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}
		
		String token = authHeader.substring(7); // Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
		String userId = jwtUtil.extractClaim(token, "user_id");
		int user_id = Integer.parseInt(userId);
		
		Optional<AddressDTO> addressOpt = addressService.getDefaultAddress(user_id);
		
		return ResponseEntity.ok(addressOpt);
	}
	
	
}
