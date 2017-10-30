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
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnLogin;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnRegister;
import com.zpstudio.ui.util.AddressPickerAct;
import com.zpstudio.ui.util.AddressPickerAct.Address;
import com.zpstudio.ui.util.AddressPickerAct.AddressConfig;
import com.zpstudio.ui.util.AddressPickerAct.SingleAddress;
import com.zpstudio.ui.util.DialogUtil;
import com.zpstudio.ui.util.NumberPickerAct;

public class UserZhuCe extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	static final int REQUEST_CODE_NUMBER_PICKER = 1;
	static final int REQUEST_CODE_ADDRESS_PICKER = 2;
	private TextView tv1_fh;
	private EditText et_mingzi;
	private RadioGroup rg_btn;
	private LinearLayout ll_age;
	private TextView et_age;
	private LinearLayout ll_dizhi;
	private TextView ll_dizhi_tips_1;
	private TextView ll_dizhi_tips_2;
	private TextView ll_dizhi_city;
	private EditText et_newmima;
	private Button btn1_wanchengzhuce;

	String phone_number;
	String dynamic_pswd;
	String sex=null;
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
		setContentView(R.layout.com_hzjstudio__ihui_item_userzhuce);
		
		init();
		listen();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if(requestCode==REQUEST_CODE_NUMBER_PICKER) 
		{ 
			if(resultCode == Activity.RESULT_OK)
			{
				int age = intent.getExtras().getInt(NumberPickerAct.RESULT_NUMBER);
				et_age.setText(String.valueOf(age));
			}
			
	        
		}
		else if(requestCode==REQUEST_CODE_ADDRESS_PICKER)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				String json = intent.getExtras().getString(AddressPickerAct.EXTRA_RESULT_SELECTED);
				selectedAddress = gson.fromJson(json, SingleAddress.class);
				ll_dizhi_tips_1.setVisibility(View.GONE);
				ll_dizhi_tips_2.setVisibility(View.GONE);
				ll_dizhi_city.setVisibility(View.VISIBLE);
				ll_dizhi_city.setText(selectedAddress.city);
			}
			
		}
	}
	private void init() {
		mSelf = this;
		phone_number = getIntent().getStringExtra("phone_number");
		dynamic_pswd = getIntent().getStringExtra("dynamic_pswd");
		setResult(Activity.RESULT_CANCELED);
		
		tv1_fh = (TextView) findViewById(R.id.tv1_fh);
		et_mingzi = (EditText) findViewById(R.id.et_mingzi);
		rg_btn = (RadioGroup) findViewById(R.id.rg_btn);
		ll_age = (LinearLayout) findViewById(R.id.ll_age);
		et_age = (TextView) findViewById(R.id.et_age);
		ll_dizhi = (LinearLayout) findViewById(R.id.ll_dizhi);
		ll_dizhi_tips_1 =  (TextView) findViewById(R.id.ll_dizhi_tips_1);
		ll_dizhi_tips_2 =  (TextView) findViewById(R.id.ll_dizhi_tips_2);
		ll_dizhi_city =  (TextView) findViewById(R.id.ll_dizhi_city);
		et_newmima = (EditText) findViewById(R.id.et_newmima);
		btn1_wanchengzhuce = (Button) findViewById(R.id.btn1_wanchengzhuce);

	}

	private void listen() {
		tv1_fh.setOnClickListener(this);
		ll_age.setOnClickListener(this);
		ll_dizhi.setOnClickListener(this);
		btn1_wanchengzhuce.setOnClickListener(this);
		rg_btn.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv1_fh:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;

		case R.id.ll_age:
			startActivityForResult(new Intent(this ,NumberPickerAct.class), REQUEST_CODE_NUMBER_PICKER );
			break;

		case R.id.ll_dizhi:
			showLoadingDlg();
			IhuiClientApi.getInstance(this).getAgentListAsync(new IhuiClientApi.ListenerOnAgentList(){

				@Override
				public void onSuccess(Address agentAddress) {
					// TODO Auto-generated method stub
					
					AddressConfig config = new AddressConfig();
					config.addressTree = agentAddress;
					AddressPickerAct.addressPick(mSelf, REQUEST_CODE_ADDRESS_PICKER, new Gson().toJson(config));
					dismissLoadingDlg();
				}

				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
					dismissLoadingDlg();
				}});
			break;

		case R.id.btn1_wanchengzhuce:
		{
			boolean bValidated = true;
			if(TextUtils.isEmpty(sex))
			{
				showToast("请选择性别");
				bValidated = false;
			}
			else if(TextUtils.isEmpty(et_age.getText().toString()))
			{
				showToast("请选择年龄");
				bValidated = false;
			}
			else if(TextUtils.isEmpty(et_newmima.getText().toString()))
			{
				showToast("请输入密码");
				bValidated = false;
			}
			else if(TextUtils.isEmpty(et_mingzi.getText().toString()))
			{
				showToast("请输入姓名");
				bValidated = false;
			}
//			else if(null == selectedAddress)
//			{
//				showToast("请选择城市");
//				bValidated = false;
//			}
			
			if(bValidated)
			{
				showLoadingDlg();
				btn1_wanchengzhuce.setClickable(false);
				IhuiClientApi.getInstance(this).registerAsync(
						phone_number, 
						sex, 
						Integer.parseInt(et_age.getText().toString()), 
						et_newmima.getText().toString(), 
						et_mingzi.getText().toString(), 
						selectedAddress.country, 
						selectedAddress.province, 
						selectedAddress.city, 
						dynamic_pswd, 
						new ListenerOnRegister(){
	
							@Override
							public void onSuccess(User user, Wallet wallet) {
								// TODO Auto-generated method stub
								dismissLoadingDlg();
								btn1_wanchengzhuce.setClickable(true);
								setResult(Activity.RESULT_OK);
								Toast.makeText(mSelf, mSelf.getString(R.string.zhucechengong), Toast.LENGTH_LONG).show();
								IhuiClientApi.getInstance(UserZhuCe.this).loginAsync(phone_number, et_newmima.getText().toString(), new ListenerOnLogin(){

									@Override
									public void onSuccess(User user, Wallet wallet) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(UserZhuCe.this,
												ActivityTabFragment.class);
										startActivity(intent);
										overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
										finish();
									}

									@Override
									public void onFail(String msg) {
										// TODO Auto-generated method stub
										Toast.makeText(mSelf, msg, Toast.LENGTH_LONG).show();
									}});
								
							}
	
							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								dismissLoadingDlg();
								btn1_wanchengzhuce.setClickable(true);
								showToast(msg);
								
							}});
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
		case R.id.rg_btn_boy:
			sex = "boy";
			break;
		case R.id.rg_btn_girl:
			sex = "girl";
			break;
		default:
			break;
		}

	}
	
	private void showLoadingDlg()
    {
		if(null == mLoadingDlg)
		{
			mLoadingDlg = DialogUtil.createLoadingDialog(this , "");
		}
    	mLoadingDlg.show();
    }
    
    private void dismissLoadingDlg()
    {
    	mLoadingDlg.dismiss();
    }
    void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
