package com.laundry.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.laundry.service.model.Address;


public class UserDTO {

	private String first_name;
	private String last_name;
	private String password;
	private String phone;	
	private String role;
	private Boolean is_vendor;
	private String profileImagePath;
	private List<Address> address = new ArrayList<>();
	
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getProfileImagePath() {
		return profileImagePath;
	}
	public void setProfileImagePath(String profileImagePath) {
		this.profileImagePath = profileImagePath;
	}
	public List<Address> getAddress() {
		return address;
	}
	public void setAddress(List<Address> address) {
		this.address = address;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getIs_vendor() {
		return is_vendor;
	}
	public void setIs_vendor(Boolean is_vendor) {
		this.is_vendor = is_vendor;
	}
	
	
}
