package com.apresa.restflow.fsm;

import java.lang.reflect.Method;

public class ActionRunner extends AbstractRunner{

	public ActionRunner(Method method, Object flowHandler) {
		super(method, flowHandler);
	}

	public void execute(Event event, Object bean){
		super.executeMethod(event, bean);
	}

}
