package com.breakpoint.americano.ticket;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
		
		String domain = getDomainName("/americano.json");
		Log.i("", "getDomainName = " + domain);
		mWebView.loadUrl(domain + "/americano/ticket/index.html");
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
	
	public String getDomainName (String filename) {
		String ext = Environment.getExternalStorageState();
		String sdPath = "";
		String jsonData = null;
			
		if(ext.equals(Environment.MEDIA_MOUNTED))
			sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		else
			sdPath = Environment.MEDIA_UNMOUNTED;
			
	    try{
	    	StringBuffer bodytext = new StringBuffer();
	    	FileInputStream fis = new FileInputStream(sdPath + filename);
	    	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
	    	String tmp = "";
	    	while((tmp = bufferReader.readLine())!=null){
	    		bodytext.append(tmp);
	    	}
	    	
	    	bufferReader.close();
	    	JSONObject ja = new JSONObject(new String(bodytext));
			return ja.getString("domain");
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return null;
	}
}
