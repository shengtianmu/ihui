package com.zpstudio.ui.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hzjstudio.ihui.DownloadFileModel;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.zpstudio.R;
import com.zpstudio.contentprovider.LocalFileContentProvider;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.AppSetting;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.Location;
import com.zpstudio.datacenter.db.SoftwareVersion;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.im.ComparatorCellPhoneContact;
import com.zpstudio.datacenter.db.im.IMCellphoneContact;
import com.zpstudio.datacenter.db.im.IMCellphoneContact.OnCellphoneContactEvent;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnCreateWeixinAdv;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnWeixinPayReqEx;
import com.zpstudio.datacenter.db.op.IhuiClientApi.WeixinAdv;
import com.zpstudio.datacenter.db.weixin.pay.PayReqEx;
import com.zpstudio.ui.adv.LockScreenService;
import com.zpstudio.ui.game.puzzle.PuzzleGamePickerAct;
import com.zpstudio.ui.util.imagepicker.ActImagePicker;
import com.zpstudio.util.ImageUtil;
import com.zpstudio.util.Log;
import com.zpstudio.util.SystemUtil;
import com.zpstudio.util.WeixinShareManager;
import com.zxing.activity.ActQRScanner;

public class JavascriptExternal implements OnFileDownloadStatusListener,OnDownloadFileChangeListener{
	private static final String TAG = "external";
	public static final String NAME = "external";
	private final static int FILECHOOSER_RESULTCODE=1;
	private final static int QRSCAN_RESULTCODE=2;
	private final static int SHARE_RESULTCODE=3;
	private final static int ADDRESSPICKER_RESULTCODE=4;
	private final static int ONMORE_RESULTCODE=5;
	public static final String DATABASE_PATH = Environment.getExternalStorageDirectory() + "/ihui/webview/";
	public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory() + "/ihui/webview/appCache/";
	public static final String GLOBAL_PATH = Environment.getExternalStorageDirectory() + "/ihui/global/";
	
	private static final String SHARE_TO_FRIEND_CYCLE = "/bbs/web_sharelink.jsp";
	private static final int THUMB_SIZE = 48;
	
	Activity mActivity;
	WebView mWebview;
	private ValueCallback<Uri> mUploadMessage;
	Handler handler = new Handler();
	LocationClient mLocationClient ;
	MyLocationListener mMyLocationListener;
	LocationResult mLocationResult = null;
	Gson gson = new Gson();
	public StringBuffer sbLog = new StringBuffer();
	ShareTimelineSetting mShareTimelineSetting;
	NativeBroadcastReceiver mNativeBroadcastReceiver = new NativeBroadcastReceiver();
	
	FileSelectorOp mFileSelectorOp = null;
	boolean mIsEnableFileSelectorOp = false;
	
	Dialog mLoadingDlg = null;
	
