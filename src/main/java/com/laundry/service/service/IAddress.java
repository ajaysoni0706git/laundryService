package com.laundry.service.service;

import java.util.Optional;

import com.laundry.service.dto.AddressDTO;

public interface IAddress {

	public String saveUpdateAddress(int user_id, AddressDTO dto);

	public Optional<AddressDTO> getDefaultAddress(int user_id);
	
}
