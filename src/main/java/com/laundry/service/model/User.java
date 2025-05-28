package com.laundry.service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	private Long id;
	
	private String first_name;
	private String last_name;
	
	@Email
	@Column(name = "email", unique = true, nullable = false)
	private String email;
	
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Column(name = "phone")
	private String phone;	
	private String role;	
	private Boolean is_active;	
	private Boolean is_verified;	
	private Boolean is_vendor;
	private LocalDateTime login_time; 
	private LocalDateTime created_date;	
	private LocalDateTime updated_date;
	private Boolean is_disabled;
	private LocalDateTime disabled_date;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Address> address = new ArrayList<>();
	
	@Column(name = "profile_image_path")
	private String profileImagePath;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

	public Boolean getIs_verified() {
		return is_verified;
	}

	public void setIs_verified(Boolean is_verified) {
		this.is_verified = is_verified;
	}

	public Boolean getIs_vendor() {
		return is_vendor;
	}

	public void setIs_vendor(Boolean is_vendor) {
		this.is_vendor = is_vendor;
	}

	public LocalDateTime getCreated_date() {
		return created_date;
	}

	public void setCreated_date(LocalDateTime localDateTime) {
		this.created_date = localDateTime;
	}

	public Boolean getIs_disabled() {
		return is_disabled;
	}

	public void setIs_disabled(Boolean is_disabled) {
		this.is_disabled = is_disabled;
	}

	public LocalDateTime getLogin_time() {
		return login_time;
	}

	public void setLogin_time(LocalDateTime login_time) {
		this.login_time = login_time;
	}

	public LocalDateTime getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(LocalDateTime updated_date) {
		this.updated_date = updated_date;
	}

	public LocalDateTime getDisabled_date() {
		return disabled_date;
	}

	public void setDisabled_date(LocalDateTime disabled_date) {
		this.disabled_date = disabled_date;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddresses(List<Address> address) {
		this.address = address;
	}

	public String getProfileImagePath() {
		return profileImagePath;
	}

	public void setProfileImagePath(String profileImagePath) {
		this.profileImagePath = profileImagePath;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	
	
	
}
