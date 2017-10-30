package com.hzjstudio.ihui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpstudio.R;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnUpdateUserAndWallet;

public class UserXinXi extends Activity implements OnClickListener {
	private TextView tv_fh;
	private TextView tv_name;
	private TextView tv_phone;
	private TextView tv_sex;
	private TextView tv_age;
	private TextView tv_passwd;
	private TextView tv_city;
	private RelativeLayout rel_update;

	User mUser = null;
	Wallet mWallet = null;
	
	ListenerOnUpdateUserAndWallet listener = new ListenerOnUpdateUserAndWallet(){

		@Override
		public void onSuccess(User user, Wallet wallet) {
			// TODO Auto-generated method stub
			mUser = user;
			mWallet = wallet;
			updateView();
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_userfragment4);
		
		init();
		updateView();
		IhuiClientApi.getInstance(this).registerListener(listener);
	}

	/**
	 * 将数据显示到控件上
	 */
	private void updateView() {

		tv_name.setText(mUser.getName());
		tv_phone.setText(mUser.getDisplayPhoneNumber());
		String sex = "";
		sex = mUser.getSex();
		if (sex.equals("boy"))
			sex = "男";
		else
			sex = "女";
		tv_sex.setText(sex);
		tv_age.setText(mUser.getAge());
		tv_passwd.setText(mUser.getPassword());
		tv_city.setText(mUser.getCity());
	}

	private void init() {
		mUser = IhuiClientApi.getInstance(this).getUser();
		mWallet = IhuiClientApi.getInstance(this).getWallet();
		
		tv_fh = (TextView) findViewById(R.id.tv_fh);

		tv_name = (TextView) findViewById(R.id.tv_name);

		tv_phone = (TextView) findViewById(R.id.tv_phone);

		tv_sex = (TextView) findViewById(R.id.tv_sex);

		tv_age = (TextView) findViewById(R.id.tv_age);

		tv_passwd = (TextView) findViewById(R.id.tv_passwd);

		tv_city = (TextView) findViewById(R.id.tv_city);

		rel_update = (RelativeLayout) findViewById(R.id.rel_update);

		tv_fh.setOnClickListener(this);
		rel_update.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_fh:
			finish();
			break;

		case R.id.rel_update:
			Intent intent = new Intent(UserXinXi.this, UserUpdate.class);
			startActivity(intent);
		default:
			break;
		}
	}
	public void onDestroy() {
		super.onDestroy();
		IhuiClientApi.getInstance(this).unregisterListener(listener);
	}
}
