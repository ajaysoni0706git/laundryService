package com.laundry.service.dto;

import com.laundry.service.model.OrderStatus;

public class OrderStatusUpdateDTO {
    private int orderId;
    private OrderStatus newStatus;
    private String message;
    
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public OrderStatus getNewStatus() {
		return newStatus;
	}
	public void setNewStatus(OrderStatus newStatus) {
		this.newStatus = newStatus;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	} 
    
    
}
