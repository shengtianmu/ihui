package com.zpstudio.ui.adv.fragment;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zpstudio.R;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class AdvertisementActivity extends Activity {
	
	private WebView webView;
	private String link;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advertisement);
		link=getIntent().getExtras().getString("link");
		id=getIntent().getExtras().getString("id");
		webView=(WebView)findViewById(R.id.webView);
		WebSet();
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		AddMoney();
	}
	
	public void AddMoney()
	{
		
		IhuiClientApi.getInstance(this).updateWalletAsync(Long.parseLong(id), IhuiClientApi.ADV_ACTION_TYPE_ATTENTION);
	}
	
	
	
	
	
	private void WebSet() {
		webView.loadUrl(link); 
		webView.requestFocusFromTouch();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient(){
		      @Override
		      public boolean shouldOverrideUrlLoading(WebView view, String url) {
		    	  
//		    	  if(Uri.parse(url).getHost().contains("http://121.40.154.127/main/share?main_id="))
//		            {
//		    		    Log.e("===", "ddddd");
//		                return false;
//		            }else {
//		            	Log.e("===", "cccccc");
//		            	return true;
//					}
		          view.loadUrl(url);
		          return true;
		      }
		});
		webView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		WebSettings settings = webView.getSettings();
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);

	}
}
