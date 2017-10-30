package com.hzjstudio.ihui;

import android.app.Activity;
import android.app.Dialog;
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

import com.zpstudio.R;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class ActivityMiMa extends Activity implements OnClickListener {
	static final int REQUEST_CODE_STEP_2 = 2;
	private TextView textview2_fh;
	private EditText et_oldname;
	private EditText et_oldpasswd;
	private Button btn2_yanzhengma;
	private Button btn2_xyb;

	Dialog mLoadingDlg = null;
	private CountDownTimer mCdt = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_item_mima);

		init();
		linsten();
	}

	private void init() {
		textview2_fh = (TextView) findViewById(R.id.textview2_fh);
		et_oldname = (EditText) findViewById(R.id.et_oldname);
		et_oldpasswd = (EditText) findViewById(R.id.et_oldpasswd);
		btn2_yanzhengma = (Button) findViewById(R.id.btn2_yanzhengma);
		btn2_xyb = (Button) findViewById(R.id.btn2_xyb);

		mCdt = new CountDownTimer(60000, 1000) {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				btn2_yanzhengma.setText(getString(R.string.huoquyanzhengma));
				btn2_yanzhengma.setClickable(true);
				et_oldname.setEnabled(true);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				btn2_yanzhengma.setText(String
						.valueOf(millisUntilFinished / 1000)
						+ getString(R.string.second));
			}
		};
	}

	private void linsten() {
		textview2_fh.setOnClickListener(this);
		btn2_yanzhengma.setOnClickListener(this);
		btn2_xyb.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textview2_fh:
			finish();
			break;

		case R.id.btn2_yanzhengma: {
			String phone_number = et_oldname.getText().toString();
			int numcode = (int) ((Math.random() * 9 + 1) * 100000);
			String smstext = String.valueOf(numcode);
			boolean bValidated = true;
			if (TextUtils.isEmpty(phone_number)) {
				Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
				bValidated = false;
			}
			if (bValidated) {
				IhuiClientApi.getInstance(this).smsValidateAsync(phone_number,
						smstext);
				et_oldpasswd.setTag(smstext);
				mCdt.start();
				btn2_yanzhengma.setClickable(false);
				et_oldname.setEnabled(false);
			}
		}
			break;

		case R.id.btn2_xyb: {
			String phone_number = et_oldname.getText().toString();
			String yanzhengma = et_oldpasswd.getText().toString();
			String smstext = (String) et_oldpasswd.getTag();
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

				Intent intent = new Intent(ActivityMiMa.this, UserMiMa.class);
				intent.putExtra("phone_number", et_oldname.getText().toString());
				intent.putExtra("dynamic_pswd", et_oldpasswd.getText()
						.toString());
				startActivityForResult(intent, REQUEST_CODE_STEP_2);
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

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
