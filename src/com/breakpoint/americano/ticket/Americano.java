package com.breakpoint.americano.ticket;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class Americano {

	private Context	mContext;

	public Americano(Context context) {

		this.mContext = context;
	}

	@JavascriptInterface
	public void getNfcData(final String Text) {

	}
}
