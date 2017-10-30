package com.zpstudio.ui.adv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.zpstudio.R;
import com.zpstudio.app.MainApplication;
import com.zpstudio.contentprovider.IhuiProvider;
import com.zpstudio.datacenter.db.AppSetting;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.util.Log;
import com.zpstudio.util.SystemUtil;
import com.zpstudio.util.UpdateManager;

/**
 * adb shell am broadcast -a android.intent.action.BATTERY_LOW --ez state true
 * @author peng.zhao
 *
 */
public class LockScreenServiceHelper{
	private static final String TAG = "LockScreenService";
	private static final boolean DEBUG = true;
	
	/* Broadcast action */
	public static final String ACT_SCREEN_OFF  			= Intent.ACTION_SCREEN_OFF;
	public static final String ACT_SCREEN_ON   			= Intent.ACTION_SCREEN_ON;
	public static final String ACT_PHONE_STATE 			= "android.intent.action.PHONE_STATE";
	public static final String ACT_NEW_OUTGOING_CALL   	= "android.intent.action.NEW_OUTGOING_CALL";
	
	/* Phone state */
	public static final int MESSAGE_WHAT_CALL_STATE_IDLE     = 0x1000;
	public static final int MESSAGE_WHAT_CALL_STATE_OFFHOOK  = 0x1001;
	public static final int MESSAGE_WHAT_CALL_STATE_RINGING  = 0x1002;
	public static final int MESSAGE_WHAT_CALL_STATE_INCOMING = 0x1003;
	public static final int MESSAGE_WHAT_CALL_STATE_OUTGOING = 0x1004;
	public static final int MESSAGE_WHAT_PREFS_CHANGE        = 0x1005;
	public static final int MESSAGE_WHAT_INVALID        = 0xffffffff;
	
	/* Message  */
	public static final int MESSAGE_WHAT_NONE = 0x0000;
	public static final int MESSAGE_WHAT_DIRECT_UNLOCK = 0x0001;
	public static final int MESSAGE_WHAT_SCREEN_OFF  = 0x0002;
	public static final int MESSAGE_WHAT_SCREEN_ON   = 0x0003;
	public static final int MESSAGE_WHAT_TIMEOUT     = 0x0004;
	public static final int MESSAGE_WHAT_DIRECT_HOME = 0x0005;
	
	/* Message param */
	public static final String MESSAGE_PARAM_PEER_PHONE_NUMBER = "message_param_peer_phone_number";
	
	boolean mIsFirstTime = true;
	
    int mPhoneStat = MESSAGE_WHAT_CALL_STATE_IDLE;
    
    public Context mContext;
    AppSetting appSettig = null; 
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int what = MESSAGE_WHAT_INVALID;
			if(intent.getAction().equals(ACT_SCREEN_OFF))
			{
				what = MESSAGE_WHAT_SCREEN_OFF;
			}
			else if(intent.getAction().equals(ACT_SCREEN_ON))
			{
				what = MESSAGE_WHAT_SCREEN_ON;
			}
			else if(intent.getAction().equals(ACT_PHONE_STATE))
			{
				
			}
			else if(intent.getAction().equals(ACT_NEW_OUTGOING_CALL))
			{
				//如果是去电（拨出）
				what = MESSAGE_WHAT_CALL_STATE_OUTGOING;
			}
			
