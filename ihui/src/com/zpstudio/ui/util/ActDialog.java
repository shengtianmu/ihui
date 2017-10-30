package com.zpstudio.ui.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.zpstudio.R;
import com.zpstudio.util.SystemUtil;

public class ActDialog extends Activity {
	public static final int    ICON  = R.drawable.tab_more_btn;
	public static final boolean REBACK_REQUIRED = true;
	public static final String INTENT_WEB_URL = "url";
	public static final String INTENT_TITLE = "title";
	static final String TAG = "ActDialog";
	public   String title;
	Button   btnTitleBack;
	TextView tvTitleText;
	
	WebView wv = null;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* windows style*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Intent intent = getIntent();
        String url = intent.getStringExtra(INTENT_WEB_URL);
        if(null == url )
        {
        	finish();
        	return;
        }
        title = intent.getStringExtra(INTENT_TITLE);
        if(null == title )
        {
        	title = "";
        }
        
        /* main layout */
        setContentView(R.layout.surfering);
        
        /* title */
        btnTitleBack = (Button)findViewById(R.id.title_reback_btn);
    	btnTitleBack.setVisibility(View.VISIBLE);
    	btnTitleBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	onGoBack();
            }  
        });
        
        tvTitleText  = (TextView)findViewById(R.id.title_text);
        tvTitleText.setText(title);
        
        
		wv = (WebView)findViewById(R.id.content);
		wv.setHorizontalScrollBarEnabled(false);//水平不显示
		wv.setVerticalScrollBarEnabled(false); //垂直不显示 
		
		wv.getSettings().setJavaScriptEnabled(true); 
		wv.addJavascriptInterface(this, "android");
		
		MyWebViewClient wc = new MyWebViewClient (this , SystemUtil.getContentView(this));
		wv.setWebViewClient(wc);
		
		wv.loadUrl(url);
        
		
	}
	
	
	
	void onGoBack()
	{
		if(wv.canGoBack())
		{
			wv.goBack();
		}
		else
		{
			finish();
		}
	}
	
	
}
