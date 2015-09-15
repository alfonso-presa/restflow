package com.apresa.restflow.samples.order;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.*;
import com.apresa.restflow.fsm.Event;
import com.apresa.restflow.fsm.StateMachineException;

class Order {
	@StateReference
	OrderStatus status = OrderStatus.INITIAL;
	Object[] products;
	Float price;
	Object customer;
	Object track;
}

enum OrderStatus{
	INITIAL,
	PLACED,
	PAYED,
	SENT
}

@Flow(OrderStatus.class)
@Transitions({
		@Transition(event = "PLACE", from="INITIAL", to = "PLACED"),
		@Transition(event = "PAY", from="PLACED", to = "PAYED"),
		@Transition(event = "SEND", from="PAYED", to = "SENT")
})
public class OrderFlow extends AbstractBeanFlow<Order> {
	@Guard("PLACE")
	private boolean checkParams(Order order, @EventParam("products") Object[] products, @EventParam("customer") Object customer){
		return products != null && customer != null;
	}

	@On("PLACE")
	private void fillOrderData(Order order, @EventParam("products") Object[] products, @EventParam("customer") Object customer){
		order.products = products;
		order.customer = customer;
	}

	@OnState("PLACED")
	private void calculatePrice(Order order){
		order.price = 10f; //this should be calculated depending on the products
	}

	@Guard("PAY")
	private boolean checkPayment(Order order, @EventParam("payment") String recipe){
		return recipe != null;
	}

	@Guard("SEND")
	private boolean checkPayment(Order order, @EventParam("tracking") Object track){
		return track != null;
	}

	@On("SEND")
	private void fillOrderTracking(Order order, @EventParam("tracking") Object track){
		order.track = track;
	}

	@OnState()
	private void notifyStateChange(Order order) {
		System.out.println(order.status);
	}

	public static final void main(String[] args) throws StateMachineException {
		Order o = new Order();

		OrderFlow flow = new OrderFlow();

		flow.raise(Event.build("PLACE").param("products", new Object[]{"pid"}).param("customer", "customer"), o);
		flow.raise(Event.build("PAY").param("payment", "123"), o);
		flow.raise(Event.build("SEND").param("trancking", "UPS-ABC"), o);
	}
}
