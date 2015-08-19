package com.apresa.restflow.fsm;

import java.lang.reflect.Method;

public class GuardRunner extends AbstractRunner{

	public GuardRunner(Method method, Object flowHandler) {
		super(method, flowHandler);
	}

	public boolean check(Event event, Object bean) {
		return (Boolean) super.executeMethod(event, bean);
	}

}
