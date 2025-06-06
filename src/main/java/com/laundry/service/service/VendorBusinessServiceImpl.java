package com.laundry.service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.dto.ItemPricingDTO;
import com.laundry.service.dto.ServicesWithItemsDTO;
import com.laundry.service.dto.VendorServicePricingDTO;
import com.laundry.service.model.ItemMaster;
import com.laundry.service.model.ServiceMaster;
import com.laundry.service.model.VendorBusiness;
import com.laundry.service.model.VendorServicePricing;
import com.laundry.service.repository.ItemMasterRepo;
import com.laundry.service.repository.ServiceMasterRepo;
import com.laundry.service.repository.VendorBusinessRepo;
import com.laundry.service.repository.VendorServicePricingRepo;

@Service
public class VendorBusinessServiceImpl implements IVendorBusinessService {

	@Autowired
	private VendorBusinessRepo vendorRepo;

	@Autowired
	private ServiceMasterRepo serviceMasterRepo;

	@Autowired
	private ItemMasterRepo itemMasterRepo;

	@Autowired
	private VendorServicePricingRepo vendorServicePricingRepo;

	@Override
	public String saveVendorBusiness(VendorBusiness business, MultipartFile licenseProof, MultipartFile idProof, MultipartFile businessDp, int user_id) {

		Optional<VendorBusiness> vendorOpt = Optional.of(vendorRepo.save(business));

		if (vendorOpt.isEmpty()) {
			throw new RuntimeException("Failed to save vendor business details");
		}

		Long vendor_id = vendorOpt.get().getId();

		saveBusinessDp(businessDp, vendor_id, user_id);
		saveLicenseAndIdProof(licenseProof, idProof, vendor_id, user_id);


		return "Vendor business saved successfully";
	}

	@Value("${user.image.upload-dir}")
	private String uploadDir;

