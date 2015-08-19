package com.apresa.restflow.samples.error;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.Flow;
import com.apresa.restflow.annotations.On;
import com.apresa.restflow.annotations.StateReference;
import com.apresa.restflow.annotations.Transition;
import com.apresa.restflow.fsm.StateMachineException;

enum States {
	A,
	B
}

class Bean {
	@StateReference
	States state = States.A;
}

@Flow(States.class)
@Transition(event="GOB", from="A", to="B")
public class ErrorFlow extends AbstractBeanFlow<Bean> {

	@On("GOB")
	private void goingToB() throws StateMachineException{
		System.out.println("going to b");
		throw new StateMachineException("Error happened");
	}
}
