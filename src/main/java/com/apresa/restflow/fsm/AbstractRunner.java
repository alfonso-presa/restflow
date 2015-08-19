package com.apresa.restflow.fsm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.apresa.restflow.annotations.EventParam;

public abstract class AbstractRunner {

	private final Method method;
	private final Object flowHandler;

	public AbstractRunner(Method method, Object flowHandler) {
		this.method = method;
		method.setAccessible(true);
		this.flowHandler = flowHandler;
	}

	@SuppressWarnings("unchecked")
	protected <T> T executeMethod(Event event, Object bean){
		try {
			Parameter[] params = method.getParameters();
			Object[] instanceParams = new Object[params.length];
			if(params.length > 0) {
				instanceParams[0] = bean;
			}
			if(params.length > 1) {
				if(params.length == 2 && params[1].getType().isInstance(event)) {
					instanceParams[1] = event;
				}
				else {
					for(int i = 1; i < params.length; i++) {
						Parameter param = params[i];
						String name = param.getAnnotation(EventParam.class).value();
						instanceParams[i] = event.getParameter(name);
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
		}
	}
}
