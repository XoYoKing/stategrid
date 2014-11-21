package com.etrust.stategrid;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebviewCeWen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webviewceshi);
		
		WebView webView = (WebView) findViewById(R.id.webView);
		
		webView.loadUrl("http://192.168.3.185/wdjk/transubDo/deviceListAnUi");
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);//内置缩放机制
		webView.getSettings().setAllowFileAccess(true);  
		webView.getSettings().setPluginState(PluginState.ON);
		
		MyWebViewClient myWebViewClient = new MyWebViewClient();
		
		
		webView.setWebViewClient(myWebViewClient);
		
	}

	private class MyWebViewClient extends WebViewClient {

		// 重写父类方法，让新打开的网页在当前的WebView中显示
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
		}

	}


	





}
