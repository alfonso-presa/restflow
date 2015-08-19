[![Build Status](https://travis-ci.org/alfonso-presa/restflow.svg?branch=master)](https://travis-ci.org/alfonso-presa/restflow)

Simple annotation based FSM java library.

## Purpose

This project aim is to provide a lightweight state machine library that allows to easily implement bean flows.

## Features

* Simple one level state machine
* Annotation based flow configuration:
	* Guards: prevents an state transition to be executed
	* On: executes an action when a transition is executed
	* OnState: executes an action when a transition to an state is completed
* Thread safe
* State is stored inside your own bean and never inside the state machine
    * When ever you trigger an event you provide the bean over which the FSM should operate.

## Sample

Let's say you have to build a REST end point to handle orders. You will like the orders to go through different states, for example:
* Placed
* Payed
* Sent

The order should only be able to pass from one state to the next one and only if it satisfies some conditions:
* Placed: it will only be placed if the products and the customer are provided.
	* At this point the price should be calculated depending on the products.
* Payed: it can only get payed if a valid payment recipe is provided.
* Sent: it can only be marked as sent if a TrackingInfo object is provided.

The bean supporting the information of an order could be something like this:

```java
class Order {
	@StateReference
	OrderStatus status = OrderStatus.INITIAL;
	Object[] products;
	Float price;
	Object customer;
	Object track;
}

enum OrderStatus{
	INITIAL,
	PLACED,
	PAYED,
	SENT
}
```

This library provides a way of implementing a flow class to handle this flow in the following manner:

```java
@Flow(OrderStatus.class)
@Transition(event = "PLACE", from="INITIAL", to = "PLACED")
@Transition(event = "PAY", from="PLACED", to = "PAYED")
@Transition(event = "SEND", from="PAYED", to = "SENT")
public class OrderFlow extends AbstractBeanFlow<Order> {
	@Guard("PLACE")
	private boolean checkParams(Order order, @EventParam("products") Object[] products, @EventParam("customer") Object customer){
		return products != null && customer != null;
	}

	@On("PLACE")
	private void fillOrderData(Order order, @EventParam("products") Object[] products, @EventParam("customer") Object customer){
		order.products = products;
		order.customer = customer;
	}

	@OnState("PLACED")
	private void calculatePrice(Order order){
		order.price = 10f; //this should be calculated depending on the products
	}

	@Guard("PAY")
	private boolean checkPayment(Order order, @EventParam("payment") String recipe){
		return recipe != null;
	}

	@Guard("SEND")
	private boolean checkPayment(Order order, @EventParam("tracking") Object track){
		return track != null;
	}

	@On("SEND")
	private void fillOrderTracking(Order order, @EventParam("tracking") Object track){
		order.track = track;
	}
}

```

Then by running the following you should be able to make an order pass through the bean flow:

```java
Order o = new Order();
OrderFlow flow = new OrderFlow();

flow.raise(Event.build("PLACE").param("products", new Object[]{"pid"}).param("customer", "customer"), o);
flow.raise(Event.build("PAY").param("payment", "123"), o);
flow.raise(Event.build("SEND").param("trancking", "UPS-ABC"), o);
```

Of course you can persist the bean and recover it later on between each step.

Look at src/text/java/com/apresa/restflow/samples for more examples. 


## Download

Currently only snapshots are available.

With gradle:

```
...
repositories {
    ...
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
    ...
}
...
dependencies {
    ...
    compile group: 'com.github.alfonso-presa.restflow', name: 'restflow', version: '0.0.1-SNAPSHOT'
    ...
}
...
```

With maven:

```
...
	<dependency>
		<groupId>com.github.alfonso-presa.restflow</groupId>
		<artifactId>restflow</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
...

<repositories>
	...
    <repository>
        <id>oss-sonatype</id>
        <name>oss-sonatype</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    ....
</repositories>
```
Download jar diractly from [sonatype repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/alfonso-presa/restflow/restflow/)
