package com.breakpoint.americano.ticket.nfc;

import android.content.Context;
import android.net.Uri;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.breakpoint.americano.ticket.nfc.IsoDepTransceiver.OnMessageReceived;

public class NfcTagAdapter implements OnMessageReceived, ReaderCallback {

	private static final String	TAG			= NfcTagAdapter.class.getSimpleName();

	private static final String	AUTHORITY	= "americano.ticket.nfc.contentproviderdata";
	private static final Uri	CONTENT_URI	= Uri.parse("content://" + AUTHORITY);

	NfcDataModel				mNfcDataModel;
	Handler						mHandler;
	Context						mContext;

	public NfcTagAdapter(Handler handler) {
		mNfcDataModel = new NfcDataModel();
		mHandler = handler;
	}

	@Override
	public void onMessage(byte[] message) {

		Message msg = new Message();
		msg.obj = String.valueOf(message);
		Log.i(TAG, "onMessage : " + String.valueOf(message));
		mHandler.sendMessage(msg);

	}

	@Override
	public void onError(Exception e) {

	}

	@Override
	public void onTagDiscovered(Tag tag) {

		Log.i(TAG, "onTagDiscovered");
		IsoDep isoDep = IsoDep.get(tag);
		Log.i(TAG, "IsoDepTransceiver start");
		IsoDepTransceiver transceiver = new IsoDepTransceiver(isoDep, this, mNfcDataModel);
		Log.i(TAG, "IsoDepTransceiver Thread start");
		Thread thread = new Thread(transceiver);
		thread.start();
	}

}
