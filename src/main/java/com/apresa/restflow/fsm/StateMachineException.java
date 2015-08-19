package com.apresa.restflow.fsm;

public class StateMachineException extends Exception {

	public StateMachineException() {
		super();
	}

	public StateMachineException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public StateMachineException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public StateMachineException(String arg0) {
		super(arg0);
	}

	public StateMachineException(Throwable baseException) {
		super(baseException);
	}

	private static final long serialVersionUID = -7984877548891413015L;


}
