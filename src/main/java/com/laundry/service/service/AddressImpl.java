package com.laundry.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.service.dto.AddressDTO;
import com.laundry.service.model.Address;
import com.laundry.service.model.User;
import com.laundry.service.repository.AddressRepo;
import com.laundry.service.repository.UserRepo;

@Service
public class AddressImpl implements IAddress {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private AddressRepo repo;

	@Override
	public String saveUpdateAddress(int userId, AddressDTO dto) {
	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Address address = new Address();

	    if (dto.getAddress_id() != null) {
	        // Update existing address
	        address = repo.findById(dto.getAddress_id())
	                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + dto.getAddress_id()));
	        
	        if (!address.getUser().getId().equals(Long.valueOf(userId))) {
	            throw new RuntimeException("Address does not belong to the user");
	        }
	    } else {
	       
	        address.setUser(user); 
	    }

	    // Set fields only if not null
	    if (dto.getStreet() != null) address.setStreet(dto.getStreet());
	    if (dto.getCity() != null) address.setCity(dto.getCity());
	    if (dto.getState() != null) address.setState(dto.getState());
	    if (dto.getCountry() != null) address.setCountry(dto.getCountry());
	    if (dto.getPostalCode() != null) address.setPostalCode(dto.getPostalCode());
	    if (dto.getLatitude() != null) address.setLatitude(dto.getLatitude());
	    if (dto.getLongitude() != null) address.setLongitude(dto.getLongitude());

	    // Handle default address logic
	    if (Boolean.TRUE.equals(dto.getIs_default())) {
	        // Make all other user addresses non-default
	        if (user.getAddress() != null) {
	            for (Address other : user.getAddress()) {
	                if (!other.equals(address)) {
	                    other.setIs_default(false);
	                    repo.save(other);
	                }
	            }
	        }
	        address.setIs_default(true);
	    }

	    repo.save(address);
	    return dto.getAddress_id() != null ? "Address updated successfully!" : "Address added successfully!";
	}


}
