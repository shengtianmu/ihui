package com.zpstudio.ui.adv;

import android.app.ActivityManager;
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

public class DaemonService extends Service {
	private static final String TAG = "DaemonService";
	private static final boolean DEBUG = true;
	public static final String ACT_SERVICE_KILLED = "com.zpstudio.service.killed";
	boolean mIsFirstTime = true;
	
	private static final String HOST = "localhost";
    Context mContext = null;
    
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(ACT_SERVICE_KILLED))
			{
				context.startService(new Intent(context,LockScreenService.class));  
			}
			
		}
    	
    };
    
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if(DEBUG) Log.i(TAG , "onCreate");
	}
	
    public int onStartCommand(Intent intent, int flags, int startId) {
        
    	
    	if(true == mIsFirstTime)
    	{
			mIsFirstTime = false;
			// register Broadcast
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACT_SERVICE_KILLED);
			this.registerReceiver(mBroadcastReceiver, filter);
						
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
			
			mContext = this;
			
			
    	}
    	
    	/* ensure to recover after killed */
    	flags =  START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }	
	
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(DEBUG) Log.i(TAG , "onDestroy ");
		
		sendBroadcast(new Intent(LockScreenService.ACT_DAEMON_SERVICE_KILLED));
		
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