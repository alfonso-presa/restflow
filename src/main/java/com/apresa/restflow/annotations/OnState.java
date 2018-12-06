package com.apresa.restflow.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
//TODO: Disabled for java 7 compatibility @Repeatable(OnStates.class)
@Target(ElementType.METHOD)
public @interface OnState {

	String value() default "*";
	int order() default Integer.MAX_VALUE;

}
