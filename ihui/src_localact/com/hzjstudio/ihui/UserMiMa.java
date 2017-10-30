package com.hzjstudio.ihui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnPasswordClaim;
import com.zpstudio.ui.util.DialogUtil;

public class UserMiMa extends Activity implements OnClickListener {
	private TextView tv2_fh;
	private EditText et_mima1;
	private EditText et_mima2;
	private Button btn_tijiao;

	Dialog mLoadingDlg = null;
	Activity mSelf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_item_newmima);

		init();
		listen();

	}

	private void init() {
		mSelf = this;
		setResult(Activity.RESULT_CANCELED);
		tv2_fh = (TextView) findViewById(R.id.tv2_fh);
		et_mima1 = (EditText) findViewById(R.id.et_mima1);
		et_mima2 = (EditText) findViewById(R.id.et_mima2);
		btn_tijiao = (Button) findViewById(R.id.btn_tijiao);
	}

	private void listen() {
		tv2_fh.setOnClickListener(this);
		btn_tijiao.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv2_fh:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;

		case R.id.btn_tijiao: {
			boolean bValidated = true;
			if (TextUtils.isEmpty(et_mima1.getText().toString())) {
				showToast(getString(R.string.shurumima));
				bValidated = false;
			} else if (TextUtils.isEmpty(et_mima2.getText().toString())) {
				showToast(getString(R.string.qingzaicishurumima));
				bValidated = false;
			} else if (!(et_mima1.getText().toString().equals(et_mima2
					.getText().toString()))) {
				showToast(getString(R.string.liangcimimabuyiyang));
				bValidated = false;
			}

			if (bValidated) {
				showLoadingDlg();
				btn_tijiao.setClickable(false);
				Intent intent = getIntent();
				final String phone_number = intent
						.getStringExtra("phone_number");
				final String dynamic_pswd = intent
						.getStringExtra("dynamic_pswd");
				final String password = et_mima2.getText().toString();
				IhuiClientApi.getInstance(this).passwordClaimAsync(
						phone_number, password, dynamic_pswd,
						new ListenerOnPasswordClaim() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					setResult(Activity.RESULT_OK);
					finish();
				}

							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								Toast.makeText(mSelf, msg, Toast.LENGTH_LONG)
										.show();
							}

						});

			}
			break;
		}
		default:
			break;
		}
	}

	private void showLoadingDlg() {
		if (null == mLoadingDlg) {
			mLoadingDlg = DialogUtil.createLoadingDialog(this, "");
		}
		mLoadingDlg.show();
	}

	void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
