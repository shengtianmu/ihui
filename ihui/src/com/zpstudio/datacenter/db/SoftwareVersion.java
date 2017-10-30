package com.zpstudio.datacenter.db;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.zpstudio.util.UpdateManager;

public class SoftwareVersion {
	public static void softwareUpdate(Context context )
	{
		String ip = Config.getServerName(context);
		String port = Config.getPort(context);
		UpdateManager manager = new UpdateManager(context, ip, port);
		// 检查软件更新
		manager.checkUpdate();
	}
	
	public static String getVersion(Context context)//获取版本号
	{
		try {
			PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0";
		}
	}
	
	public static int getVersionCode(Context context)//获取版本号(内部识别号)
	{
		try {
			PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

}
