package com.laundry.service.dto;

public class OrderItemRequestDTO {

	  private Long serviceId;
	    private Long itemId;
	    private Integer quantity;
	    private String notes; // optional

	    public Long getServiceId() {
	        return serviceId;
	    }

	    public void setServiceId(Long serviceId) {
	        this.serviceId = serviceId;
	    }

	    public Long getItemId() {
	        return itemId;
	    }

	    public void setItemId(Long itemId) {
	        this.itemId = itemId;
	    }

	    public Integer getQuantity() {
	        return quantity;
	    }

	    public void setQuantity(Integer quantity) {
	        this.quantity = quantity;
	    }

	    public String getNotes() {
	        return notes;
	    }

	    public void setNotes(String notes) {
	        this.notes = notes;
	    }
}
