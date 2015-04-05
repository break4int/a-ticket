package com.breakpoint.americano.ticket.nfc;

import java.io.IOException;

import android.nfc.tech.IsoDep;
import android.util.Log;

public class IsoDepTransceiver implements Runnable {

	private static final String	TAG				= IsoDepTransceiver.class.getSimpleName();

	private IsoDep				isoDep;
	private OnMessageReceived	onMessageReceived;
	private NfcDataModel		mNfcDataModel;

	private static final byte[]	CLA_INS_P1_P2	= { 0x00, (byte) 0xA4, 0x04, 0x00 };
	private static final byte[]	AID_ANDROID		= { (byte) 0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };

	public interface OnMessageReceived {

		void onMessage(byte[] message);

		void onError(Exception e);
	}

	public IsoDepTransceiver(IsoDep isoDep, OnMessageReceived onMessageReceived, NfcDataModel nfcDataModel) {

		this.isoDep = isoDep;
		this.onMessageReceived = onMessageReceived;
		this.mNfcDataModel = nfcDataModel;
	}

	private byte[] createSelectAidApdu(byte[] aid) {

		byte[] result = new byte[6 + aid.length];
		System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
		result[4] = (byte) aid.length;
		System.arraycopy(aid, 0, result, 5, aid.length);
		result[result.length - 1] = 0;

		return result;
	}

	@Override
	public void run() {

		try {
			isoDep.connect();

			byte[] response = isoDep.transceive(createSelectAidApdu(AID_ANDROID));
			Log.i(TAG, "response Size : " + response.length);
			int number = mNfcDataModel.getNumber();

			while (isoDep.isConnected() && !Thread.interrupted()) {

				String message = String.valueOf(number);
				Log.i(TAG, "IsoDepTransceiver : " + message + ", Number : " + number);
				response = isoDep.transceive(message.getBytes());
				String responseStr = new String(response);
				Log.i(TAG, "response size : " + response.length);
				Log.i(TAG, "IsoDepTransceiver response : " + responseStr);

				String test = String.valueOf(number);
				onMessageReceived.onMessage(test.getBytes());
				Log.i(TAG, "HW_TEST !!!!!!");

				if (responseStr.equals("true")) {

					break;
				}
			}

			Log.i(TAG, "HW_TEST IN");
			mNfcDataModel.setNumber(number + 1);
			Log.i(TAG, "IsoDepTransceiver Number : " + number);
			isoDep.close();
		} catch (IOException e) {
			onMessageReceived.onError(e);
		}
	}
}
