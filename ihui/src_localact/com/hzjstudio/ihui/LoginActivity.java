package com.hzjstudio.ihui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zpstudio.R;
import com.zpstudio.app.MainApplication;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnLogin;

public class LoginActivity extends Activity implements OnClickListener {
	private Button btn_zc;
	private TextView textView_mima;
	private EditText et_name;
	private EditText et_passwd;
	private Button btn_dl;
	public static String KEY_PHONE_NUMBER = "phone_number";
	public static String KEY_PASSWORE = "password";
	
	Activity mSelf = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_activity_login);
		init();
		listen();

	}

	private void init() {
		mSelf = this;
		btn_zc = (Button) findViewById(R.id.btn_zc);
		textView_mima = (TextView) findViewById(R.id.textview_mima);
		et_name = (EditText) findViewById(R.id.et_name);
		et_passwd = (EditText) findViewById(R.id.et_passwd);
		btn_dl = (Button) findViewById(R.id.btn_dl);
		// btn_dl.setEnabled(false);
	}

	private void listen() {
		btn_dl.setOnClickListener(this);
		btn_zc.setOnClickListener(this);
		textView_mima.setOnClickListener(this);
	}
	void login(final String phone_number ,final  String password)
	{
		if(phone_number.trim().equals("") || password.trim().equals(""))
		{
			return;
		}
		
		IhuiClientApi.getInstance(this).loginAsync(phone_number, password, new ListenerOnLogin(){

			@Override
			public void onSuccess(User user, Wallet wallet) {
				// TODO Auto-generated method stub
				initData();
				Intent intent = new Intent(LoginActivity.this,
						ActivityTabFragment.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(mSelf, msg, Toast.LENGTH_LONG).show();
			}});
		
	}
	
	
	
	private void initData() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(this).refreshHangOnAdvAsync();
	}
	
	
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_dl:
			login(et_name.getText().toString() , et_passwd.getText().toString());
			break;

		case R.id.btn_zc:
			startActivity(new Intent(LoginActivity.this, ActivityZhuCe.class));
			break;

		case R.id.textview_mima:
			startActivity(new Intent(LoginActivity.this, ActivityMiMa.class));
			break;

		default:
			break;
		}
	}
}
