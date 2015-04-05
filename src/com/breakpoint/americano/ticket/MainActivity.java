package com.breakpoint.americano.ticket;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.breakpoint.americano.ticket.nfc.NfcTagAdapter;

public class MainActivity extends Activity {

	private WebView			mWebView;
	private NfcTagAdapter	mNfcTagAdapter;
	private NfcAdapter		mNfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.addJavascriptInterface(new Americano(this.getApplicationContext()), "americano");
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebChromeClient(new WebChromeClient());

		WebSettings settings = mWebView.getSettings();
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setSaveFormData(false);
		settings.setSupportZoom(false);
		settings.setJavaScriptEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setAllowContentAccess(true);

		mWebView.loadUrl("file:///android_asset/WebContent/index.html");
	}

	@Override
	protected void onResume() {

		super.onResume();
		// mNfcAdapter.enableReaderMode(this, mNfcTagAdapter,
		// NfcAdapter.FLAG_READER_NFC_A
		// | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
	}

	@Override
	protected void onPause() {

		super.onPause();
		// mNfcAdapter.disableReaderMode(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {

		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			super.onBackPressed(); // = finish();
		}
	}

	private void handleMessage(Message msg) {

		Toast.makeText(getApplicationContext(), String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
	}

	public static class NfcHandler extends Handler {

		private final WeakReference<MainActivity>	mActivity;

		NfcHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			MainActivity activity = mActivity.get();

			if (activity != null) {
				activity.handleMessage(msg);
			}
		}
	}
}
