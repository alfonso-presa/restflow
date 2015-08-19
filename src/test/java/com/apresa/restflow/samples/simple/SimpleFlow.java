package com.apresa.restflow.samples.simple;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.Flow;
import com.apresa.restflow.annotations.On;
import com.apresa.restflow.annotations.StateReference;
import com.apresa.restflow.annotations.Transition;
import com.apresa.restflow.fsm.Event;
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
@Transition(event = "GOB", from="A", to = "B")
@Transition(event = "GOA", from="B", to = "A")
public class SimpleFlow extends AbstractBeanFlow<Bean> {
	@On("GOB")
	private void goingToB(){
		System.out.println("Going to b");
	}

	@On("GOA")
	private void goingToA(){
		System.out.println("Going to a");
	}

	public static final void main(String[] args) throws StateMachineException {
		Bean b = new Bean();
		SimpleFlow flow = new SimpleFlow();
		flow.raise(Event.build("GOB"), b);
		flow.raise(Event.build("GOA"), b);
		flow.raise(Event.build("GOA"), b);//Will do nothing
		flow.raise(Event.build("GOB"), b);
	}
}
