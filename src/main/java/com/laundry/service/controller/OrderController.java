package com.laundry.service.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.service.dto.OrderRequestDTO;
import com.laundry.service.dto.OrderRespondDTO;
import com.laundry.service.dto.OrderStatusUpdateDTO;
import com.laundry.service.service.IOrderService;

@RestController
@RequestMapping("laundry/orders")
public class OrderController {

	@Autowired
	private IOrderService orderService;

	@PostMapping("/placeOrder")
	public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO dto, Principal p) {

		if (dto.getItems() == null || dto.getItems().isEmpty()) {
			return ResponseEntity.badRequest().body("Order must include at least one item.");
		}

		if (dto.getPickupDate() != null && dto.getPickupDate().isBefore(LocalDate.now())) {
			System.out.println(LocalDate.now());
			return ResponseEntity.badRequest().body("Pickup date cannot be in the past.");
		}

		if (dto.getVendorId() == null || dto.getAddressId() == null) {
			return ResponseEntity.badRequest().body("Vendor ID and Address ID must be provided.");
		}
		
		return orderService.placeOrder(dto, p);
	}
	
	@PutMapping("/respond")
	public ResponseEntity<?> respondToOrder(@RequestBody OrderRespondDTO dto, Principal p) {
		return orderService.respondToOrder(dto, p);
	}
	
	@GetMapping("/getAllOrders")
	public ResponseEntity<?> getAllOrders(Principal p) {
		return orderService.getAllOrders(p);
	}
	
	@GetMapping("/getOrder/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable ("orderId") Long orderId) {
		return orderService.getOrderById(orderId);
	}
	
	@PutMapping("/updateOrderStatus")
	public ResponseEntity<?> updateOrderStatus(@RequestBody OrderStatusUpdateDTO dto) {
		return orderService.updateOrderStatus(dto);
	}
	
}
