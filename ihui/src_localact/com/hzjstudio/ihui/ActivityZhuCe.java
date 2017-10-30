package com.hzjstudio.ihui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hzjstudio.ihui.view.FuWu;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class ActivityZhuCe extends Activity implements OnClickListener {
	static final int REQUEST_CODE_STEP_2 = 2;
	private TextView textview1_fh;
	private EditText et_newname;
	private EditText et_yanzhengma;
	private Button btn1_yanzhengma;
	private Button btn1_xyb;
	private TextView tv_textview;
	private CountDownTimer mCdt = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_item_zc);

		init();
		listen();
	}

	private void init() {
		textview1_fh = (TextView) findViewById(R.id.textview1_fh);
		et_newname = (EditText) findViewById(R.id.et_newname);
		et_yanzhengma = (EditText) findViewById(R.id.et_yanzhengma);
		btn1_yanzhengma = (Button) findViewById(R.id.btn1_yanzhengma);
		btn1_xyb = (Button) findViewById(R.id.btn1_xyb);
		tv_textview = (TextView) findViewById(R.id.tv_textview);

		mCdt = new CountDownTimer(60000, 1000) {

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

	private void listen() {
		textview1_fh.setOnClickListener(this);
		btn1_yanzhengma.setOnClickListener(this);
		btn1_xyb.setOnClickListener(this);
		tv_textview.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textview1_fh:
			finish();
			break;

		case R.id.tv_textview:
			startActivity(new Intent(ActivityZhuCe.this, FuWu.class));
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
				Intent intent = new Intent(ActivityZhuCe.this, UserZhuCe.class);
				intent.putExtra("phone_number", phone_number);
				intent.putExtra("dynamic_pswd", yanzhengma);
				ActivityZhuCe.this.startActivityForResult(intent,
						REQUEST_CODE_STEP_2);
			}
		}
			break;

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE_STEP_2) {
			if (resultCode == Activity.RESULT_OK) {
				finish();
			} else {

			}
		}
	}

	void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
