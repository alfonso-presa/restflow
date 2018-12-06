package com.apresa.restflow.samples.order;

import com.apresa.restflow.fsm.Event;
import com.apresa.restflow.samples.order.flow.Order;
import com.apresa.restflow.samples.order.flow.OrderFlow;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderFlowTests {

	@Test
	public void itShouldInvokeMethodsInOrder() throws Exception {

		for(int i = 0; i < 10; i++) {
			final Order o = new Order();

			final OrderFlow flow = new OrderFlow();

			final Object[] products = new Object[]{"pid"};
			final String customer = "customer";
			final Event event = Event.build("PLACE")
					.param("products", products)
					.param("customer", customer);

			flow.raise(event, o);

			assertEquals(o.check, "1234567");
		}

	}
}
