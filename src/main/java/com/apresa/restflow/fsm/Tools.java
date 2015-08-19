package com.apresa.restflow.fsm;

import java.lang.reflect.Field;

import com.apresa.restflow.annotations.StateReference;

public class Tools {

	private Tools(){}

	public static Field getBeanStateField(Object bean) {
		Field stateField = null;
		for(Field f : bean.getClass().getDeclaredFields()){
			if(f.getAnnotation(StateReference.class) != null) {
				stateField = f;
				break;
			}
		}

		if(stateField == null) {
			throw new RuntimeException("State reference field not found in bean of type " + bean.getClass());
		}
		stateField.setAccessible(true);
		return stateField;
	}

	public static String getBeanState(Object bean) {

		Field stateField = getBeanStateField(bean);
		String state = null;
		try {

			state = stateField.get(bean).toString();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return state;
	}

}
