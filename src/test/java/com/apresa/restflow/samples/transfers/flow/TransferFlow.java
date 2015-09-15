package com.apresa.restflow.samples.transfers.flow;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.*;
import com.apresa.restflow.samples.transfers.model.Transfer;
import com.apresa.restflow.samples.transfers.support.Utils;

@Flow(TransferStatus.class)
@Transitions({
	@Transition(event=TransferEvents.FILL, from=TransferStatusConst.INITIAL, to=TransferStatusConst.INITIAL),
	@Transition(event=TransferEvents.PROCEED, from=TransferStatusConst.INITIAL, to=TransferStatusConst.PENDING_SIGNATURE),
	@Transition(event=TransferEvents.SIGN, from=TransferStatusConst.PENDING_SIGNATURE, to=TransferStatusConst.PENDING_OTP),
	@Transition(event=TransferEvents.EXECUTE, from=TransferStatusConst.PENDING_OTP, to=TransferStatusConst.EXECUTED)
})
public class TransferFlow extends AbstractBeanFlow<Transfer>{

	@Guard(TransferEvents.SIGN)
	private boolean checkValidSignature(Transfer transfer, @EventParam("signature") String signature) {
		return Utils.checkValidSignature(transfer, signature);
	}

	@Guard(TransferEvents.EXECUTE)
	private boolean checkValidOTP(Transfer transfer, @EventParam("otp") String otp) {
		return Utils.hashOTP(otp).equals(transfer.otpHash);
	}

	@OnState(TransferStatusConst.PENDING_OTP)
	private void generateOTP(Transfer transfer) {
		Utils.addOTP(transfer);
	}

	@On(TransferEvents.FILL)
	private void fillData(Transfer transfer, @EventParam("amount") Float amount, @EventParam("src") String src, @EventParam("target") String target) {
		transfer.amount = amount != null ? amount : transfer.amount;
		transfer.src = src != null ? src : transfer.src;
		transfer.target = target != null ? target : transfer.target;
	}

	@Guards({
		@Guard(TransferEvents.PROCEED),
		@Guard(TransferEvents.SIGN),
		@Guard(TransferEvents.EXECUTE)
	})

	private boolean validateData(Transfer transfer) {
		return Utils.checkValidData(transfer);
	}
}
