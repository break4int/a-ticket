package com.breakpoint.americano.ticket.nfc;

import android.util.Log;

public class NfcDataModel {

	private static final String	TAG	= NfcDataModel.class.getSimpleName();
	private int					number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		Log.i(TAG, "setNumber " + this.number + " -> " + number);
		this.number = number;
	}

}
