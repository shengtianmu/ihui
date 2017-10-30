package com.zpstudio.ui.prelude;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zpstudio.R;
import com.zpstudio.app.MainApplication;
import com.zpstudio.contentprovider.IhuiProvider;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.SoftwareVersion;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.im.ComparatorCellPhoneContact;
import com.zpstudio.datacenter.db.im.IMCellphoneContact;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.ui.util.ActBrowser;
import com.zpstudio.ui.util.JavascriptExternal;
import com.zpstudio.ui.util.MyWebViewClient;
import com.zpstudio.ui.util.progressWeb.ProgressWebChromeClient;
import com.zpstudio.util.SystemUtil;


public class Appstart extends Activity {
	

	public static final int MENU_DEBUG=Menu.FIRST;//����


	private static final String ROOT_URL = "file:///android_asset/webim/web_frame.html";
	public   String title;
	Button   btnTitleBack;
	TextView tvTitleText;
	
	MyWebViewClient wc;
	JavascriptExternal mJE = null;
	
	WebView wv = null;
	Activity mySelf;
	
	String mStartUrl ;
	
	boolean bStarted = false;
	ContentObserver walletContentObserver = new ContentObserver(new Handler())
	{
		public void onChange(boolean selfChange) {
			Wallet wallet = Wallet.loadFromLocal(mySelf);
			if(null != wallet && wallet.getNotify_credit() > 0)
			{
				mJE.walletUpdatedInd();
			}
			
        }
	};
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, 
	                                    Intent intent) { 
		mJE.onActivityResult(requestCode, resultCode, intent);
	} 
	
	@Override 
	public void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
		if(null != intent)
		{
			String page = intent.getStringExtra("page");
			String param = intent.getStringExtra("param");
			
			String js = String.format("javascript:forwardPageEx('%s' , '%s')", page , param);
			mJE.callJs(js);
		}
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !wc.getIsError() ) {
        	moveTaskToBack(true);
            return true;
			
        } 
        else 
        {
            return super.onKeyDown(keyCode, event);
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mySelf = this;
		/* windows style*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        /* main layout */
        setContentView(R.layout.surfering);
        
        findViewById(R.id.title_layout).setVisibility(View.GONE);
        
		wv = (WebView)findViewById(R.id.content);

		
		mJE = new JavascriptExternal(this , wv);
		if(android.os.Build.VERSION.SDK_INT > 10)
		{
			wv.addJavascriptInterface(mJE, JavascriptExternal.NAME);
		}
		wc = new MyWebViewClient (this , SystemUtil.getContentView(this)){
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				onWebViewUrl(url);
			    return true;
		    }
			
		};
		wc.configProgress(false, true, false);
		wc.enableShowError(true);
		wv.setWebViewClient(wc);
		
		
		wv.setWebChromeClient(new ProgressWebChromeClient(findViewById(R.id.progressBar)) 
	    { 
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
	           
	           @Override
	           public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
	               
	               return mJE.onJsPrompt(view, url, message, defaultValue, result);
	           }
	           
	    }); 
		
		IMCellphoneContact.getAllAsync(this, new IMCellphoneContact.OnCellphoneContactEvent() {
			@Override
			public void onStarted() {
				MainApplication.mPhoneContactList.clear();
			}

			@Override
			public void onAdd(IMCellphoneContact contact) {
				// TODO Auto-generated method stub
				
				MainApplication.mPhoneContactList.add(contact);
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				Collections.sort(MainApplication.mPhoneContactList,
						new ComparatorCellPhoneContact());
				
			}

		});
		
		getContentResolver().registerContentObserver(Uri.parse(IhuiProvider.WALLET_CONTENT_URIS), true, walletContentObserver);
		
		mJE.startScreenService();
		
		SoftwareVersion.softwareUpdate(this);
		
	}
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		
		if(!bStarted)
		{
			bStarted = true;
			Rect outRect = new Rect();
			//this.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
			getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
			//DisplayMetrics dm = new DisplayMetrics();
			//getWindowManager().getDefaultDisplay().getMetrics(dm);
			int widthDip = SystemUtil.px2dip(this, outRect.right - outRect.left);//���
			int heightDip = SystemUtil.px2dip(this, outRect.bottom - outRect.top);//�߶�
			String url = Config.getFullUrl(this, ROOT_URL + "?windowWidth=" + widthDip + "&windowHeight=" + heightDip);
			
			Intent intent = getIntent();
			if(null != getIntent())
			{
				String page = intent.getStringExtra("page");
				String param = intent.getStringExtra("param");
				
				if(page != null)
				{
					url = addOrModifyParameter(url , "page" , page);
				}
				if(param != null)
				{
					url = addOrModifyParameter(url , "param" , param);
				}
			}
			mStartUrl = url;
			wv.loadUrl(url);
		}
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,MENU_DEBUG,0,"����"); //����
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId,MenuItem item){
		switch(item.getItemId()){
		case MENU_DEBUG:
			mJE.showLog();
		break;
		}
		//���ظ���ִ��
		return super.onMenuItemSelected(featureId, item);
	}
	*/
	public void onWebViewUrl(String url)
	{
		Intent i = new Intent();
		i.setClass(this, ActBrowser.class);
		ArrayList<String > blacklist = new ArrayList<String>();
		blacklist.add(mStartUrl);
		i.putStringArrayListExtra(ActBrowser.INTENT_URL_BLACKLIST, blacklist);
		i.putExtra(ActBrowser.INTENT_WEB_URL, url);
		startActivity(i);
	}
	
	String addOrModifyParameter(String link ,String key ,String value)
	{
		if(link.indexOf("?") > 0 )
		{
		    return link + "&" + key+ "=" + value;
		}
		else
		{
			return link + "?" + key+ "=" + value;
		}
	}
	
	public void onDestroy()
	{
		getContentResolver().unregisterContentObserver(walletContentObserver);
		
		super.onDestroy();
	}
	
	String getUserId() {
		Object userManager = getSystemService("user");
		if (userManager == null) {
			Toast.makeText(this, "userManager is null", Toast.LENGTH_LONG).show();
			return null;
		}

		try {
			Method myUserHandleMethod = android.os.Process.class.getMethod(
					"myUserHandle", (Class<?>[]) null);
			Object myUserHandle = myUserHandleMethod.invoke(
					android.os.Process.class, (Object[]) null);

			Method getSerialNumberForUser = userManager.getClass().getMethod(
					"getSerialNumberForUser", myUserHandle.getClass());
			long userSerial = (Long) getSerialNumberForUser.invoke(userManager,
					myUserHandle);
			Toast.makeText(this, "userSerial is " + String.valueOf(userSerial), Toast.LENGTH_LONG).show();
			return String.valueOf(userSerial);
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		Toast.makeText(this, "userSerial is null" , Toast.LENGTH_LONG).show();
		
		return null;
	}
	
	void uploadLog()
	{
		String who = "";
		User mUser = User.loadFromLocal(this);
		if(null != mUser)
		{
			who = mUser.getPhone_number();
		}
		else
		{
			who = ((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		}
		
		IhuiClientApi.getInstance(this).uploadLog(who, mJE.getLog());
	}
}
