package com.apresa.restflow.fsm;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				RunnerConsumer rc = new RunnerConsumer(tr);
				List<ActionRunner> trActions = actions.get(t.event());
				if(trActions != null) {

					forEach(trActions, rc);
				}
				List<GuardRunner> trGuards = guards.get(t.event());
				if(trGuards != null) {
					forEach(trGuards, rc);
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
					ActionConsumer ac = new ActionConsumer(event, bean);
					List<ActionRunner> actions = this.stateActions.get(state);
					if(actions != null){
						forEach(actions, ac);
					}
					actions = this.stateActions.get("*");
					if(actions != null){
						forEach(actions, ac);
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

	private static <T> void forEach(List<? extends T> list, Consumer<T> consumer) {
		for(T t : list) {
			consumer.accept(t);
		}
	}

	private interface Consumer<T> {
		void accept(T actionRunner);
	}

	private static class RunnerConsumer implements Consumer<AbstractRunner> {

		private TransitionRunner transitionRunner;
		
		public RunnerConsumer(TransitionRunner transitionRunner) {
			this.transitionRunner = transitionRunner;
		}		
		@Override
		public void accept(AbstractRunner actionRunner) {
			if(actionRunner instanceof ActionRunner) {
				transitionRunner.add((ActionRunner) actionRunner);
			}
			else if(actionRunner instanceof GuardRunner) {
				transitionRunner.add((GuardRunner) actionRunner);
			}
			else {
				throw new RuntimeException("Action runner type not supported: " + actionRunner);
			}
		}
	}

	private static class ActionConsumer implements Consumer<ActionRunner> {
		
		private Event event;
		private Object bean;

		public ActionConsumer(Event event, Object bean) {
			this.event = event;
			this.bean = bean;
		}

		@Override
		public void accept(ActionRunner actionRunner) {
			actionRunner.execute(event, bean);
		}
	}

}
