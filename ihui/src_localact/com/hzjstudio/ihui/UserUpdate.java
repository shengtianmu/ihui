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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnUpdateUserAndWallet;
import com.zpstudio.ui.util.AddressPickerAct;
import com.zpstudio.ui.util.DialogUtil;
import com.zpstudio.ui.util.NumberPickerAct;
import com.zpstudio.ui.util.AddressPickerAct.Address;
import com.zpstudio.ui.util.AddressPickerAct.AddressConfig;
import com.zpstudio.ui.util.AddressPickerAct.SingleAddress;

public class UserUpdate extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	static final int REQUEST_CODE_NUMBER_PICKER = 1;
	static final int REQUEST_CODE_ADDRESS_PICKER = 2;
	private TextView textview_fanhui;
	private Button btn1_tijiao;
	private EditText et_mingzi1;
	private RadioGroup rg_btn1;
	private LinearLayout ll_age1;
	private TextView et_age1;
	private LinearLayout ll_dizhi1;
	private TextView ll_dizhi_tips_1_1;
	private TextView ll_dizhi_tips_2_2;
	private TextView ll_dizhi_city1;

	String phone_number;
	String dynamic_pswd;
	String sex = null;
	// private String display_resolution;
	// private String system_version;
	private Activity mSelf;
	SingleAddress selectedAddress = null;
	Gson gson = new Gson();
	Dialog mLoadingDlg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_userupdate);
		init();

	}

	private void init() {
		mSelf = this;
		// TODO Auto-generated method stub
		textview_fanhui = (TextView) findViewById(R.id.textview_fanhui);
		btn1_tijiao = (Button) findViewById(R.id.btn1_tijiao);
		rg_btn1 = (RadioGroup) findViewById(R.id.rg_btn1);
		et_mingzi1 = (EditText) findViewById(R.id.et_mingzi1);
		ll_age1 = (LinearLayout) findViewById(R.id.ll_age1);
		et_age1 = (TextView) findViewById(R.id.et_age1);
		ll_dizhi1 = (LinearLayout) findViewById(R.id.ll_dizhi1);
		ll_dizhi_tips_1_1 = (TextView) findViewById(R.id.ll_dizhi_tips_1_1);
		ll_dizhi_tips_2_2 = (TextView) findViewById(R.id.ll_dizhi_tips_2_2);
		ll_dizhi_city1 = (TextView) findViewById(R.id.ll_dizhi_city1);

		textview_fanhui.setOnClickListener(this);
		btn1_tijiao.setOnClickListener(this);
		ll_age1.setOnClickListener(this);
		ll_dizhi1.setOnClickListener(this);
		rg_btn1.setOnCheckedChangeListener(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE_NUMBER_PICKER) {
			if (resultCode == Activity.RESULT_OK) {
				int age = intent.getExtras().getInt(
						NumberPickerAct.RESULT_NUMBER);
				et_age1.setText(String.valueOf(age));
			}

		} else if (requestCode == REQUEST_CODE_ADDRESS_PICKER) {
			if (resultCode == Activity.RESULT_OK) {
				String json = intent.getExtras().getString(
						AddressPickerAct.EXTRA_RESULT_SELECTED);
				selectedAddress = gson.fromJson(json, SingleAddress.class);
				ll_dizhi_tips_1_1.setVisibility(View.GONE);
				ll_dizhi_tips_2_2.setVisibility(View.GONE);
				ll_dizhi_city1.setVisibility(View.VISIBLE);
				ll_dizhi_city1.setText(selectedAddress.city);
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textview_fanhui:
			finish();
			break;

		case R.id.ll_age1:
			startActivityForResult(new Intent(this, NumberPickerAct.class),
					REQUEST_CODE_NUMBER_PICKER);
			break;

		case R.id.ll_dizhi1:
			showLoadingDlg();
			IhuiClientApi.getInstance(this).getAgentListAsync(
					new IhuiClientApi.ListenerOnAgentList() {

						@Override
						public void onSuccess(Address agentAddress) {
							// TODO Auto-generated method stub

							AddressConfig config = new AddressConfig();
							config.addressTree = agentAddress;
							AddressPickerAct.addressPick(mSelf,
									REQUEST_CODE_ADDRESS_PICKER,
									new Gson().toJson(config));
							dismissLoadingDlg();
						}

						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
							dismissLoadingDlg();
						}
					});
			break;

		case R.id.btn1_tijiao: {
			boolean bValidated = true;
			if (TextUtils.isEmpty(sex)) {
				showToast("请选择性别");
				bValidated = false;
			} else if (TextUtils.isEmpty(et_age1.getText().toString())) {
				showToast("请选择年龄");
				bValidated = false;
			} else if (TextUtils.isEmpty(et_mingzi1.getText().toString())) {
				showToast("请输入姓名");
				bValidated = false;
			} 
			
			
//			else if (null == selectedAddress) {
//				showToast("请选择城市");
//				bValidated = false;
//			}

			if (bValidated) {
				showLoadingDlg();
				btn1_tijiao.setClickable(false);
				IhuiClientApi.getInstance(this).modifyUserInfoAsync(
						et_mingzi1.getText().toString(),
						Integer.parseInt(et_age1.getText().toString()), sex,
						selectedAddress.country, selectedAddress.province,
						selectedAddress.city,
						new ListenerOnUpdateUserAndWallet() {

							@Override
							public void onSuccess(User user, Wallet wallet) {
								// TODO Auto-generated method stub
								dismissLoadingDlg();
								btn1_tijiao.setClickable(true);
								setResult(Activity.RESULT_OK);
								// Toast.makeText(
								// mSelf,
								// mSelf.getString(R.string.zhucechengong),
								// Toast.LENGTH_LONG).show();
								finish();

							}

							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								dismissLoadingDlg();
								btn1_tijiao.setClickable(true);
								showToast(msg);

							}
						});
			}
			break;
		}

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rg_btn_boy1:
			sex = "boy";
			break;
		case R.id.rg_btn_girl1:
			sex = "girl";
			break;
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

	private void dismissLoadingDlg() {
		mLoadingDlg.dismiss();
	}

	void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
