package com.laundry.service.dto;

import java.util.List;

public class ServicesWithItemsDTO {

	private Long serviceId;
	private String serviceName;
	private List<ItemPricingDTO> items;

	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public List<ItemPricingDTO> getItems() {
		return items;
	}
	public void setItems(List<ItemPricingDTO> items) {
		this.items = items;
	}
}
