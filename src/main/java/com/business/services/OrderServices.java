package com.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.business.entities.Orders;
import com.business.entities.User;
import com.business.repositories.OrderRepository;

@Service
public class OrderServices
{
	@Autowired
	private OrderRepository orderRepository;

	//get all orders
	public List<Orders> getOrders()
	{
		List<Orders> list = this.orderRepository.findAll();
		return list;
	}

	//save Order
	public void saveOrder(@NonNull Orders order)
	{
		this.orderRepository.save(order);
	}
	
	//update order
	public void updateOrder(int id, @NonNull Orders order)
	{
		order.setoId(id);
		this.orderRepository.save(order);
	}
	
	//delete order
	public void deleteOrder(int id)
	{
		this.orderRepository.deleteById(id);
	}
	
	//get Order history of user
	public List<Orders> getOrdersForUser(User user)
	{
		return this.orderRepository.findOrdersByUser(user);
	}
}
