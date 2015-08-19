package com.apresa.restflow.fsm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.Flow;
import com.apresa.restflow.annotations.Guard;
import com.apresa.restflow.annotations.Guards;
import com.apresa.restflow.annotations.On;
import com.apresa.restflow.annotations.OnState;
import com.apresa.restflow.annotations.OnStates;
import com.apresa.restflow.annotations.Ons;
import com.apresa.restflow.annotations.Transition;
import com.apresa.restflow.annotations.Transitions;

public class StateMachine<T> {

	private final List<TransitionRunner> transitions = new ArrayList<>();
	private final Map<String, List<ActionRunner>> stateActions;

	public StateMachine(AbstractBeanFlow<T> flowHandler) {

		this.stateActions = processStateActionAnnotations(flowHandler);
		Map<String, List<ActionRunner>> actions = processActionAnnotations(flowHandler);
		Map<String, List<GuardRunner>> guards = processGuardAnnotations(flowHandler);
		Flow flow = flowHandler.getClass().getAnnotation(Flow.class);
		Transitions transitions = flowHandler.getClass().getAnnotation(Transitions.class);
		Transition[] transitionList = null;
		if(transitions == null) {
			Transition transition = flowHandler.getClass().getAnnotation(Transition.class);
			if(transition != null) {
				transitionList = new Transition[]{transition};
			}
		}
		else{
			transitionList = transitions.value();
		}

		if(transitionList != null) {
			for(Transition t : transitionList) {
				TransitionRunner tr = new TransitionRunner(flow.value(), t);
				List<ActionRunner> trActions = actions.get(t.event());
				if(trActions != null) {
					trActions.forEach(runner -> tr.add(runner));
				}
				List<GuardRunner> trGuards = guards.get(t.event());
				if(trGuards != null) {
					trGuards.forEach(runner -> tr.add(runner));
				}

				this.transitions.add(tr);

			}
		}
	}

	private static Map<String, List<GuardRunner>> processGuardAnnotations(Object flowHandler) {
		Map<String, List<GuardRunner>> grs = new HashMap<>();
		for(Method m : flowHandler.getClass().getDeclaredMethods()) {
			Guards gs = m.getAnnotation(Guards.class);
			Guard g = m.getAnnotation(Guard.class);
			Guard[] guards = gs == null ? g == null ? new Guard[]{} : new Guard[]{g} : gs.value();

			for(Guard guard : guards) {
				GuardRunner gr = new GuardRunner(m, flowHandler);
				List<GuardRunner> transitionGrs = grs.get(guard.value());
				if(transitionGrs == null) {
					transitionGrs = new ArrayList<>();
					grs.put(guard.value(), transitionGrs);
				}
				transitionGrs.add(gr);
			}
		}
		return grs;
	}

	private static Map<String, List<ActionRunner>> processActionAnnotations(Object flowHandler) {
		Map<String, List<ActionRunner>> ars = new HashMap<>();
		for(Method m : flowHandler.getClass().getDeclaredMethods()) {
			Ons os = m.getAnnotation(Ons.class);
			On o = m.getAnnotation(On.class);
			On[] ons = os == null ? o == null ? new On[]{} : new On[]{o} : os.value();

			for(On on : ons) {
				ActionRunner ar = new ActionRunner(m, flowHandler);
				List<ActionRunner> transitionArs = ars.get(on.value());
				if(transitionArs == null) {
					transitionArs = new ArrayList<>();
					ars.put(on.value(), transitionArs);
				}
				transitionArs.add(ar);
			}
		}
		return ars;
	}

	private static Map<String, List<ActionRunner>> processStateActionAnnotations(Object flowHandler) {
		Map<String, List<ActionRunner>> ars = new HashMap<>();
		for(Method m : flowHandler.getClass().getDeclaredMethods()) {
			OnStates os = m.getAnnotation(OnStates.class);
			OnState o = m.getAnnotation(OnState.class);
			OnState[] ons = os == null ? o == null ? new OnState[]{} : new OnState[]{o} : os.value();

			for(OnState on : ons) {
				ActionRunner ar = new ActionRunner(m, flowHandler);
				List<ActionRunner> transitionArs = ars.get(on.value());
				if(transitionArs == null) {
					transitionArs = new ArrayList<>();
					ars.put(on.value(), transitionArs);
				}
				transitionArs.add(ar);
			}
		}
		return ars;
	}

	public boolean raise(Event event, T bean) throws StateMachineException {

		try{

			for(TransitionRunner t : transitions) {
				if(t.execute(event, bean)) {
					String state = Tools.getBeanState(bean);
					List<ActionRunner> actions = this.stateActions.get(state);
					if(actions != null){
						actions.forEach(action -> action.execute(event, bean));
					}
					actions = this.stateActions.get("*");
					if(actions != null){
						actions.forEach(action -> action.execute(event, bean));
					}
					return true;
				}
			}

		}
		catch(InternalStateMachineException isme){
			Throwable baseException = isme.getCause();
			if(baseException instanceof StateMachineException){
				throw (StateMachineException) baseException;
			}
			throw new StateMachineException(baseException);
		}
		return false;
	}

}