			/* send self */
			if(MESSAGE_WHAT_INVALID != what)
			{
				Message msg = mHandler.obtainMessage(what);
		        Bundle bundle = new Bundle();
		        msg.setData(bundle);
		        mHandler.sendMessage(msg);
			}
		}
    	
    };
    
    PhoneStateListener mPhoneStateListener=new PhoneStateListener(){
    	 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			//state 当前状态 incomingNumber,貌似没有去电的API
			int what=0;
			super.onCallStateChanged(state, incomingNumber);
			switch(state){
			case TelephonyManager.CALL_STATE_IDLE:
				what = MESSAGE_WHAT_CALL_STATE_IDLE;
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				what = MESSAGE_WHAT_CALL_STATE_OFFHOOK;
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				what = MESSAGE_WHAT_CALL_STATE_RINGING;
				//输出来电号码
				break;
			}
			
			if(what != 0)
			{
				Message msg = mHandler.obtainMessage(what);
		        Bundle bundle = new Bundle();
		        bundle.putString(MESSAGE_PARAM_PEER_PHONE_NUMBER, incomingNumber);
		        msg.setData(bundle);
		        mHandler.sendMessage(msg);
			}
		}
 
	};
	

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			processMessage(msg);
		}
	};
	
	TelephonyManager mTelephonyManager = null;  
	
	boolean mbAutoUnlock = true;
	
	ContentObserver contentObserver = new ContentObserver(new Handler())
	{
		public void onChange(boolean selfChange) {
			appSettig = AppSetting.loadFromLocal(mContext);
        }
	};

	SharedPreferences mDotuisharedPreferences;
	OnSharedPreferenceChangeListener mDoutuiOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener(){

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// TODO Auto-generated method stub
			if(key.equals("adv_phone_id"))
			{
				long adv_phone_id = sharedPreferences.getLong(key, 0);
				if(adv_phone_id > 0 )
				{
					IhuiClientApi.getInstance(mContext).setDotuiScoreAdvPhoneId(adv_phone_id);
				}
			}
			else if(key.equals("score"))
			{
				long score = sharedPreferences.getLong(key, 0);
				if(score > 0 )
				{
					IhuiClientApi.getInstance(mContext).dotuiScoreAddAsync(score);
				}
			}
		}
		
	};
	private boolean bUpdateNotified = false;
	ContentObserver walletContentObserver = new ContentObserver(new Handler())
	{
		public void onChange(boolean selfChange) {
			Wallet wallet = Wallet.loadFromLocal(mContext);
			
			if(null != wallet && wallet.getNotify_credit() > 0)
			{
				cancelNotification(MainApplication.NOTIFICATION_ID_CREDIT);
				showNotification(MainApplication.NOTIFICATION_ID_CREDIT,"收入增加"+SystemUtil.getYuan(wallet.getNotify_credit()) , false);
			}
			if(null != wallet && wallet.getForceMsg() != null && !wallet.getForceMsg().equals(""))
			{
				cancelNotification(MainApplication.NOTIFICATION_ID_CREDIT);
				showNotification(MainApplication.NOTIFICATION_ID_CREDIT,wallet.getForceMsg() , false);
			}
			if((!bUpdateNotified) &&
			   null != wallet && 
			   wallet.getVersion() > UpdateManager.getVersionCode(mContext))
			{
				showNotification(MainApplication.NOTIFICATION_ID_UPDATE,"有新版本啦" , true);
				bUpdateNotified = true;
			}
        }
	};
	
	private ContentObserver userContentObserver = new ContentObserver(new Handler())
    {
    	public void onChange(boolean selfChange) {
            // Do nothing.  Subclass should override.
    		if(User.loadFromLocal(mContext) == null)
    		{
    		}
        }
    };
	
    public LockScreenServiceHelper(Context context)
    {
    	mContext = context;
    	onCreate();
    }
	
    public void onCreate() {
        
    	
    	if(true == mIsFirstTime)
    	{
    		mIsFirstTime = false;
			// register Broadcast
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACT_SCREEN_OFF);    
			filter.addAction(ACT_SCREEN_ON);
	    	filter.addAction(ACT_PHONE_STATE);    
			filter.addAction(ACT_NEW_OUTGOING_CALL);
			mContext.registerReceiver(mBroadcastReceiver, filter);
			
			mTelephonyManager = (TelephonyManager)mContext.getSystemService(Service.TELEPHONY_SERVICE); 
			mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			
			appSettig = AppSetting.loadFromLocal(mContext);
			mContext.getContentResolver().registerContentObserver(Uri.parse(IhuiProvider.SETTING_CONTENT_URIS), true, contentObserver);
			mContext.getContentResolver().registerContentObserver(Uri.parse(IhuiProvider.WALLET_CONTENT_URIS), true, walletContentObserver);
			mContext.getContentResolver().registerContentObserver(Uri.parse(IhuiProvider.USER_CONTENT_URIS), true, userContentObserver);  
			
			mDotuisharedPreferences = mContext.getSharedPreferences("dotuiscore",
					Context.MODE_PRIVATE);
			mDotuisharedPreferences.registerOnSharedPreferenceChangeListener(mDoutuiOnSharedPreferenceChangeListener);
			
			if(DEBUG) Log.i(TAG , "onStartCommand ");
    	}
    	
    }	
	
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(DEBUG) Log.i(TAG , "onDestroy ");
		mDotuisharedPreferences.unregisterOnSharedPreferenceChangeListener(mDoutuiOnSharedPreferenceChangeListener);
		mContext.getContentResolver().unregisterContentObserver(userContentObserver);
		mContext.getContentResolver().unregisterContentObserver(walletContentObserver);
		mContext.getContentResolver().unregisterContentObserver(contentObserver);
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		mContext.unregisterReceiver(mBroadcastReceiver);
		

	}
	
	private void processMessage(Message msg)
	{
		switch (msg.what)
		{
			case MESSAGE_WHAT_SCREEN_OFF:
			/*屏幕关闭*/
			if(MESSAGE_WHAT_CALL_STATE_IDLE == mPhoneStat)
			{ 
				if(null!=appSettig && 0 == appSettig.getEnable_lockscreen())
				{
					
				}
				else
				{
					/* 如果不在通话中，则启动屏保*/
					startLockScreen();
				}
				
				
			}
			break;
			case MESSAGE_WHAT_SCREEN_ON:
			/*屏幕打开*/
			if(MESSAGE_WHAT_CALL_STATE_IDLE == mPhoneStat)
			{
				
			}
			break;
			case MESSAGE_WHAT_CALL_STATE_OUTGOING:

			case MESSAGE_WHAT_CALL_STATE_IDLE:
			
			case MESSAGE_WHAT_CALL_STATE_OFFHOOK:
			
			case MESSAGE_WHAT_CALL_STATE_RINGING:
			/*来电*/
				mPhoneStat = msg.what;
			break;
			
			default:
			break;
		}
	}
	
	private void startLockScreen()
	{
		try{
			if(DEBUG) Log.i(TAG, "start startLockScreen");
			Intent i = new Intent();  
			i.putExtra(LockScreenAdv.INTENT_EXTRA_AUTO_UNLOCK, mbAutoUnlock);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            i.setClass(mContext, LockScreenAdv.class);
           
            mContext.startActivity(i);
            mbAutoUnlock = false;
            if(DEBUG) Log.i(TAG, "end startPhoneLock");
		}catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG,e.toString());
		}
	}

	/**
     * 在状态栏显示通知
     */
    private void showNotification(int id ,String msg,boolean persistent){
        // 创建一个NotificationManager的引用  
        NotificationManager notificationManager = (NotificationManager)   
        		mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);  
          
        // 定义Notification的各种属性  
        Notification notification =new Notification(R.drawable.ihui,  
        		msg, System.currentTimeMillis());
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
        if(!persistent)
        {
        	notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }
        else
        {
        	notification.flags |= Notification.FLAG_ONGOING_EVENT;
        	notification.flags |= Notification.FLAG_NO_CLEAR;
        }
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        //DEFAULT_LIGHTS  使用默认闪光提示
        //DEFAULT_SOUNDS  使用默认提示声音
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_ALL;
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        //notification.ledARGB = Color.BLUE;  
        //notification.ledOnMS =5000; //闪光时间，毫秒
          
        // 设置通知的事件消息  
        CharSequence contentTitle = mContext.getText(R.string.app_name); // 通知栏标题  
        CharSequence contentText =msg; // 通知栏内容  
        Intent notificationIntent =new Intent(mContext, MainApplication.LINKED_CLASS); // 点击该通知后要跳转的Activity  
        PendingIntent contentItent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);  
        notification.setLatestEventInfo(mContext, contentTitle, contentText, contentItent);  
          
        // 把Notification传递给NotificationManager  
        notificationManager.notify(id, notification);  
        
    }
    
    public void cancelNotification(int id)
    {
    	NotificationManager notificationManager = (NotificationManager)   
    			mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);  
    	notificationManager.cancel(id);
    }
    
}
