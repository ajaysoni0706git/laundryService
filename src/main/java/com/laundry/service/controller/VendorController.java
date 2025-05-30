package com.laundry.service.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}