	public class NativeBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction() == null)
			{
				return;
			}
			if(intent.getAction().equals(Config.INTENT_PUZZLE_GAME_WIN))
			{
				final String id = intent.getStringExtra(Config.EXTRA_PUZZLE_GAME_ID);
				handler.post(new Runnable() {

					@Override
					public void run() {
						callJs("javascript:puzzlegameWinInd(" + id +")");
					}
					
				});
			}
		}
		
	}
	public class VersionInfo
	{
		int versionCode;
		String versionName;
		String model;
		String manufacturer;
		String imei;
	}
	public class ViewImagesReq
	{
		ArrayList<String> urls;
		int pos;
	}
	public class Param{
		String text;
	}
	public class ExitSystemReq
	{
		String cause;
	}
	public class LocationResult
	{
		String msAddress;
		double mLatitude;
		double mLongitude;
	}
	public class NotificationSetting
	{
		String title;
		String text;
		int   id;
		boolean bCancelOnClick;
		boolean bPersisiting;
		String page;
		String param;
	}
	
	public class QRScanResult
	{
		int errorCode;
		String description;
		String result;
	}
	public class ShareTimelineSetting
	{
		String title;
		String link;
		String imgUrl;
	}
	class JsCallJavaByPrompt
	{
		String functionName;
		String param;
	}
	public class PuzzleGameReq
	{
		String url;
	}
	public class GotoLink
	{
		String url;
	}
	public class FileSelectorOp
	{
		int cropWidth;
		int cropHeight;
	}
	public class WeixinPrepayReq{
		String body ;
		String out_trade_no ;
		String total_fee ;
	}
	public class AdvFileDownloadReq{
		long adv_phone_id;
		String fileUrl;
	}
	public JavascriptExternal(Activity acitvity , WebView webview)
	{
		
		mActivity = acitvity;
		mWebview = webview;

		VersionInfo versionInfo = buildVersionInfo(mActivity);
		
		println("INFO" , "versionInfo is " + gson.toJson(versionInfo));
		println("INFO" , "SDK_INT is " + Integer.toString(android.os.Build.VERSION.SDK_INT));
		boolean status = createDirs(new String[]{DATABASE_PATH , APP_CACHE_PATH , GLOBAL_PATH});
		println("INFO" , "createDirs is " + status);
		
		mWebview.setHorizontalScrollBarEnabled(false);
		mWebview.setVerticalScrollBarEnabled(false);
		WebSettings websetting = mWebview.getSettings();
		
		websetting.setRenderPriority(RenderPriority.HIGH);
		websetting.setJavaScriptEnabled(true); 
		websetting.setDomStorageEnabled(true);
		websetting.setDatabaseEnabled(true);
		websetting.setDatabasePath(DATABASE_PATH);
		
		/*start appCache setting*/
		websetting.setAllowFileAccess(true);
		websetting.setAppCacheEnabled(true);
		websetting.setAppCacheMaxSize(1024*1024*8);
		//String appCacheDir = mActivity.getDir("cache", Context.MODE_PRIVATE).getPath();
		websetting.setAppCachePath(APP_CACHE_PATH);

		//ssl
		if(Build.VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN)
		{
			websetting.setAllowUniversalAccessFromFileURLs(true);
		}
		//check if running first time on this version
		SharedPreferences shPrf = mActivity.getSharedPreferences("setting" , 0);
		int savedVersionCode = shPrf.getInt("versionCode", 1);
		int curVersionCode = SoftwareVersion.getVersionCode(mActivity);
		println("INFO" , "savedVersionCode is " + savedVersionCode + ",curVersionCode is " + curVersionCode);
		if(savedVersionCode != curVersionCode)
		{
			shPrf.edit().putInt("versionCode", curVersionCode);
			shPrf.edit().commit();
			showTips();
			
		}
		
		if(SystemUtil.isNetworkAvailable(acitvity))
		{
			websetting.setCacheMode(WebSettings.LOAD_DEFAULT); 
		}
		else
		{
			websetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 
		}
		
		IntentFilter filter = new IntentFilter();
    	filter.addAction(Config.INTENT_PUZZLE_GAME_WIN);    
		mActivity.registerReceiver(mNativeBroadcastReceiver, filter);
		
		mLoadingDlg = DialogUtil.createLoadingDialog(mActivity , "");
	
		// location
		mLocationClient = new LocationClient(mActivity);
		mMyLocationListener = new MyLocationListener();;
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("gcj02");
		int span=1000;
		
		option.setScanSpan(span);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		
		
	}
	
	private boolean createDirs(String[] dirs)
	{
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			println("ERROR" , "createDirs:SD card not exists");
			return false;
		}
		
		for(int i = 0 ;i < dirs.length; i ++)
		{
			String path = dirs[i];
			File dir = new File(path);
			if (!dir.exists()) {
				if(!dir.mkdirs())
				{
					println("ERROR" , "createDirs:" + path + " failed");
					return false;
				}
			}
		}
		
		return true;
	}
	private void showTips() {
		// TODO Auto-generated method stub
		
	}

	public void destroy()
	{
		stopLocation();
	}
	
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if(null == mLocationResult){
				mLocationResult = new LocationResult();
			}
			mLocationResult.mLatitude = location.getLatitude();
			mLocationResult.mLongitude = location.getLongitude();
			mLocationResult.msAddress = location.getAddrStr();
			Location.save(mActivity, mLocationResult.mLongitude, 
					mLocationResult.mLatitude, 
					mLocationResult.msAddress);
			callJs("javascript:locationInd" , gson.toJson(mLocationResult));
			println("INFO" ,gson.toJson(mLocationResult) );
			
		}
	}
	
	
	private void startLocation(){
		if(mLocationClient.isStarted())
		{
			return;
		}
		
		
		mLocationClient.registerLocationListener(mMyLocationListener);
		mLocationClient.start();
	}
	
	private void stopLocation()
	{
		if(!mLocationClient.isStarted())
		{
			return;
		}
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(mMyLocationListener);
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if(requestCode==FILECHOOSER_RESULTCODE) 
		{ 
				
			if (null == mUploadMessage)
			{
				   return;
			}
			Uri result = null;
			if(Activity.RESULT_OK == resultCode)
			{
				result = Uri.parse("file://" + intent.getStringExtra("single_path")); 
		        
			}
			else
			{
				result = null;
			}
			mUploadMessage.onReceiveValue(result); 
	        mUploadMessage = null; 
	        
		}
		else if(requestCode==QRSCAN_RESULTCODE)
		{
			QRScanResult qrScanResult = new  QRScanResult();
			String scanResult="";
			if(resultCode == Activity.RESULT_OK)
			{
				//bar scan
				Bundle bundle = intent.getExtras();
				scanResult = bundle.getString("result");
				qrScanResult.errorCode = 0;
			}
			else
			{
				qrScanResult.errorCode = 1;
			}
			qrScanResult.result = scanResult;
			qrScanCnf(qrScanResult);
		}
		else if(requestCode==ADDRESSPICKER_RESULTCODE)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				String selectedAddress = intent.getStringExtra(AddressPickerAct.EXTRA_RESULT_SELECTED);
				addressPickCnf(selectedAddress);
			}
			
		}
		else if(requestCode==ONMORE_RESULTCODE)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				int menu = intent.getIntExtra(WebMorePopupMenuActivity.EXTRA_ACTION_RESULT_SELECTED , WebMorePopupMenuActivity.MENU_NONE);
				
				doShareToWeixin(mWebview.getOriginalUrl() , menu);
			}
			
		}
		
		
	}
	public void onMore()
	{
		Intent i = new Intent(mActivity , WebMorePopupMenuActivity.class);
		mActivity.startActivityForResult(i, ONMORE_RESULTCODE);
		
	}
	public void qrScanCnf(final QRScanResult qrScanResult)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				callJs("javascript:qrScanCnf" , gson.toJson(qrScanResult));
			}
			
		});
	}
	public void addressPickCnf(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				callJs("javascript:addressPickCnf" , json);
			}
			
		});
	}
	public void walletUpdatedInd()
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				callJs("javascript:walletUpdatedInd()");
			}
			
		});
	}
	public void doOpenFileChooser(ValueCallback<Uri> uploadMsg)
	{
		println("INFO" , "doOpenFileChooser");
		int cropWidth = 300;
		int cropHeight = 300;
		int quality = 100;
		if(mIsEnableFileSelectorOp)
		{
			mIsEnableFileSelectorOp = false;
			cropWidth = mFileSelectorOp.cropWidth;
			cropHeight = mFileSelectorOp.cropHeight;
		}
		mUploadMessage = uploadMsg; 
		
		ActImagePicker.pickImage(mActivity,FILECHOOSER_RESULTCODE,cropWidth , cropHeight , quality);
		/*
		Intent i = new Intent(ActImagePicker.ACTION_PICK);
		mActivity.startActivityForResult(i, FILECHOOSER_RESULTCODE);
		*/
		
	}

	void stopScreenService()
	{
		mActivity.stopService(new Intent(mActivity, LockScreenService.class));
	}
    public void startScreenService()
    {
    	mActivity.startService(new Intent(mActivity,LockScreenService.class));  
    }
    
    
    public boolean onConsoleMessage(ConsoleMessage cm)
    {
 	   MessageLevel level = cm.messageLevel();
 	   String msg = cm.message() + "-- From line " + cm.lineNumber() + " of " + cm.sourceId();
 	   if(0 == level.compareTo(MessageLevel.ERROR))
 	   {
 		   Log.e(TAG, msg);
 	   }
 	   else if(0 == level.compareTo(MessageLevel.WARNING))
 	   {
 		   Log.w(TAG, msg);
 	   }
 	   else if(0 == level.compareTo(MessageLevel.DEBUG))
 	   {
 		   Log.d(TAG, msg);
 	   }
 	   else if(0 == level.compareTo(MessageLevel.LOG))
 	   {
 		   Log.i(TAG, msg);
 	   }
 	   else if(0 == level.compareTo(MessageLevel.TIP))
 	   {
 		   Log.v(TAG, msg);
 	   }
 	   println(level.toString() , msg);
 	   return false;
    }
    
    private void initShareIntent(String type , String msg , Uri uri) {
    	/*
	    
		*/
    	if(null == type)
    	{
    		ShareAppAct.share(mActivity, SHARE_RESULTCODE, msg);
		   
    	}
	   
	}
    
    public void callJs(String js)
    {
    	mWebview.loadUrl(js);
    }
    
    public void callJs(String fn , String param)
    {
    	String js = String.format("%s('%s')", fn , param);
    	println("INFO","callJs:" + js);
    	mWebview.loadUrl(js);
    }
    
    public String getLog()
    {
    	return sbLog.toString();
    }
    public void showLog()
    {
    	DialogTextView dialog = new DialogTextView(mActivity, getLog());
		dialog.show();
    }
    
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
    	
		Class clazz = this.getClass();
		try {
			JsCallJavaByPrompt jsCallJavaByPrompt = gson.fromJson(message, JsCallJavaByPrompt.class);
			Method m = clazz.getDeclaredMethod(jsCallJavaByPrompt.functionName, String.class);
			m.invoke(this, jsCallJavaByPrompt.param);
		} catch(JsonSyntaxException e) {
			e.printStackTrace();
    		Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
    	}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
		} 
		
    	result.confirm("");
        return true;
    }

    String currentTime()
    {
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
    	return df.format(new Date());
    }
    
    public void println(String level , String msg)
    {
    	sbLog.append("[" + level + "]" + currentTime()+ ":" + msg + "\n\r");
    }
    
    protected void doClearCache() {
		// TODO Auto-generated method stub
    	println("WARNING" , "doClearCache");
    	
	}

    public void doShareToFriendCycle() {
		// TODO Auto-generated method stub
		if(null != mShareTimelineSetting )
		{
			User user = User.loadFromLocal(mActivity);
			if(null != user)
			{
				String url = Config.getFullUrl(mActivity, SHARE_TO_FRIEND_CYCLE);
				url += "?";
				try {
					url += "title=" + URLEncoder.encode(mShareTimelineSetting.title, "UTF-8");
					url += "&link=" + URLEncoder.encode(mShareTimelineSetting.link, "UTF-8");
					url += "&imgUrl=" + URLEncoder.encode(mShareTimelineSetting.imgUrl, "UTF-8");
					url += "&phone_number=" + URLEncoder.encode(user.getPhone_number(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent i = new Intent();
				i.setClass(mActivity, ActBrowser.class);
				i.putExtra(ActBrowser.INTENT_WEB_URL, url);
				mActivity.startActivity(i);
			}
		}
		
	}
    public void onConfigureShareFriendCycleSetting()
    {
    	
    }
    
    public void doShareToWeixin(String url , final int shareType) {
    	showLoadingDlg();
		IhuiClientApi.getInstance(mActivity).createWeixinAdv(url,
				new ListenerOnCreateWeixinAdv(){

					@Override
					public void onSuccess(final WeixinAdv weixinAdv) {
						// TODO Auto-generated method stub
							println("INFO" , gson.toJson(weixinAdv));
							
							ImageUtil.loadImageAsync(weixinAdv.picture, THUMB_SIZE, THUMB_SIZE, new ImageUtil.LoadingListener(){

								@Override
								public void onLoadedComplete(byte[] array) {
									// TODO Auto-generated method stub
									println("INFO" , "onLoadedComplete " + array.length );
									boolean status =  false;
									WeixinShareManager instance = WeixinShareManager.getInstance(mActivity);
									
									status = instance.shareByWeixin(
											instance.new ShareContentWebpage(
													weixinAdv.title,
													weixinAdv.content,
													weixinAdv.url,
													array
													),
													shareType);
									
									println("INFO" , "shareByWeixin " + status );
									if(!status)
									{
										Bitmap defaultBmp = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ihui);
										byte[] defaultArray = ImageUtil.bmpToByteArray(defaultBmp, true); 
										status = instance.shareByWeixin(
												instance.new ShareContentWebpage(
														weixinAdv.title,
														weixinAdv.content,
														weixinAdv.url,
														defaultArray
														),
														shareType);
										println("WARNING" , "shareByWeixin using default icon(" + defaultArray.length + ") " + status );
										defaultArray = null;
										
									}
									
									dismissLoadingDlg();
									
								}

								@Override
								public void onLoadedFail(String e) {
									// TODO Auto-generated method stub
									println("ERROR" , "imageloading fail:" + e);
									dismissLoadingDlg();
								}});
							
						
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						println("ERROR" , msg);
						dismissLoadingDlg();
					}
			
		});
		
	}
    private void showLoadingDlg()
    {
    	mLoadingDlg.show();
    }
    
    private void dismissLoadingDlg()
    {
    	mLoadingDlg.dismiss();
    }
   
    protected boolean copyRaw(int id , String name)
    {
		try {
			InputStream in = mActivity.getResources().openRawResource(id);
	    	FileOutputStream fos = new FileOutputStream(new File(GLOBAL_PATH , name));
			byte[] buffer = new byte[8192];
		    int count = 0;
		    while ((count = in.read(buffer)) > 0)
		    {
		     fos.write(buffer, 0, count);
		    }
		    fos.close();
		    in.close(); 
		    return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return false;
	    
    }
    protected Uri getRawResource(int id , String name) {
    	
    	if(copyRaw(id , name))
    	{
    		return Uri.fromFile(new File(GLOBAL_PATH , name));
    	}
    	else
    	{
    		return null;
    	}
	}
    
    VersionInfo buildVersionInfo(Context context)
    {
    	VersionInfo versionInfo = new VersionInfo();
    	versionInfo.versionCode = SoftwareVersion.getVersionCode(context);
		versionInfo.versionName = SoftwareVersion.getVersion(context);
		versionInfo.model       = android.os.Build.MODEL;
		versionInfo.manufacturer= android.os.Build.MANUFACTURER;
		versionInfo.imei        = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return versionInfo;
    }
    @JavascriptInterface
	public void addWeixinFriend(final String json)
	{
		handler.post(new Runnable() {

			public void run() {
				Param param = gson.fromJson(json, Param.class);
				initShareIntent("com.tencent.mm" , param.text , getRawResource(R.raw.ihui , "ihui.png"));//mActivity.getString(R.string.share_to_without_link_context));
			}
		});
	}
	
	@JavascriptInterface
	public void addQQFriend(final String json)
	{
		handler.post(new Runnable() {

			public void run() {
				Param param = gson.fromJson(json, Param.class);
				
				initShareIntent("tencent.mobileqq" , param.text , getRawResource(R.raw.ihui , "ihui.png"));//mActivity.getString(R.string.share_to_context));
			}
		});
	}
	

	@JavascriptInterface
	public void shareBySms(final String json)
	{
		handler.post(new Runnable() {

			public void run() {
				/*
				Param param = gson.fromJson(json, Param.class);
				String apkDownloadWebsite = param.text;
				Uri smsToUri = Uri.parse( "smsto:" );
				Intent i = new Intent(Intent.ACTION_VIEW,smsToUri); 
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		        i.putExtra("sms_body",apkDownloadWebsite);
		        i.setType("vnd.android-dir/mms-sms");
		        mActivity.startActivity(Intent.createChooser(i, "����"));
		        */
				

				Param param = gson.fromJson(json, Param.class);
				initShareIntent(null , param.text , getRawResource(R.raw.ihui , "ihui.png"));//mActivity.getString(R.string.share_to_context));
			
			}
		});
	}
	
	@JavascriptInterface
	public void viewImages(final String json)
	{
		handler.post(new Runnable() {

			public void run() {
				
		        
			}
		});
	}
	@JavascriptInterface
	public void getPhonebookAsync(final String json)
	{
		handler.post(new Runnable() {

			public void run() {
				IMCellphoneContact.getAllAsync(mActivity, new OnCellphoneContactEvent() {
					public List<IMCellphoneContact> mPhoneContactList = new ArrayList<IMCellphoneContact>();
					@Override
					public void onStarted() {
						mPhoneContactList.clear();
					}

					@Override
					public void onAdd(IMCellphoneContact contact) {
						// TODO Auto-generated method stub
						
						mPhoneContactList.add(contact);
					}

					@Override
					public void onFinished() {
						// TODO Auto-generated method stub
						Collections.sort(mPhoneContactList,
								new ComparatorCellPhoneContact());
						
						String json = gson.toJson(mPhoneContactList);
						
						callJs("javascript:phonebookCnf" , json);
						mPhoneContactList.clear();
					}

				});
		        
			}
		});
	}
	
	@JavascriptInterface
	public void getLocationStartAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startLocation();
				
			}
			
		});
	}
	
	@JavascriptInterface
	public void getLocationStopAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopLocation();
			}
			
		});
	}
	
	@JavascriptInterface
	public void sendNotificationAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				NotificationSetting setting = gson.fromJson(json, NotificationSetting.class);
				NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity)
													.setSmallIcon(R.drawable.ihui)
													.setContentText(setting.text)
													.setContentTitle(setting.title)
													.setAutoCancel(setting.bCancelOnClick)
													.setTicker(setting.text)
													.setDefaults(Notification.DEFAULT_ALL)
													.setOnlyAlertOnce(true);
				
				Intent notificationIntent = new Intent(mActivity , mActivity.getClass());
				notificationIntent.putExtra("page", setting.page);
				notificationIntent.putExtra("param", setting.param);
				
				PendingIntent contentIntent = PendingIntent.getActivity(mActivity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				builder.setContentIntent(contentIntent);
				
				NotificationManager manager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.notify(setting.id, builder.build());
			}
			
		});
	}
	
	@JavascriptInterface
	public void cancelNotificationAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				NotificationSetting setting = gson.fromJson(json, NotificationSetting.class);
				
				NotificationManager manager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.cancel(setting.id);
			}
			
		});
	}
	
	@JavascriptInterface
	public void updateUserAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				try{
					sbLog.append(json);
					User user = gson.fromJson(json, User.class);
					User.saveToLocal(mActivity, user, null);
					startScreenService();
				}
				catch(Exception e)
				{
					sbLog.append(e);
				}
				
			}
			
		});
	}
	
	@JavascriptInterface
	public void updateSettingAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				AppSetting setting = gson.fromJson(json, AppSetting.class);
				AppSetting oldSetting = AppSetting.loadFromLocal(mActivity);
				oldSetting.setEnable_lockscreen(setting.getEnable_lockscreen());
				AppSetting.saveToLocal(mActivity, oldSetting);
				
			}
			
		});
	}
	
	@JavascriptInterface
	public void exitSystem(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				User.deleteAll(mActivity);
				stopScreenService();
				mActivity.finish();
			}
			
		});
	}
	
	@JavascriptInterface
	public void finishApp(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				
				mActivity.finish();
			}
			
		});
	}
	
	@JavascriptInterface
	public void getVersionAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				VersionInfo versionInfo = buildVersionInfo(mActivity);
				
				String js = String.format("javascript:versionCnf('%s')", gson.toJson(versionInfo));
				callJs(js);
			}
			
		});
	}
	
	@JavascriptInterface
	public void clearCacheAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				doClearCache();
			}
			
		});
	}
	
	
	@JavascriptInterface
	public void debugMode(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				showLog();
			}
			
		});
	}
	
	@JavascriptInterface
	public void fileSelectReq(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				doOpenFileChooser(new ValueCallback<Uri>(){

					@Override
					public void onReceiveValue(Uri value) {
						// TODO Auto-generated method stub
						File file = new File(value.getPath());
						String json = LocalFileContentProvider.constructUri(file.getAbsolutePath());
						callJs("javascript:fileSelectCnf" , json);
					}});
				
			}
			
		});
	}
	@JavascriptInterface
	public void fileSelectorOp(final String json)
	{
		mFileSelectorOp = gson.fromJson(json, FileSelectorOp.class);
		mIsEnableFileSelectorOp = true;
	}
	@JavascriptInterface
	public void qrScanReqAsync(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				Intent openCameraIntent = new Intent(mActivity,ActQRScanner.class);
				mActivity.startActivityForResult(openCameraIntent, QRSCAN_RESULTCODE);
				
			}
			
		});
		
		
	}
	
	@JavascriptInterface
	public void onMenuShareTimeline(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				
				mShareTimelineSetting = gson.fromJson(json, ShareTimelineSetting.class);
				onConfigureShareFriendCycleSetting();
			}
			
		});
	}
	
	@JavascriptInterface
	public void puzzleGameReq(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				
				PuzzleGameReq puzzleGameReq = gson.fromJson(json, PuzzleGameReq.class);
				Intent i = new Intent(mActivity, PuzzleGamePickerAct.class);
				i.putExtra(PuzzleGamePickerAct.EXTRA_URL, puzzleGameReq.url);
				mActivity.startActivity(i);
			}
			
		});
	}
	
	@JavascriptInterface
	public void gotoLink(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				
				GotoLink gotoLink = gson.fromJson(json, GotoLink.class);
				Intent i = new Intent();
				i.setClass(mActivity, ActBrowser.class);
				ArrayList<String > blacklist = new ArrayList<String>();
				i.putStringArrayListExtra(ActBrowser.INTENT_URL_BLACKLIST, blacklist);
				i.putExtra(ActBrowser.INTENT_WEB_URL, gotoLink.url);
				mActivity.startActivity(i);
			}
			
		});
	}
	@JavascriptInterface
	public void addressPickReq(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				AddressPickerAct.addressPick(mActivity, ADDRESSPICKER_RESULTCODE, json);
			}
			
		});
	}
	
	@JavascriptInterface
	public void weixinPayReq(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				println("INFO" , "weixinPayReq:" + json);
				WeixinPrepayReq weixinPrepayReq = gson.fromJson(json, WeixinPrepayReq.class);
				IhuiClientApi.getInstance(mActivity).prepareWeixinpayAsync(weixinPrepayReq.body, 
						weixinPrepayReq.out_trade_no, 
						weixinPrepayReq.total_fee, 
						new ListenerOnWeixinPayReqEx(){

							@Override
							public void onSuccess(PayReqEx payReqEx) {
								// TODO Auto-generated method stub
								Log.i(TAG, "onSuccess:" + gson.toJson(payReqEx));
								println("INFO" , "onSuccess:" + gson.toJson(payReqEx));
								PayReq req = new PayReq();
								req.appId			= payReqEx.getAppId();
								req.partnerId		= payReqEx.getPartnerId();
								req.prepayId		= payReqEx.getPrepayId();
								req.nonceStr		= payReqEx.getNonceStr();
								req.timeStamp		= payReqEx.getTimeStamp();
								req.packageValue	= "Sign=WXPay";//payReqEx.getPackageValue();
								req.sign			= payReqEx.getSign();
								// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
								//mIWXAPI.registerApp(req.appId);
								IhuiClientApi.getInstance(mActivity).getWeixinPayApi().sendReq(req);
								
							}

							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								println("INFO" , "onFail:" + msg);
								AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
								builder.setMessage(msg);
								builder.show();
								
							}});
			}
			
		});
	}

	private Adv adv;
	private DownloadFileModel obj;
	private AdvFileDownloadReq req;
	public TextView tv_action;
	public ProgressBar pb_progress;
	
	@JavascriptInterface
	public void advFileDownloadReq(final String json)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				req = gson.fromJson(json, AdvFileDownloadReq.class);
