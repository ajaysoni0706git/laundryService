package com.laundry.service.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.config.JwtUtil;
import com.laundry.service.dto.ItemPricingDTO;
import com.laundry.service.dto.ServicesWithItemsDTO;
import com.laundry.service.dto.VendorServicePricingDTO;
import com.laundry.service.model.ServiceMaster;
import com.laundry.service.model.User;
import com.laundry.service.model.VendorBusiness;
import com.laundry.service.service.IUserService;
import com.laundry.service.service.IVendorBusinessService;

@RestController
@RequestMapping("/laundry/vendor")
public class VendorController {

	@Autowired
	private IUserService userService;

	@Autowired
	private IVendorBusinessService vendorService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/registration/{userId}")
	public ResponseEntity<?> vendorRegistration(@RequestParam Map<String, String> params, 
			@RequestParam("licenseProof") MultipartFile licenseProof, @RequestParam("idProof") MultipartFile idProof, @RequestParam("businessDp") MultipartFile businessDp,
			@PathVariable ("userId") int user_id) {

		Optional<User> userOpt = userService.findById(user_id);

		if (userOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found");
		}

		User user = userOpt.get();

		if (!user.getRole().equalsIgnoreCase("VENDOR")) {
			return ResponseEntity.badRequest().body("User is not authorized as a vendor");
		}

		// Validate the proofs
		if (licenseProof.isEmpty()) {
			return ResponseEntity.badRequest().body("License proof is required");
		}

		if (idProof.isEmpty()) {
			return ResponseEntity.badRequest().body("ID proof is required");
		}

		VendorBusiness business = new VendorBusiness();

		business.setBusinessName(params.get("businessName"));
		business.setGstNumber(params.get("gstNumber"));
		business.setContactNumber(params.get("contactNumber"));
		business.setContactEmail(params.get("contactEmail"));
		business.setWorkingHours(params.get("workingHours"));
		business.setHome_pickup(params.get("homePickup").equalsIgnoreCase("true"));
		business.setHome_delivery(params.get("homeDelivery").equalsIgnoreCase("true"));
		business.setDelivery_radius(Double.parseDouble(params.get("deliveryRadius")));
		business.setStreet(params.get("street"));
		business.setCity(params.get("city"));
		business.setState(params.get("state"));
		business.setCountry(params.get("country"));
		business.setPostalCode(params.get("postalCode"));
		business.setLongitude(Double.parseDouble(params.get("longitude")));
		business.setLatitude(Double.parseDouble(params.get("latitude")));
		business.setUser(user);

		String result = vendorService.saveVendorBusiness(business, licenseProof, idProof, businessDp, user_id);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/getMyBusiness")
	public ResponseEntity<?> getMyBusinessDetails(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		String userId = jwtUtil.extractClaim(token, "user_id");
		int user_id = Integer.parseInt(userId);

		Optional<VendorBusiness> vendorOpt = vendorService.findByUserId(user_id);
		if (vendorOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor business not found for this user");
		}

		VendorBusiness vendorBusiness = vendorOpt.get();
		return ResponseEntity.ok(vendorBusiness);
	}

	@GetMapping("/getAllServices")
	public ResponseEntity<?> getAllServicesAndItems(){
		List<ServiceMaster> services = vendorService.getAllServicesAndItems();
		if (services.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No services found");
		}
		return ResponseEntity.ok(services);
	}



	@PostMapping("/add-pricing/{vendorId}")
	public ResponseEntity<?> addPricing(@PathVariable Long vendorId, @RequestBody List<VendorServicePricingDTO> pricingList, @RequestHeader("Authorization") String authHeader) {
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
		Optional<User> userOpt = userService.findById(user_id);
		User user = userOpt.get();

		Optional<VendorBusiness> vendorOpt = vendorService.findById(vendorId);
		if (vendorOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
		}

		VendorBusiness vendorBusiness = vendorOpt.get();

		if (!vendorBusiness.getUser().getId().equals(user.getId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to add pricing for this vendor");
		}

		String result = vendorService.saveVendorPricing(vendorId, pricingList);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/getVendorItemsByServices/{vendorId}")
	public ResponseEntity<?> getVendorServices(@PathVariable Long vendorId, @RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		Optional<VendorBusiness> vendorOpt = vendorService.findById(vendorId);
		if (vendorOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
		}

		try {
			List<VendorServicePricingDTO> services = vendorService.getVendorServices(vendorId);
			return ResponseEntity.ok(services);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
		}
	}
	
	@GetMapping("/getVendorServicesGrouped/{vendorId}")
	public ResponseEntity<?> getVendorServicesGrouped(@PathVariable Long vendorId, @RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		Optional<VendorBusiness> vendorOpt = vendorService.findById(vendorId);
		if (vendorOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
		}

		try {
			List<ServicesWithItemsDTO> services = vendorService.getVendorServicesGrouped(vendorId);
			return ResponseEntity.ok(services);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
		}
	}
	
	@GetMapping("/getVendorItems")
	public ResponseEntity<?> getVendorItems(@RequestParam Long vendorId, @RequestParam Long serviceId, @RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
		}

		String token = authHeader.substring(7); // Remove the Bearer
		if (!jwtUtil.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}

		Optional<VendorBusiness> vendorOpt = vendorService.findById(vendorId);
		if (vendorOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
		}

		try {
			List<ItemPricingDTO> items = vendorService.getItemsByVendorAndService(vendorId, serviceId);
			return ResponseEntity.ok(items);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
		}
	}

}
