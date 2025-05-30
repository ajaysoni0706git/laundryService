package com.laundry.service.service;

import org.springframework.web.multipart.MultipartFile;

import com.laundry.service.model.VendorBusiness;

public interface IVendorBusinessService {

	public String saveVendorBusiness(VendorBusiness business, MultipartFile licenseProof, MultipartFile idProof, MultipartFile businessDp, int user_id);

	public String saveBusinessDp(MultipartFile businessDp, Long vendor_id, int user_id);

	public String saveLicenseAndIdProof(MultipartFile licenseProof, MultipartFile idProof, Long vendor_id, int user_id);

}
