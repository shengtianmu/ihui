package com.zpstudio.ui.adv;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.zpstudio.R;
import com.zpstudio.app.MainApplication;
import com.zpstudio.util.Log;
import com.zpstudio.util.Watcher;

/**
 * adb shell am broadcast -a android.intent.action.BATTERY_LOW --ez state true
 * @author peng.zhao
 *
 */
public class LockScreenService extends Service {
	private static final String TAG = "LockScreenService";
	private static final boolean DEBUG = true;
	
	public static final String ACT_DAEMON_SERVICE_KILLED = "com.zpstudio.daemonservice.killed";
	boolean mIsFirstTime = true;
	
    public static final int GARD_PORT = 9998;
    Context mContext = null;
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(ACT_DAEMON_SERVICE_KILLED))
			{
				context.startService(new Intent(context,DaemonService.class));  
			}
			
		}
    	
    };
    
    public LockScreenServiceHelper mLockScreenServiceHelper;
    
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if(DEBUG) Log.i(TAG , "onCreate");
		
	}
	
    public int onStartCommand(Intent intent, int flags, int startId) {
        
    	
    	if(true == mIsFirstTime)
    	{
			mIsFirstTime = false;
			
			startService(new Intent(this,DaemonService.class));  
			
			// register Broadcast
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACT_DAEMON_SERVICE_KILLED);
			registerReceiver(mBroadcastReceiver, filter);
						
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//					.setSmallIcon(R.drawable.ihui)
//					.setContentText(getText(R.string.ongoing))
//					.setContentTitle(getText(R.string.app_name))
//					.setAutoCancel(false)
//					.setOngoing(true)
//					.setTicker(getText(R.string.app_name))
//					.setDefaults(Notification.DEFAULT_ALL)
//					.setOnlyAlertOnce(true);
//
//			Intent notificationIntent = new Intent(this , MainApplication.LINKED_CLASS);
//			
//			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			
//			builder.setContentIntent(contentIntent);
//			
//			startForeground(MainApplication.NOTIFICATION_ID_STATUS, builder.build());
			
			mLockScreenServiceHelper = new LockScreenServiceHelper(this);
			
			mContext = this;
			
			Log.e("LockScreenService.class.getName()", LockScreenService.class.getName());
			new Watcher(this).createAppMonitor(LockScreenService.class.getName());
    	}
    	
    	/* ensure to recover after killed */
    	flags =  START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }	
	
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(DEBUG) Log.i(TAG , "onDestroy ");
		mLockScreenServiceHelper.onDestroy();
	    sendBroadcast(new Intent(DaemonService.ACT_SERVICE_KILLED));
		stopForeground(true);
		unregisterReceiver(mBroadcastReceiver);
		
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}