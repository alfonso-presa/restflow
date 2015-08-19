package com.apresa.restflow.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OnStates.class)
@Target(ElementType.METHOD)
public @interface OnState {

	String value() default "*";

}
