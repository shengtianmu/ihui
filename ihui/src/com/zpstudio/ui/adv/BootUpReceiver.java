package com.zpstudio.ui.adv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zpstudio.datacenter.db.AppSetting;
import com.zpstudio.datacenter.db.User;

public class BootUpReceiver extends BroadcastReceiver{
	static final String TAG = "BootUpReceiver";
	@Override
    public void onReceive(Context context, Intent intent) {
        //if (  )
        //	||Intent.ACTION_USER_PRESENT.equals(intent.getAction())) 
		if(intent.getAction() != null)
        {
            //Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
            /*
            // 启动完成  
        	Intent i = new Intent(context, AlarmReceiver.class);  
        	i.setAction(AlarmReceiver.INTENT_ACTION_DEAMON_ALARM);  
        	PendingIntent sender = PendingIntent.getBroadcast(context, 0,  i, 0);  
        	long firstime = SystemClock.elapsedRealtime();  
        	AlarmManager am = (AlarmManager) context  .getSystemService(Context.ALARM_SERVICE);  
        	// 10秒一个周期，不停的发送广播 
        	//am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,sender);
        	am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,  10 * 1000, sender);
        	*/
        	// 启动service   
        	// 多次调用startService并不会启动多个service 而是会多次调用onStart  
        	AppSetting appSetting = AppSetting.loadFromLocal(context);
        	User user = User.loadFromLocal(context);
        	if(   (user != null)
        		&&(appSetting != null)
        		&&(0 != appSetting.getEnable_lockscreen()))
        	{
        		context.startService(new Intent(context,LockScreenService.class));  
        	}
            return;
        }
    }


}
