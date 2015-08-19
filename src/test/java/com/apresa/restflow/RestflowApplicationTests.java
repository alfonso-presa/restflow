package com.apresa.restflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.apresa.restflow.fsm.Event;
import com.apresa.restflow.samples.transfers.flow.TransferEvents;
import com.apresa.restflow.samples.transfers.flow.TransferFlow;
import com.apresa.restflow.samples.transfers.flow.TransferStatus;
import com.apresa.restflow.samples.transfers.model.Transfer;

public class RestflowApplicationTests {

	TransferFlow flow = new TransferFlow();

	private Transfer initTransfer() {
		return new Transfer();
	}

	private void fillTransfer(Transfer t) throws Exception {
		flow.raise(
			Event.build(TransferEvents.FILL)
				.param("amount", 1.0f)
				.param("src", "SRC_ACCOUNT")
				.param("target", "DST_ACCOUNT")
		, t);

		assertEquals(t.amount, 1.0f, 0);
		assertEquals(t.src, "SRC_ACCOUNT");
		assertEquals(t.target, "DST_ACCOUNT");
		assertEquals(t.status, TransferStatus.INITIAL);
	}

	private void proceedTransfer(Transfer t) throws Exception {
		assertTrue(flow.raise(Event.build(TransferEvents.PROCEED), t));
		assertEquals(t.status, TransferStatus.PENDING_SIGNATURE);
	}

	private void signTransfer(Transfer t) throws Exception {
		assertTrue(
			flow.raise(
				Event.build(TransferEvents.SIGN)
					.param("signature", "SIGNATURE")
			, t)
		);
		assertEquals(t.status, TransferStatus.PENDING_OTP);
		assertNotNull(t.otpHash);
	}

	private void executeTransfer(Transfer t) throws Exception {
		assertTrue(
			flow.raise(
				Event.build(TransferEvents.EXECUTE)
					.param("otp", "OTP")
			, t)
		);
		assertEquals(t.status, TransferStatus.EXECUTED);
	}

	@Test
	public void itShouldProgressTheBeanThroughTheFlow() throws Exception {
		Transfer t = initTransfer();

		fillTransfer(t);
		proceedTransfer(t);
		signTransfer(t);
		executeTransfer(t);
	}

	@Test
	public void itShouldReturnFalseIfTransitionIsUnknown() throws Exception {
		Transfer t = initTransfer();
		assertFalse(flow.raise(Event.build("NON_EXISTING"), t));
	}

	@Test
	public void itShouldReturnFalseIfGuardsAreNotOk() throws Exception {
		Transfer t = initTransfer();

		fillTransfer(t);

		assertTrue(
			flow.raise(
				Event.build(TransferEvents.FILL)
					.param("amount", -1.0f)
			, t)
		);

		assertEquals(t.target, "DST_ACCOUNT");
		assertFalse(flow.raise(new Event(TransferEvents.PROCEED), t));
	}

	@Test
	public void itShouldReturnFalseIfTransitionIsNotPossibleInTheState() throws Exception {
		Transfer t = initTransfer();

		fillTransfer(t);
		proceedTransfer(t);

		assertFalse(flow.raise(Event.build(TransferEvents.FILL), t));
	}
}
