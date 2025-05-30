package com.laundry.service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.model.User;
import com.laundry.service.model.VendorBusiness;
import com.laundry.service.repository.VendorBusinessRepo;

@Service
public class VendorBusinessServiceImpl implements IVendorBusinessService {

	@Autowired
	private VendorBusinessRepo vendorRepo;

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

	/*
	 * private String saveFileToDisk(MultipartFile file, String filename) throws IOException {
	 *  Path path = Paths.get("uploads/" + filename + "_" + file.getOriginalFilename());
	 *  Files.write(path, file.getBytes());
	 *  return path.toString();
	 * }
	 */
}
