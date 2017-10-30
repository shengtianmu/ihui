package com.hzjstudio.ihui;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzjstudio.ihui.view.ShouRu;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class UserShouRu extends Activity implements OnClickListener {
	private TextView textview_fanhui1;
	private TextView tv_jiesuoshouru;
	private TextView tv_guanzhushouru;
	private RelativeLayout rel_zhuanzhangmingxi;

	User mUser = null;
	Wallet mWallet = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_usershouru);
		init();
		showInfo();

	}

	/**
	 * 将数据显示到控件上
	 */
	private void showInfo() {
		DecimalFormat df = new DecimalFormat("###.###");
		tv_jiesuoshouru.setText(df.format(mWallet.getUnlock_credit() / 100));
		tv_guanzhushouru.setText(df.format(mWallet.getAttention_credit() / 100));
	}

	private void init() {
		mUser = IhuiClientApi.getInstance(this).getUser();
		mWallet = IhuiClientApi.getInstance(this).getWallet();
		
		textview_fanhui1 = (TextView) findViewById(R.id.textview_fanhui1);
		tv_jiesuoshouru = (TextView) findViewById(R.id.tv_jiesuoshouru);
		tv_guanzhushouru = (TextView) findViewById(R.id.tv_guanzhushouru);
		rel_zhuanzhangmingxi = (RelativeLayout) findViewById(R.id.rel_zhuanzhangmingxi);

		textview_fanhui1.setOnClickListener(this);
		rel_zhuanzhangmingxi.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textview_fanhui1:
			finish();
			break;

		case R.id.rel_zhuanzhangmingxi:
			Intent intent = new Intent(UserShouRu.this, ShouRu.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
