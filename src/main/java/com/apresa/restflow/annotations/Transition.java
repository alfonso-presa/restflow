package com.apresa.restflow.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Transitions.class)
@Target(ElementType.TYPE)
@Inherited
public  @interface Transition {

	String event();

	String from() default "*";

	String to();

}
