package com.apresa.restflow.samples.order.flow;

import com.apresa.restflow.annotations.StateReference;

/**
 * Created by alfonso on 6/12/18.
 */
public class Order {
	@StateReference
	public OrderStatus status = OrderStatus.INITIAL;
	public Object[] products;
	public Float price;
	public Object customer;
	public Object track;
	public String check = "";
}
