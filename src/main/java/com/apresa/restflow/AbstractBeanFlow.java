package com.apresa.restflow;

import com.apresa.restflow.fsm.Event;
import com.apresa.restflow.fsm.StateMachine;
import com.apresa.restflow.fsm.StateMachineException;

public class AbstractBeanFlow<T> {

	private StateMachine<T> fsm;

	public AbstractBeanFlow() {
		fsm = new StateMachine<>(this);
	}

	public boolean raise(Event event, T object) throws StateMachineException {
		return fsm.raise(event, object);
	}

}
