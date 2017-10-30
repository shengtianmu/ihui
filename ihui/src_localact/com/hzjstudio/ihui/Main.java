package com.hzjstudio.ihui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.zpstudio.R;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnLogin;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.com_hzjstudio__ihui_main);

		User user = IhuiClientApi.getInstance(this).getUser();
		// 判断程序与第几次运行，如果是第一次运行则跳转到引导页面
		if (null == user) {
			IhuiClientApi.getInstance(this).DoAnonymouslogin(new ListenerOnLogin(){

				@Override
				public void onSuccess(User user, Wallet wallet) {
					// TODO Auto-generated method stub
					initData();
					Intent intent = new Intent(Main.this,
							ActivityTabFragment.class);
					startActivity(intent);
					finish();
				}

				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
					Toast.makeText(Main.this, msg, Toast.LENGTH_LONG).show();
				}});
			
//			final Intent localIntent = new Intent(this, LoginActivity.class);
//			startActivity(localIntent);
//			Timer timer = new Timer();
//			TimerTask tast = new TimerTask() {
//				@Override
//				public void run() {
//					startActivity(localIntent);
//					finish();
//				}
//			};
//			timer.schedule(tast, 1000);
//			try {
//				this.clone();
//			} catch (CloneNotSupportedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} else {
			final Intent localIntent = new Intent(this, ActivityTabFragment.class);
			Timer timer = new Timer();
			TimerTask tast = new TimerTask() {
				@Override
				public void run() {
					startActivity(localIntent);
					finish();
				}
			};
			timer.schedule(tast, 1000);
		}
		
		/*
		Editor editor = preferences.edit();
		// 存入数据
		editor.putInt("ps", ++count);
		// 提交修改
		editor.commit();
		*/

	}
	
	private void initData() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(this).refreshHangOnAdvAsync();
	}
}
