package com.apresa.restflow.samples.order.flow;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.*;
import com.apresa.restflow.fsm.Event;
import com.apresa.restflow.fsm.StateMachineException;

@Flow(OrderStatus.class)
@Transitions({
		@Transition(event = "PLACE", from="INITIAL", to = "PLACED"),
		@Transition(event = "PAY", from="PLACED", to = "PAYED"),
		@Transition(event = "SEND", from="PAYED", to = "SENT")
})
public class OrderFlow extends AbstractBeanFlow<Order> {


	@Guard("PLACE")
	public boolean orderCheck(Order order) {
		order.check += "3";
		return true;
	}

	@Guard(value="PLACE", order=2)
	public boolean checkParams(Order order, @EventParam("products") Object[] products, @EventParam("customer") Object customer){
		order.check += "2";
		return products != null && customer != null;
	}

	@Guard(value="PLACE", order=1)
	public boolean firstCheck(Order order) {
		order.check += "1";
		return true;
	}

	@On("PLACE")
	private void checkPlace(Order order) {
		order.check += "5";
	}

	@On(value="PLACE", order=1)
	private void fillOrderData(Order order, @EventParam("products") Object[] products, @EventParam("customer") Object customer){
		order.check += "4";
		order.products = products;
		order.customer = customer;
	}

	@OnState("PLACED")
	private void calculatePrice(Order order){
		order.check += "7";
		order.price = 10f; //this should be calculated depending on the products
	}

	@OnState(value="PLACED", order=1)
	private void doPlaced(Order order){
		order.check += "6";
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
