package com.laundry.service.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.laundry.service.dto.OrderItemRequestDTO;
import com.laundry.service.dto.OrderRequestDTO;
import com.laundry.service.dto.OrderRespondDTO;
import com.laundry.service.dto.OrderStatusUpdateDTO;
import com.laundry.service.model.Address;
import com.laundry.service.model.ItemMaster;
import com.laundry.service.model.Order;
import com.laundry.service.model.OrderItem;
import com.laundry.service.model.OrderStatus;
import com.laundry.service.model.ServiceMaster;
import com.laundry.service.model.User;
import com.laundry.service.model.VendorBusiness;
import com.laundry.service.model.VendorServicePricing;
import com.laundry.service.repository.AddressRepo;
import com.laundry.service.repository.ItemMasterRepo;
import com.laundry.service.repository.OrderRepo;
import com.laundry.service.repository.ServiceMasterRepo;
import com.laundry.service.repository.UserRepo;
import com.laundry.service.repository.VendorBusinessRepo;
import com.laundry.service.repository.VendorServicePricingRepo;

@Service
public class OrderServiceImpl implements IOrderService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private VendorBusinessRepo vendorRepo;

	@Autowired
	private AddressRepo addressRepo;
	
	@Autowired
	private ServiceMasterRepo serviceRepo;

	@Autowired
	private ItemMasterRepo itemRepo;

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private VendorServicePricingRepo pricingRepo;

	@Override
	public ResponseEntity<?> placeOrder(OrderRequestDTO request, Principal principal) {

		String email = principal.getName();
		User customer = userRepo.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		VendorBusiness vendor = vendorRepo.findById(request.getVendorId())
				.orElseThrow(() -> new RuntimeException("Vendor not found"));

		Address address;
		if (request.getAddressId() != null) {
			address = addressRepo.findById(request.getAddressId())
					.orElseThrow(() -> new RuntimeException("Address not found"));
		} else {
			address = addressRepo.findByUserAndIsDefaultTrue(customer)
					.orElseThrow(() -> new RuntimeException("No default address found"));
		}

		Order order = new Order();
		order.setOrderNumber(generateOrderNumber());
		order.setCustomer(customer);
		order.setVendor(vendor);
		order.setDeliveryAddress(address);
		order.setPickupDate(request.getPickupDate() != null ? request.getPickupDate() : LocalDate.now().plusDays(1));
		order.setNotes(request.getNotes());
		order.setStatus(OrderStatus.PENDING);

		List<OrderItem> orderItems = new ArrayList<>();
		double totalAmount = 0.0;

		for (OrderItemRequestDTO itemDTO : request.getItems()) {

			// Fetch item entity
			ItemMaster item = itemRepo.findById(itemDTO.getItemId())
					.orElseThrow(() -> new RuntimeException("Item not found"));

			// Fetch correct price based on vendor, service, and item
			VendorServicePricing pricing = pricingRepo.findByVendorIdAndServiceIdAndItemIdAndIsActiveTrue(
					request.getVendorId(), itemDTO.getServiceId(), itemDTO.getItemId()
					).orElseThrow(() -> new RuntimeException("Pricing not found for item: " + item.getItemName()));

			double itemPrice = pricing.getPrice();

			OrderItem orderItem = new OrderItem();
			
			Optional<ServiceMaster> service = serviceRepo.findById(itemDTO.getServiceId());
			ServiceMaster serviceModel = service.get();
			orderItem.setOrder(order);
			orderItem.setItem(item);
			orderItem.setQuantity(itemDTO.getQuantity());
			orderItem.setPrice(itemPrice);
			orderItem.setService(serviceModel);

			totalAmount += itemPrice * itemDTO.getQuantity();
			orderItems.add(orderItem);
		}

		order.setItems(orderItems);
		order.setTotalAmount(totalAmount);
		orderRepo.save(order);

		Map<String, Object> response = new HashMap<>();
		response.put("orderNumber", order.getOrderNumber());
		response.put("totalAmount", totalAmount);
		response.put("message", "Order placed successfully");

		return ResponseEntity.ok(response);
	}

	private String generateOrderNumber() {
		return "ORD-" + UUID.randomUUID().toString().substring(0, 16);
		// String orderNumber = "ORD-" + customerId + "-" + System.currentTimeMillis() + "-" + random;
	}

	@Override
	public ResponseEntity<?> respondToOrder(OrderRespondDTO dto, Principal p) {
		String email = p.getName();

		VendorBusiness vendor = vendorRepo.findByUserEmail(email)
				.orElseThrow(() -> new RuntimeException("Vendor not found"));


		Order order = orderRepo.findById((long) dto.getOrderId())
				.orElseThrow(() -> new RuntimeException("Order not found"));

		if (!order.getVendor().getId().equals(vendor.getId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to respond to this order");
		}

		if (dto.isAccepted()) {
			order.setStatus(OrderStatus.ACCEPTED);
		} else {
			order.setStatus(OrderStatus.REJECTED);
			order.setNotes(dto.getRejectionReason());
		}

		orderRepo.save(order);

		/*	    String msg = dto.isAccepted() ? 
	            "Your order has been accepted. We’ll notify you for pickup soon." :
	            "Sorry, your order was rejected. Reason: " + dto.getRejectionReason();

	    // Here you would typically send a notification to the customer


	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "Order response recorded");
	    response.put("orderNumber", order.getOrderNumber());
	    response.put("status", order.getStatus());
	    response.put("notification", msg);*/

		return ResponseEntity.ok().body("Order response recorded successfully. Order Number: " + order.getOrderNumber()
		+ ", Status: " + order.getStatus());
	}

	@Override
	public ResponseEntity<?> getAllOrders(Principal p) {
		// TODO Auto-generated method stub
		
		String email = p.getName();
		
		List<Order> orders = orderRepo.findAllByCustomerEmail(email);
		
		if (orders.isEmpty()) {
			return ResponseEntity.ok("No orders found for this user.");
		}
		
        return ResponseEntity.ok(orders);
	}

	@Override
	public ResponseEntity<?> getOrderById(Long orderId) {
		// TODO Auto-generated method stub
		
		Optional<Order> orderOpt = orderRepo.findById(orderId);
		
		if (orderOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
		}
		
		return ResponseEntity.ok(orderOpt);
	}

	@Override
	public ResponseEntity<?> updateOrderStatus(OrderStatusUpdateDTO dto) {
		// TODO Auto-generated method stub
		
	    Order order = orderRepo.findById((long) dto.getOrderId())
	            .orElseThrow(() -> new RuntimeException("Order not found"));
	    
	    OrderStatus current = order.getStatus();
	    OrderStatus next = dto.getNewStatus();

	    if (!isValidTransition(current, next)) {
	        return ResponseEntity.badRequest().body("Invalid status transition from " + current + " to " + next);
	    }

	    // Step 5: Update status
	    order.setStatus(next);
	    if (dto.getMessage() != null) {
	        order.setNotes(dto.getMessage());
	    }
	    orderRepo.save(order);
	    return ResponseEntity.ok(Map.of(
	            "message", "Order status updated to " + next,
	            "orderNumber", order.getOrderNumber()
	        ));
	}
	
	private boolean isValidTransition(OrderStatus current, OrderStatus next) {
	    return switch (current) {
	        case ACCEPTED -> next == OrderStatus.PICKED_UP;
	        case PICKED_UP -> next == OrderStatus.IN_PROCESS;
	        case IN_PROCESS -> next == OrderStatus.COMPLETED;
	        case COMPLETED -> next == OrderStatus.DELIVERED;
	        default -> false;
	    };
	}

}
