package com.apresa.restflow.samples.transfers.support;

import com.apresa.restflow.samples.transfers.model.Transfer;


public class Utils {

	public static String hashOTP(String otp) {
		return Integer.toString(otp.hashCode());
	}

	public static boolean checkValidSignature(Transfer transfer,
			String signature) {

		return signature.equals("SIGNATURE");
	}

	public static void addOTP(Transfer transfer) {
		transfer.otpHash = hashOTP("OTP");
	}

	public static boolean checkValidData(Transfer transfer) {
		return transfer.amount > 0 && transfer.src != null && transfer.target != null;
	}

}
