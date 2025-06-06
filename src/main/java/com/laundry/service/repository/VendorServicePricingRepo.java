package com.laundry.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.laundry.service.model.VendorServicePricing;

public interface VendorServicePricingRepo extends JpaRepository<VendorServicePricing, Long> {	

//	List<VendorServicePricingDTO> findByVendorIdAndServiceId(Long vendorId, Long serviceId);
	
	@Query("SELECT vsp FROM VendorServicePricing vsp WHERE vsp.vendor.id = :vendorId")
	List<VendorServicePricing> getVendorServices(@Param("vendorId") Long vendorId);

	@Query("SELECT vsp FROM VendorServicePricing vsp WHERE vsp.vendor.id = :vendorId AND vsp.service.id = :serviceId")
	List<VendorServicePricing> findByVendorIdAndServiceId(@Param("vendorId") Long vendorId, @Param("serviceId") Long serviceId);
	
    Optional<VendorServicePricing> findByVendorIdAndServiceIdAndItemIdAndIsActiveTrue(Long vendorId, Long serviceId, Long itemId);

}
