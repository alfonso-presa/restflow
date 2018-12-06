package com.apresa.restflow.fsm;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

import com.apresa.restflow.annotations.EventParam;

public abstract class AbstractRunner {

	protected static class AbstractRunnerSorter implements Comparator<AbstractRunner> {

		@Override
		public int compare(AbstractRunner o1, AbstractRunner o2) {
			return o1.getPriority() - o2.getPriority();
		}

	}

	private final Method method;
	private final Object flowHandler;
	private int priority = 0;

	public AbstractRunner(Method method, Object flowHandler) {
		this.method = method;
		method.setAccessible(true);
		this.flowHandler = flowHandler;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}


	@SuppressWarnings("unchecked")
	protected <T> T executeMethod(Event event, Object bean){
		try {
			Method annonMethod = EventParam.class.getDeclaredMethod("value");
			Class<?>[] params = method.getParameterTypes();
			Annotation[][] annotations = method.getParameterAnnotations();
			Object[] instanceParams = new Object[params.length];
			if(params.length > 0) {
				instanceParams[0] = bean;
			}
			if(params.length > 1) {
				if (params.length == 2 && params[1].isInstance(event)) {
					instanceParams[1] = event;
				}
				else {
					for(int i = 1; i < params.length; i++) {
						if (annotations[i].length > 0) {
							for (Annotation innerA : annotations[i]) {
								String name = annonMethod.invoke(innerA, (Object[]) null).toString();
								instanceParams[i] = event.getParameter(name);
							}
						}
					}
				}
			}
			return (T) method.invoke(flowHandler, instanceParams);
		} catch (IllegalAccessException e) {
			throw new InternalStateMachineException(e);
		} catch (IllegalArgumentException e) {
			throw new InternalStateMachineException(e);
		} catch (InvocationTargetException e) {
			throw new InternalStateMachineException(e.getCause());
		} catch (NoSuchMethodException e) {
			throw new InternalStateMachineException(e);
		}
	}
}
