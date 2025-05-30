package com.laundry.service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Entity
@Table(name = "vendor_business")
@Data
public class VendorBusiness {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "business_id", unique = true, nullable = false)
	private Long id;

	private String businessName;

	@Email
	private String contactEmail;

	@Column(name = "contact_number")
	private String contactNumber;
	
    @Column(name = "gst_number")
    private String gstNumber; // Optional
    
    @Column(name = "working_hours")
    private String workingHours;
    
    @Column(name = "is_home_pickup")
    private Boolean home_pickup;
    
    @Column(name = "is_home_delivery")
    private Boolean home_delivery;
    
    private Double delivery_radius;
    
    private Boolean is_active;	
    
	private Boolean is_verified;

	private String street;
	private String city;
	private String state;
	private String postalCode;
	private String country;

	@Column(name = "latitude", nullable = true)
	private Double latitude;

	@Column(name = "longitude", nullable = true)
	private Double longitude;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	private LocalDateTime created_date;	
	private LocalDateTime updated_date;
	private Boolean is_disabled;
	private LocalDateTime disabled_date;
	
    @Column(name = "license_proof_path")
    private String licenseProofPath;

    @Column(name = "id_proof_path")
    private String idProofPath;
    
    @Column(name = "business_dp_path")
    private String businessDpPath;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	public Boolean getHome_pickup() {
		return home_pickup;
	}

	public void setHome_pickup(Boolean home_pickup) {
		this.home_pickup = home_pickup;
	}

	public Boolean getHome_delivery() {
		return home_delivery;
	}

	public void setHome_delivery(Boolean home_delivery) {
		this.home_delivery = home_delivery;
	}

	public Double getDelivery_radius() {
		return delivery_radius;
	}

	public void setDelivery_radius(Double delivery_radius) {
		this.delivery_radius = delivery_radius;
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

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getCreated_date() {
		return created_date;
	}

	public void setCreated_date(LocalDateTime created_date) {
		this.created_date = created_date;
	}

	public LocalDateTime getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(LocalDateTime updated_date) {
		this.updated_date = updated_date;
	}

	public Boolean getIs_disabled() {
		return is_disabled;
	}

	public void setIs_disabled(Boolean is_disabled) {
		this.is_disabled = is_disabled;
	}

	public LocalDateTime getDisabled_date() {
		return disabled_date;
	}

	public void setDisabled_date(LocalDateTime disabled_date) {
		this.disabled_date = disabled_date;
	}

	public String getLicenseProofPath() {
		return licenseProofPath;
	}

	public void setLicenseProofPath(String licenseProofPath) {
		this.licenseProofPath = licenseProofPath;
	}

	public String getIdProofPath() {
		return idProofPath;
	}

	public void setIdProofPath(String idProofPath) {
		this.idProofPath = idProofPath;
	}

	public String getBusinessDpPath() {
		return businessDpPath;
	}

	public void setBusinessDpPath(String businessDpPath) {
		this.businessDpPath = businessDpPath;
	}
    
    
}
