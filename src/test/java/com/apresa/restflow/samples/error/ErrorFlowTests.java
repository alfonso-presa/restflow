package com.apresa.restflow.samples.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.apresa.restflow.fsm.Event;
import com.apresa.restflow.fsm.StateMachineException;

public class ErrorFlowTests {

	ErrorFlow flow = new ErrorFlow();

	@Test
	public void itShouldRaiseExceptionsThatHappen() throws Exception {

		try {
			System.out.println(flow.raise(Event.build("GOB"), new Bean()));
			assertFalse(true);
		} catch (StateMachineException e) {
			assertEquals(e.getMessage(), "Error happened");
		}
	}


}