//				Toast.makeText(mActivity, json, Toast.LENGTH_LONG).show();
//				Log.e("111", req.fileUrl+""+req.adv_phone_id);
				FileDownloader.registerDownloadStatusListener(JavascriptExternal.this);
		        FileDownloader.registerDownloadFileChangeListener(JavascriptExternal.this);
//				IhuiClientApi.getInstance(getActivity()).downloadScoreAddAsync(req.adv_phone_id);
		        adv=new Adv();
		        adv.setLink(req.fileUrl);
				obj = new DownloadFileModel(mActivity , adv);
				View view=LayoutInflater.from(mActivity).inflate(R.layout.adapter_download, null);
				RelativeLayout.LayoutParams relLayoutParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				mActivity.addContentView(view, relLayoutParams);
				tv_action=(TextView) view.findViewById(R.id.tv_action);
				pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
				registerEventListener(tv_action, obj);
			}
		});
	}
	
	
	void downloadComplete(String link)
	{
//		IhuiClientApi.getInstance(mActivity).downloadScoreAddAsync(req.adv_phone_id);
	}
	
	private void registerEventListener(final TextView tv_action, final DownloadFileModel obj) {
		tv_action.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	if(obj.isbInstalled())
    			{
            		obj.startActivity();
    			}
    			else if(obj.isDownloaded())
    			{
    				obj.install();
    			}
    			else if(obj.isDownloading())
    			{
    				FileDownloader.pause(obj.getAdv().getLink());
    			}
    			else if(obj.isPaused())
    			{
    				FileDownloader.reStart(obj.getAdv().getLink());
    			}
    			else if(obj.isNew())
    			{
    				FileDownloader.start(obj.getAdv().getLink());
    			}
    			
            }
		});
		updateView(obj);
	}
	
	
	void updateView(DownloadFileModel obj)
	{
		double percent = obj.getPercent();
		if(obj.isbInstalled())
		{
			tv_action.setText(mActivity.getString(R.string.open));
			pb_progress.setProgress(pb_progress.getMax()); 
		}
		else if(obj.isDownloaded())
		{
			tv_action.setText(mActivity.getString(R.string.install));
			tv_action.setTextColor(0xffffffff);
			pb_progress.setProgress(pb_progress.getMax()); 
		}
		else if(obj.isDownloading())
		{
			tv_action.setText((int)(percent*100)+"%");
			pb_progress.setProgress((int)(percent*100)); 
		}
		else if(obj.isPaused())
		{
			tv_action.setText(mActivity.getString(R.string.redownload));
			pb_progress.setProgress((int)(percent*100)); 
		}
		else if(obj.isNew())
		{
			tv_action.setText(mActivity.getString(R.string.download2));
			pb_progress.setProgress(0); 
		}
	}
	
	
	
	private DownloadFileModel onFileDownloadStatus(DownloadFileInfo downloadFileInfo)
	{
//		DownloadFileModel obj = null;
		if (downloadFileInfo == null) {
			Log.e(TAG , "onFileDownloadStatus downloadFileInfo is null");
            return obj;
        }
//
//        WeakReference<View> weakCacheConvertView = mConvertViews.get(downloadFileInfo.getUrl());
//        if (weakCacheConvertView == null || weakCacheConvertView.get() == null) {
//        	Log.e(TAG , "onFileDownloadStatus (weakCacheConvertView == null || weakCacheConvertView.get() == null)");
//            return obj;
//        }

//        View cacheConvertView = weakCacheConvertView.get();
//        DownloadHolder holder = (DownloadHolder)cacheConvertView.getTag();
//        obj = findModelByLink(downloadFileInfo.getUrl());
        updateView(obj);
        return obj;
	}
	
	
	
	

	@Override
	public void onFileDownloadStatusWaiting(
			DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		Log.i(TAG , "onFileDownloadStatusWaiting  ");
		onFileDownloadStatus(downloadFileInfo);
		
	}


	@Override
	public void onFileDownloadStatusPreparing(
			DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		Log.i(TAG , "onFileDownloadStatusPreparing  ");
		onFileDownloadStatus(downloadFileInfo);
	}


	@Override
	public void onFileDownloadStatusPrepared(
			DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		Log.i(TAG , "onFileDownloadStatusPrepared  ");
		onFileDownloadStatus(downloadFileInfo);
	}


	@Override
	public void onFileDownloadStatusDownloading(
			DownloadFileInfo downloadFileInfo, float downloadSpeed,
			long remainingTime) {
		// TODO Auto-generated method stub
		Log.i(TAG , "onFileDownloadStatusDownloading  ");
		onFileDownloadStatus(downloadFileInfo);
        
	}


	@Override
	public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		Log.i(TAG , "onFileDownloadStatusPaused  ");
		onFileDownloadStatus(downloadFileInfo);
        
	}


	@Override
	public void onFileDownloadStatusCompleted(
			DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		Log.i(TAG , "onFileDownloadStatusCompleted  ");
		DownloadFileModel obj = onFileDownloadStatus(downloadFileInfo);
        if(null != obj)
        {
        	obj.install();
        }
        else
		{
			Log.e(TAG, "onFileDownloadStatusCompleted:"  + downloadFileInfo.getUrl()+ " has no obj");
		}
        downloadComplete(downloadFileInfo.getUrl());
	}


	@Override
	public void onFileDownloadStatusFailed(String url,
			DownloadFileInfo downloadFileInfo,
			FileDownloadStatusFailReason failReason) {
		// TODO Auto-generated method stub
		Log.i("homer" , "onFileDownloadStatusFailed  ");
		onFileDownloadStatus(downloadFileInfo);
	}


	@Override
	public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		
		if(null == downloadFileInfo)
		{
			Log.e(TAG, "onDownloadFileCreated downloadFileInfo is null");
			return ;
		}
		Log.i(TAG , "onDownloadFileCreated " + downloadFileInfo.getUrl());
