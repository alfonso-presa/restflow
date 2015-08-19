package com.apresa.restflow.fsm;

import java.util.HashMap;
import java.util.Map;

public class Event {

	private String name;
	private Map<String, Object> params = new HashMap<>();

	public Event(Object name) {
		this.name = name.toString();
	}

	public static Event build(Object name) {
		return new Event(name);
	}

	public Event param(String name, Object value) {
		this.params.put(name, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameter(String name) {
		return (T) params.get(name);
	}

	public String getName() {
		return name;
	}

}
