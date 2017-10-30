package com.zpstudio.ui.adv;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.app.LocalActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zpstudio.R;
import com.zpstudio.contentprovider.IhuiProvider;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.ui.adv.fragment.FragAdapter;
import com.zpstudio.ui.adv.fragment.Fragment1;
import com.zpstudio.ui.adv.fragment.Fragment2;
import com.zpstudio.ui.adv.fragment.FragmentGameDownload;
import com.zpstudio.util.LockLayer;
import com.zpstudio.util.SystemUtil;

import ethan.custom.verticalscrolllayout.VerticalScrollLayout;

public class LockScreenAdv extends FragmentActivity implements OnKeyguardExitResult{
	private static final String TAG = "LockSceenAdv";
	private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	
	public static final String INTENT_EXTRA_AUTO_UNLOCK = "auto_unlock";
	public static final int MSG_ON_UNLOCK_LEFT = 1;
	public static final int MSG_ON_UNLOCK_RIGHT = 2;
	/* Broadcast action */
	public static final String ACT_SCREEN_OFF  			= Intent.ACTION_SCREEN_OFF;
	public static final String ACT_SCREEN_ON   			= Intent.ACTION_SCREEN_ON;
	
	VerticalScrollLayout mRoot         = null;
	ImageView            mAdvImageView = null;
	User       mUser     = null;
	Adv        mAdv      = null;
	StringBuffer sbLog   = new StringBuffer();
	int debugModeCount = 0;
	ContentObserver advContentObserver = new ContentObserver(new Handler())
    {
    	public void onChange(boolean selfChange) {
    		//updateView();
        }
    };
    
    PowerManager mPowerManager = null;  
	KeyguardManager mKeyguardManager = null;
	KeyguardLock mKeyguardLock = null;
	
	LockLayer mLockLayer = null;
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACT_SCREEN_OFF))
			{
				
			}
			else if(intent.getAction().equals(ACT_SCREEN_ON))
			{
				SystemUtil.triggerReturn();
			}
		}
    };
    
    TelephonyManager mTelephonyManager = null;  
    PhoneStateListener mPhoneStateListener=new PhoneStateListener(){
   	 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch(state){
			
			case TelephonyManager.CALL_STATE_RINGING:
				//来电退出
				exit(true);
			break;
			}
		}
    };
	private ViewPager vp;
	private LocalActivityManager manager;
    Activity mSelf = this;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//check permission
		println(Build.MANUFACTURER);
		boolean bPopupPermitted = checkPermission();
		
		/*Wake up screen */
		/*2.2.3 系统的老方法*/
		mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);  
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");  
        mKeyguardLock.disableKeyguard();
        
        
        /* 4.0.x屏蔽home键,不好使 */
        //getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int contentViewId = R.layout.com_zpstudio_lock_screen_adv_flip;
        if(!bPopupPermitted)
        {
			/*replace system lock view with ours ,setting FLAG_SHOW_WHEN_LOCKED and FLAG_DISMISS_KEYGUARD */
	        /* 放到LockLayer里了*/
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); 
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
	        
	        setContentView(contentViewId);
	        vp = (ViewPager)findViewById(R.id.vp_root);
	        mRoot     = (VerticalScrollLayout) findViewById(R.id.root);
        }
        else
        {
        	//屏蔽HOME键，替换setContentView(R.layout.com_zpstudio_lock_screen_adv);
//            View lockView = View.inflate(this, contentViewId, null);  
//            mLockLayer = LockLayer.getInstance(this);  
//            mLockLayer.setLockView(lockView);  
//            mLockLayer.lock();  
//            vp = (ViewPager)lockView.findViewById(R.id.vp_root);
//            mRoot     = (VerticalScrollLayout) lockView.findViewById(R.id.root);
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); 
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
	        
	        setContentView(contentViewId);
	        vp = (ViewPager)findViewById(R.id.vp_root);
	        mRoot     = (VerticalScrollLayout) findViewById(R.id.root);
        }
        
        initView();
		// reg
        getContentResolver().registerContentObserver(Uri.parse(IhuiProvider.ADV_CONTENT_URIS), true, advContentObserver);
        
        // listen phone
        mTelephonyManager = (TelephonyManager)getSystemService(Service.TELEPHONY_SERVICE); 
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		// register Broadcast
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACT_SCREEN_OFF);    
		filter.addAction(ACT_SCREEN_ON);
		registerReceiver(mBroadcastReceiver, filter);
		
	}
	
	
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_ON_UNLOCK_RIGHT:
					if(null != msg.obj)
					{
						Adv adv = (Adv)msg.obj;
						IhuiClientApi.getInstance(mSelf).updateWalletAsync(adv.getAdv_phone_id() , IhuiClientApi.ADV_ACTION_TYPE_UNLOCK);
					}
					exit(true);
					break;
				case MSG_ON_UNLOCK_LEFT:
					if(null != msg.obj)
					{
						Adv adv = (Adv)msg.obj;
						String link = Config.getFullUrl(mSelf, adv.getLink());
						link = IhuiClientApi.getInstance(mSelf).addLinkParameter(link, "adv_phone_id",String.valueOf(adv.getAdv_phone_id()));
						IhuiClientApi.getInstance(mSelf).redirect(link);
						IhuiClientApi.getInstance(mSelf).updateWalletAsync(adv.getAdv_phone_id() , IhuiClientApi.ADV_ACTION_TYPE_ATTENTION);
					}
					exit(false);
					break;
				case 3:
					Log.e("11", "advs:1");
					String link = Config.getFullUrl(mSelf, "");
					link = IhuiClientApi.getInstance(mSelf).addLinkParameter(link, "adv_phone_id",String.valueOf(0));
					IhuiClientApi.getInstance(mSelf).redirect(link);
					exit(false);
					break;
				default:
					break;
			}
			
		};
	};
	
	private void initView() {
		// TODO Auto-generated method stub
		
		
		List<Fragment> fragments=new ArrayList<Fragment>();
//		fragments.add(new Fragment1());
		fragments.add(new Fragment2());
//		fragments.add(new FragmentGameDownload()); 
		FragAdapter adapter = new FragAdapter(this.getSupportFragmentManager(), fragments);
        vp.setAdapter(adapter);
//        vp.setCurrentItem(1);
//        vp.setOffscreenPageLimit(3);
		

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
	
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		// 4.0以前的版本disable HOME键的方法，但4.0.x会报错
		// getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);     
	    super.onAttachedToWindow(); 	
	}
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		getContentResolver().unregisterContentObserver(advContentObserver);
        
	}
	
	public void exit(boolean needMoveToback)
	{
		if(null != mKeyguardManager)
		{
			mKeyguardManager.exitKeyguardSecurely(this);
		}
		if(null != mLockLayer) 
		{
			mLockLayer.unlock();
			mLockLayer = null;
		}
		if(needMoveToback)
		{
			moveTaskToBack(true);
		}
		finish();
	}
	public void onKeyguardExitResult(boolean success) {
		// TODO Auto-generated method stub
		mKeyguardLock.reenableKeyguard();
	}
	
	
	private void println(String msg)
	{
		if(null != sbLog)
		{
			sbLog.append(msg + "\n\r");
		}
	}
	
	private boolean checkPermission()
	{
		return !android.os.Build.MANUFACTURER.equalsIgnoreCase("xiaomi");
	}
	public void doChange(int lastIndex, int currentIndex) {
		
	}

	public Handler getHandler() {
		// TODO Auto-generated method stub
		return mHandler;
	}
	
}
