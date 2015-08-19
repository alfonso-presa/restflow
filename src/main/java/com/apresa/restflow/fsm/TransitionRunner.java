package com.apresa.restflow.fsm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.apresa.restflow.annotations.Transition;

public class TransitionRunner {

	@SuppressWarnings("rawtypes")
	private Class stateClass;

	public TransitionRunner(@SuppressWarnings("rawtypes") Class c, Transition transition) {
		stateClass = c;
		this.transitionData = transition;
	}

	private Transition transitionData;
	private List<GuardRunner> guards = new ArrayList<GuardRunner>();
	private List<ActionRunner> actions = new ArrayList<ActionRunner>();

	public void add(GuardRunner guard) {
		guards.add(guard);
	}

	public void add(ActionRunner action) {
		actions.add(action);
	}

	@SuppressWarnings("unchecked")
	public boolean execute(Event event, Object bean) {
		if(!event.getName().equals(transitionData.event())){
			return false;
		}

		String state = Tools.getBeanState(bean);
		Field stateField = Tools.getBeanStateField(bean);

		if(!transitionData.from().equals(state) && !transitionData.from().equals("*")) {
			return false;
		}

		for(GuardRunner guard : guards) {
			if(!guard.check(event, bean)) {
				return false;
			}
		}

		try {
			stateField.set(bean, Enum.valueOf(stateClass, transitionData.to()));
		} catch (IllegalArgumentException e) {
			throw new InternalStateMachineException(e);
		} catch (IllegalAccessException e) {
			throw new InternalStateMachineException(e);
		}

		for(ActionRunner action : actions) {
			action.execute(event, bean);
		}

		return true;

	}

}
