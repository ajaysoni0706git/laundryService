package com.laundry.service.service;

import java.security.Principal;

import org.springframework.http.ResponseEntity;

import com.laundry.service.dto.OrderRequestDTO;
import com.laundry.service.dto.OrderRespondDTO;
import com.laundry.service.dto.OrderStatusUpdateDTO;

public interface IOrderService {

	public ResponseEntity<?> placeOrder(OrderRequestDTO dto, Principal p);

	public ResponseEntity<?> respondToOrder(OrderRespondDTO dto, Principal p);

	public ResponseEntity<?> getAllOrders(Principal p);

	public ResponseEntity<?> getOrderById(Long orderId);

	public ResponseEntity<?> updateOrderStatus(OrderStatusUpdateDTO dto);

}
