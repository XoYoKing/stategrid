package com.etrust.stategrid;

import java.io.File;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.etrust.stategrid.bean.Infrared;
import com.etrust.stategrid.utils.FileUtils;

public class InfraredDetailActivity extends Activity implements OnClickListener {
	private WebView tipsWebView;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infrared_detail);
		Infrared infrared = (Infrared) getIntent().getSerializableExtra("infrared");
		url = infrared.path;
		setupView();

		readHtmlFormAssets();
	}

	private void setupView() {
		TextView title_text = (TextView) findViewById(R.id.inf_title_text);
		title_text.setText("红外详情");
		findViewById(R.id.inf_title_right).setOnClickListener(this);
		tipsWebView = (WebView) findViewById(R.id.inf_webView);
	}

	private void readHtmlFormAssets() {
		WebSettings webSettings = tipsWebView.getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setAppCacheEnabled(true);
		tipsWebView.setBackgroundColor(Color.TRANSPARENT);
		try{
			String content=FileUtils.readTextFile(new File(url));
			System.out.println(content);
            tipsWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
		}catch(Exception e){
			e.printStackTrace();
			tipsWebView.loadDataWithBaseURL(null, "文件不存在！", "text/html", "UTF-8", null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.inf_title_right:
			finish();
			break;
		}
	}
}