//		DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
		if(null != obj)
		{
			obj.setDownloadFileInfo(downloadFileInfo);
		}
		else
		{
			Log.e(TAG, "onDownloadFileCreated:"  + downloadFileInfo.getUrl()+ " has no obj");
		}
		onFileDownloadStatus(downloadFileInfo);
	}


	@Override
	public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo,
			Type type) {
		// TODO Auto-generated method stub
		if(null == downloadFileInfo)
		{
			Log.e(TAG, "onDownloadFileUpdated downloadFileInfo is null");
			return ;
		}
		Log.i(TAG , "onDownloadFileUpdated " + downloadFileInfo.getUrl());
//		DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
		if(null != obj)
		{
			obj.setDownloadFileInfo(downloadFileInfo);
		}
		else
		{
			Log.e(TAG, "onDownloadFileUpdated:"  + downloadFileInfo.getUrl()+ " has no obj");
		}
		onFileDownloadStatus(downloadFileInfo);
	}


	@Override
	public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		if(null == downloadFileInfo)
		{
			Log.e(TAG, "onDownloadFileDeleted downloadFileInfo is null");
			return ;
		}
		Log.i(TAG , "onDownloadFileDeleted " + downloadFileInfo.getUrl());
//		DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
		if(null != obj)
		{
			obj.setDownloadFileInfo(null);
		}
		else
		{
			Log.e(TAG, "onDownloadFileDeleted:"  + downloadFileInfo.getUrl()+ " has no obj");
		}
		onFileDownloadStatus(downloadFileInfo);
	}
	
}
