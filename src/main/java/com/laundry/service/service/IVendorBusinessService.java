package com.laundry.service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.dto.ItemPricingDTO;
import com.laundry.service.dto.ServicesWithItemsDTO;
import com.laundry.service.dto.VendorServicePricingDTO;
import com.laundry.service.model.ServiceMaster;
import com.laundry.service.model.VendorBusiness;

public interface IVendorBusinessService {

	public String saveVendorBusiness(VendorBusiness business, MultipartFile licenseProof, MultipartFile idProof, MultipartFile businessDp, int user_id);

	public String saveBusinessDp(MultipartFile businessDp, Long vendor_id, int user_id);

	public String saveLicenseAndIdProof(MultipartFile licenseProof, MultipartFile idProof, Long vendor_id, int user_id);

	public String saveVendorPricing(Long vendorId, List<VendorServicePricingDTO> pricingList);
	
	public Optional<VendorBusiness> findById(Long vendorId);

	public List<ServiceMaster> getAllServicesAndItems();

	public Optional<VendorBusiness> findByUserId(int user_id);

	public List<VendorServicePricingDTO> getVendorServices(Long vendorId);

	public List<ServicesWithItemsDTO> getVendorServicesGrouped(Long vendorId);

	public List<ItemPricingDTO> getItemsByVendorAndService(Long vendorId, Long serviceId);

}
