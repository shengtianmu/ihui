package com.hzjstudio.ihui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class InstalledReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals("android.intent.action.PACKAGE_ADDED")) { // install
			String packageName = intent.getDataString();

			Toast.makeText(context, "安装了 :" + packageName,
					Toast.LENGTH_LONG).show();
		}

		if (intent.getAction().equals(
				"android.intent.action.PACKAGE_REMOVED")) { // uninstall
			String packageName = intent.getDataString();

			Toast.makeText(context, "卸载了 :" + packageName,
					Toast.LENGTH_LONG).show();
		}

	}
}
