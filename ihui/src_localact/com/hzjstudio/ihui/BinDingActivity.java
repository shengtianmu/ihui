package com.hzjstudio.ihui;


import com.hzjstudio.ihui.view.FuWu;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BinDingActivity extends Activity implements OnClickListener{
	
	private TextView textview1_fh;
	private EditText et_newname;
	private EditText et_yanzhengma;
	private Button btn1_yanzhengma;
	private Button btn1_xyb;
	private TextView tv_textview;
	private CountDownTimer mCdt=null;
	private EditText et_yjm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.binding_activity);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		textview1_fh = (TextView) findViewById(R.id.textview1_fh);
		et_newname = (EditText) findViewById(R.id.et_newname);
		et_yjm = (EditText) findViewById(R.id.et_yqm);
		et_yanzhengma = (EditText) findViewById(R.id.et_yanzhengma);
		btn1_yanzhengma = (Button) findViewById(R.id.btn1_yanzhengma);
		btn1_xyb = (Button) findViewById(R.id.btn1_xyb);
		tv_textview = (TextView) findViewById(R.id.tv_textview);
		textview1_fh.setOnClickListener(this);
		btn1_yanzhengma.setOnClickListener(this);
		btn1_xyb.setOnClickListener(this);
		tv_textview.setOnClickListener(this);
		
		mCdt= new CountDownTimer(60000, 1000) {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				btn1_yanzhengma.setText(getString(R.string.huoquyanzhengma));
				btn1_yanzhengma.setClickable(true);
				et_newname.setEnabled(true);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				btn1_yanzhengma.setText(String
						.valueOf(millisUntilFinished / 1000)
						+ getString(R.string.second));
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textview1_fh:
			finish();
			break;

		case R.id.tv_textview:
			startActivity(new Intent(BinDingActivity.this, FuWu.class));
			break;

		case R.id.btn1_yanzhengma: {
			String phone_number = et_newname.getText().toString();
			int numcode = (int) ((Math.random() * 9 + 1) * 100000);
			String smstext = String.valueOf(numcode);
			boolean bValidated = true;
			if (TextUtils.isEmpty(phone_number)) {
				showToast("请输入手机号码");
				bValidated = false;
			}
			if (bValidated) {
				Log.e("111", smstext+"");
				
				IhuiClientApi.getInstance(this).smsValidateAsync(phone_number,
						smstext);
				et_yanzhengma.setTag(smstext);
				mCdt.start();
				btn1_yanzhengma.setClickable(false);
				et_newname.setEnabled(false);
			}
		}
			break;

		case R.id.btn1_xyb: {
			String phone_number = et_newname.getText().toString();
			String yanzhengma = et_yanzhengma.getText().toString();
			String smstext = (String) et_yanzhengma.getTag();
			boolean bValidated = true;
			if (TextUtils.isEmpty(phone_number)) {
				showToast("请输入手机号码");
				bValidated = false;
			} else if (TextUtils.isEmpty(yanzhengma)) {
				showToast("请输入验证码");
				bValidated = false;
			} else if (!yanzhengma.equals(smstext)) {
				showToast("验证码错误");
				bValidated = false;
			}
			if (bValidated) {
				IhuiClientApi.getInstance(this).DoBindphonenumber(phone_number,et_yjm.getText().toString(), new ListenerOnLogin(){

					@Override
					public void onSuccess(User user, Wallet wallet) {
						// TODO Auto-generated method stub
						initData();
						Intent intent = new Intent(BinDingActivity.this,
								ActivityTabFragment.class);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						showToast(msg);
					}});
			}
		}
			break;

		default:
			break;
		}
	}
	
	void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(this).refreshHangOnAdvAsync();
	}
}
