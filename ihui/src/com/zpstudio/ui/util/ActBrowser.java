package com.zpstudio.ui.util;

import java.util.ArrayList;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zpstudio.R;
import com.zpstudio.ui.util.progressWeb.ProgressWebChromeClient;
import com.zpstudio.util.SystemUtil;

public class ActBrowser extends Activity  {
	public static final String DATABASE_PATH = Environment.getExternalStorageDirectory() + "/ihui/webview/";
	public static final int    ICON  = R.drawable.tab_more_btn;
	public static final boolean REBACK_REQUIRED = true;
	public static final String INTENT_URL_BLACKLIST = "url_blacklist";
	public static final String INTENT_WEB_URL = "url";
	public static final String INTENT_TITLE = "title";
	static final String TAG = "ActBrowser";
	private static int DEBUG_THRESHOLD_COUNT = 5;
	public   String title="";
	Button   btnTitleBack;
	TextView tvTitleText;
	ImageButton   btnMore;
	WebView wv = null;
	MyWebViewClient wc = null;
	JavascriptExternal mJE = null;
	
	ArrayList<String> mBlackList = null;
	
	int debugModeCount = 0;
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, 
	                                    Intent intent) { 
		mJE.onActivityResult(requestCode, resultCode, intent);
	} 
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* windows style*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Intent intent = getIntent();
        mBlackList = intent.getStringArrayListExtra(INTENT_URL_BLACKLIST);
        if(null == mBlackList)
        {
        	mBlackList = new ArrayList<String>();
        }
        
        String url = intent.getStringExtra(INTENT_WEB_URL);
        
        if(null == url )
        {
        	finish();
        	return;
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
        tvTitleText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(0 == (++debugModeCount)%DEBUG_THRESHOLD_COUNT)
            	{
            		onDebug();
            	}
            }  
        });
        
        /* more */
        btnMore = (ImageButton)findViewById(R.id.right_btn);
        btnMore.setVisibility(View.GONE);
        btnMore.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	onMore(v);
            }  
        });
        
		wv = (WebView)findViewById(R.id.content);
		
		
		mJE = new JavascriptExternal(this , wv){
			public void onConfigureShareFriendCycleSetting()
			{
				btnMore.setVisibility(View.VISIBLE);
			}
		};
		wv.addJavascriptInterface(mJE, JavascriptExternal.NAME);
		
		wc = new MyWebViewClient (this , SystemUtil.getContentView(this) , mBlackList , mJE.sbLog);
		wv.setWebViewClient(wc);
		
		wv.setWebChromeClient(new ProgressWebChromeClient(findViewById(R.id.progressBar)) 
	    { 
			@Override  
            public void onReceivedTitle(WebView view, String title) {  
                super.onReceivedTitle(view, title);  
                if(title.trim().equals(""))
                {
                	findViewById(R.id.title_layout).setVisibility(View.GONE);
                }
                else
                {
                	tvTitleText.setText(title);
                }
            }  
			
	           //The undocumented magic method override 
	           //Eclipse will swear at you if you try to put @Override here 
	        // For Android 3.0+
	        public void openFileChooser(ValueCallback<Uri> uploadMsg) { 
	        		mJE.doOpenFileChooser(uploadMsg);
	           }
	 
	        // For Android 3.0+
	           public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
	        	   mJE.doOpenFileChooser(uploadMsg);
	           }
	 
	        //For Android 4.1
	           public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
	        	   mJE.doOpenFileChooser(uploadMsg);
	           }
	           
	           public boolean onConsoleMessage(ConsoleMessage cm)
	           {
	        	   return mJE.onConsoleMessage(cm);
	           }
	    }); 
		mJE.println("INFO" , "loadUrl:" + url);
		wv.loadUrl(url);
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		SystemUtil.clearCacheFolder(this.getCacheDir(), System.currentTimeMillis());//ɾ���ʱ֮ǰ�Ļ���.
	}
	
	public boolean onKeyDown(int keyCode , KeyEvent event)
	{
		if(   event.getAction() == KeyEvent.ACTION_DOWN 
			&&KeyEvent.KEYCODE_BACK == keyCode )
		{
			onGoBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
		
	void onGoBack()
	{
		btnMore.setVisibility(View.GONE);
		if(!wc.onGoback())
		{
			finish();
		}
	}
	void onMore(View v)
	{
		mJE.onMore();
	}
	void onDebug()
	{
		mJE.showLog();
	}
}
