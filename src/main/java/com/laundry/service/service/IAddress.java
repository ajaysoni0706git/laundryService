package com.laundry.service.service;

import com.laundry.service.dto.AddressDTO;

public interface IAddress {

	public String saveUpdateAddress(int user_id, AddressDTO dto);
	
}
