package com.apresa.restflow.annotations;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
//TODO: Disabled for java 7 compatibility @Repeatable(Transitions.class)
@Target(ElementType.TYPE)
@Inherited
public  @interface Transition {

	String event();

	String from() default "*";

	String to();

}