	@Override
	public String saveBusinessDp(MultipartFile businessDp, Long vendor_id, int user_id) {

		try {

			VendorBusiness business = vendorRepo.findById(vendor_id)
					.orElseThrow(() -> new RuntimeException("Business not found"));

			// Create user-specific path
			String folderPath = uploadDir + "user_" + user_id + "/business_" + vendor_id + "/profile_pic/";
			Path uploadPath = Paths.get(folderPath);

			// Create directory if it doesn't exist
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Extract file extension (e.g., .jpg, .png)
			String originalFilename = businessDp.getOriginalFilename();
			String extension = getFileExtension(originalFilename);

			/*		if (originalFilename != null && originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}*/

			// Use fixed filename
			String filename = vendor_id + "profile_pic" + extension;
			Path filePath = uploadPath.resolve(filename);

			// Overwrite if file exists
			Files.copy(businessDp.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// Update DB with relative path
			business.setBusinessDpPath(folderPath + filename);
			vendorRepo.save(business);

			return business.getBusinessDpPath();

		} catch (IOException e) {
			throw new RuntimeException("Failed to upload profile image: " + e.getMessage());
		}

	}

	@Override
	public String saveLicenseAndIdProof(MultipartFile licenseProof, MultipartFile idProof, Long vendor_id, int user_id) {
		try {
			VendorBusiness business = vendorRepo.findById(vendor_id)
					.orElseThrow(() -> new RuntimeException("Business not found"));

			// Base folder path: uploads/user_{user_id}/business_{vendor_id}/
			String baseFolder = uploadDir + "user_" + user_id + "/business_" + vendor_id + "/";

			// ========== Save License Proof ==========
			String licenseFolder = baseFolder + "license/";
			Path licensePath = Paths.get(licenseFolder);
			if (!Files.exists(licensePath)) {
				Files.createDirectories(licensePath);
			}

			String licenseExtension = getFileExtension(licenseProof.getOriginalFilename());
			String licenseFilename = "license_proof" + licenseExtension;
			Path licenseFilePath = licensePath.resolve(licenseFilename);
			Files.copy(licenseProof.getInputStream(), licenseFilePath, StandardCopyOption.REPLACE_EXISTING);
			business.setLicenseProofPath(licenseFolder + licenseFilename);

			// ========== Save ID Proof ==========
			String idFolder = baseFolder + "id/";
			Path idPath = Paths.get(idFolder);
			if (!Files.exists(idPath)) {
				Files.createDirectories(idPath);
			}

			String idExtension = getFileExtension(idProof.getOriginalFilename());
			String idFilename = "id_proof" + idExtension;
			Path idFilePath = idPath.resolve(idFilename);
			Files.copy(idProof.getInputStream(), idFilePath, StandardCopyOption.REPLACE_EXISTING);
			business.setIdProofPath(idFolder + idFilename);

			// Save updated business
			vendorRepo.save(business);

			return "License and ID proof uploaded successfully.";

		} catch (IOException e) {
			throw new RuntimeException("Failed to upload license or ID proof: " + e.getMessage());
		}
	}

	// Utility method to extract file extension
	private String getFileExtension(String filename) {
		if (filename != null && filename.contains(".")) {
			return filename.substring(filename.lastIndexOf("."));
		}
		return "";
	}

	@Override
	public String saveVendorPricing(Long vendorId, List<VendorServicePricingDTO> pricingList) {
		VendorBusiness business = vendorRepo.findById(vendorId)
				.orElseThrow(() -> new RuntimeException("Business not found"));


		for (VendorServicePricingDTO pricing : pricingList) {

			ServiceMaster service = serviceMasterRepo.findById(pricing.getServiceId())
					.orElseThrow(() -> new RuntimeException("Service not found: " + pricing.getServiceId()));

			ItemMaster item = itemMasterRepo.findById(pricing.getItemId())
					.orElseThrow(() -> new RuntimeException("Item not found: " + pricing.getItemId()));

			VendorServicePricing vendorPricing = new VendorServicePricing();
			vendorPricing.setVendor(business);
			vendorPricing.setService(service);
			vendorPricing.setItem(item);
			vendorPricing.setPrice(pricing.getPrice());
			vendorPricing.setIsActive(true);

			vendorServicePricingRepo.save(vendorPricing);
		}

		return "Vendor pricing saved successfully";
	}

	@Override
	public Optional<VendorBusiness> findById(Long vendorId) {
		// TODO Auto-generated method stub
		Optional<VendorBusiness> vendorOpt = vendorRepo.findById(vendorId);
		return vendorOpt;
	}

	@Override
	public List<ServiceMaster> getAllServicesAndItems() {
		// TODO Auto-generated method stub
		List<ServiceMaster> services = serviceMasterRepo.getAllServicesAndItems();
		return services;
	}

	@Override
	public Optional<VendorBusiness> findByUserId(int user_id) {
		// TODO Auto-generated method stub

		Optional<VendorBusiness> vendorOpt = vendorRepo.findByUserId(user_id);
		if (vendorOpt.isEmpty()) {
			throw new RuntimeException("No vendor details found for this business.");
		}
		return vendorOpt;
	}

	@Override
	public List<VendorServicePricingDTO> getVendorServices(Long vendorId){
		List<VendorServicePricing> vendorServices = vendorServicePricingRepo.getVendorServices(vendorId);
		if (vendorServices == null || vendorServices.isEmpty()) {
			throw new RuntimeException("No services found for this vendor.");
		}

		List<VendorServicePricingDTO> dtoList = new ArrayList<>();

		for (VendorServicePricing vsp : vendorServices) {
			VendorServicePricingDTO dto = new VendorServicePricingDTO();
			dto.setServiceId(vsp.getService().getId());
			dto.setServiceName(vsp.getService().getServiceName());
			dto.setItemId(vsp.getItem().getId());
			dto.setItemName(vsp.getItem().getItemName());
			dto.setPrice(vsp.getPrice());
			dto.setIsActive(vsp.getIsActive());

			dtoList.add(dto);
		}

		return dtoList;
	}
	
	@Override
	public List<ServicesWithItemsDTO> getVendorServicesGrouped(Long vendorId) {
	    List<VendorServicePricing> vendorServices = vendorServicePricingRepo.getVendorServices(vendorId);

	    if (vendorServices == null || vendorServices.isEmpty()) {
	        throw new RuntimeException("No services found for this vendor.");
	    }

	    Map<Long, ServicesWithItemsDTO> serviceMap = new HashMap<>();

	    for (VendorServicePricing vsp : vendorServices) {
	        Long serviceId = vsp.getService().getId();
	        ServicesWithItemsDTO serviceDTO = serviceMap.get(serviceId);

	        if (serviceDTO == null) {
	            serviceDTO = new ServicesWithItemsDTO();
	            serviceDTO.setServiceId(serviceId);
	            serviceDTO.setServiceName(vsp.getService().getServiceName());
	            serviceDTO.setItems(new ArrayList<ItemPricingDTO>());
	            serviceMap.put(serviceId, serviceDTO);
	        }

	        ItemPricingDTO itemDTO = new ItemPricingDTO();
	        itemDTO.setItemId(vsp.getItem().getId());
	        itemDTO.setItemName(vsp.getItem().getItemName());
	        itemDTO.setPrice(vsp.getPrice());
	        itemDTO.setIsActive(vsp.getIsActive());

	        serviceDTO.getItems().add(itemDTO);
	    }

	    return new ArrayList<ServicesWithItemsDTO>(serviceMap.values());
	}

	@Override
	public List<ItemPricingDTO> getItemsByVendorAndService(Long vendorId, Long serviceId) {
	    List<VendorServicePricing> records = vendorServicePricingRepo.findByVendorIdAndServiceId(vendorId, serviceId);
	    
	    List<ItemPricingDTO> itemList = new ArrayList<>();
	    
	    for (VendorServicePricing vsp : records) {
	        ItemPricingDTO item = new ItemPricingDTO();
	        item.setItemId(vsp.getItem().getId());
	        item.setItemName(vsp.getItem().getItemName());
	        item.setPrice(vsp.getPrice());
	        item.setIsActive(vsp.getIsActive());
	        itemList.add(item);
	    }
	    
	    return itemList;
	}



	/*
	 * private String saveFileToDisk(MultipartFile file, String filename) throws IOException {
	 *  Path path = Paths.get("uploads/" + filename + "_" + file.getOriginalFilename());
	 *  Files.write(path, file.getBytes());
	 *  return path.toString();
	 * }
	 */
}
