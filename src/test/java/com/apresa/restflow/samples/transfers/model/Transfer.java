package com.apresa.restflow.samples.transfers.model;


import com.apresa.restflow.annotations.StateReference;
import com.apresa.restflow.samples.transfers.flow.TransferStatus;

public class Transfer {
	@StateReference
	public TransferStatus status = TransferStatus.INITIAL;

	public Float amount;
	public String src;
	public String target;
	public String otpHash;
}
