package com.zpstudio.datacenter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zpstudio.contentprovider.IhuiProvider;

public class AppSetting {
	int enable_lockscreen;
	String  new_version;
	/**
	 * @return the enable_lockscreen
	 */
	public int getEnable_lockscreen() {
		return enable_lockscreen;
	}
	/**
	 * @param enable_lockscreen the enable_lockscreen to set
	 */
	public void setEnable_lockscreen(int enable_lockscreen) {
		this.enable_lockscreen = enable_lockscreen;
	}
	
	public static void saveToLocal(Context context , AppSetting appSetting)
	{
		//Delete all first
		if(null != appSetting)
		{
		
			ContentValues values = new ContentValues();
			values.put(IhuiProvider.SETTING_TABLE_ENABLE_LOCKSCREEN , appSetting.getEnable_lockscreen());
			context.getContentResolver().delete(Uri.parse(IhuiProvider.SETTING_CONTENT_URIS), 
					null, null);
			context.getContentResolver().insert(Uri.parse(IhuiProvider.SETTING_CONTENT_URIS), values);
		}
		context.getContentResolver().notifyChange(Uri.parse(IhuiProvider.SETTING_CONTENT_URIS), null);
	}	
	
	public static AppSetting loadFromLocal(Context context)
	{
		AppSetting appSetting = null;
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse(IhuiProvider.SETTING_CONTENT_URIS),
						null,
						null,
						null, 
						null);
		if(cursor != null){   
            if(cursor.moveToNext()){   
            	appSetting = new AppSetting();
            	appSetting.setEnable_lockscreen(cursor.getInt(cursor.getColumnIndex(IhuiProvider.SETTING_TABLE_ENABLE_LOCKSCREEN)));
            	
            }  
        }   
		
		if(null != cursor)
		{
			cursor.close();
		}
		return appSetting;
	}
}
