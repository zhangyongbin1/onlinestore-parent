package com.onlinestore.order.pojo;

import com.onlinestore.pojo.TbOrder;
import com.onlinestore.pojo.TbOrderItem;
import com.onlinestore.pojo.TbOrderShipping;

import java.io.Serializable;
import java.util.List;

/**
 * 因为需要把订单信息和物流信息包装在一起所以使用你新pojo包装
 */
public class OrderInfo extends TbOrder implements Serializable{
	
	private List<TbOrderItem> orderItems;
	private TbOrderShipping orderShipping;
	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}
	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
	

}
