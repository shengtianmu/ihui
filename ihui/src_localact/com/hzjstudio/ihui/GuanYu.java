package com.hzjstudio.ihui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpstudio.R;
import com.zpstudio.datacenter.db.SoftwareVersion;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class GuanYu extends Activity implements OnClickListener {
	private TextView tv_fanhui;
	private TextView tv_banben;
	private RelativeLayout rel_shiyongxuzhi;
	private RelativeLayout rel_yonghuxieyi;
	private RelativeLayout rel_lianxiwomen;

	User mUser = null;
	Wallet mWallet = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_guanyu);
		init();
		listen();
		showInfo();
	}

	/**
	 * 将数据显示到控件上
	 */
	private void showInfo() {

		String versionName = SoftwareVersion.getVersion(this);
		tv_banben.setText(versionName + "版");
	}

	private void init() {
		mUser = IhuiClientApi.getInstance(this).getUser();
		mWallet = IhuiClientApi.getInstance(this).getWallet();
		
		tv_fanhui = (TextView) findViewById(R.id.tv_fanhui);
		tv_banben = (TextView) findViewById(R.id.tv_banben);
		rel_shiyongxuzhi = (RelativeLayout) findViewById(R.id.rel_shiyongxuzhi);
		rel_yonghuxieyi = (RelativeLayout) findViewById(R.id.rel_yonghuxieyi);
		rel_lianxiwomen = (RelativeLayout) findViewById(R.id.rel_lianxiwomen);
	}

	private void listen() {
		tv_fanhui.setOnClickListener(this);
		rel_shiyongxuzhi.setOnClickListener(this);
		rel_yonghuxieyi.setOnClickListener(this);
		rel_lianxiwomen.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_fanhui:
			finish();
			break;

		case R.id.rel_shiyongxuzhi:
			IhuiClientApi.getInstance(this).redirect("http://m.02727.cn:8080/webim/terms_of_use.htm?");
			break;

		case R.id.rel_yonghuxieyi:
			IhuiClientApi.getInstance(this).redirect("http://m.02727.cn:8080/webim/user_contract.htm?");
			break;

		case R.id.rel_lianxiwomen:
			IhuiClientApi.getInstance(this).redirect("http://m.02727.cn:8080/webim/lxwm.htm?");
			break;

		default:
			break;
		}
	}

}
