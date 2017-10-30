package com.hzjstudio.ihui;

import com.zpstudio.R;
import com.zpstudio.datacenter.db.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecommendMakeMoneyActivity extends Activity implements OnClickListener{
	
	
	private TextView tv_invitation_code;
	private LinearLayout ll_wx;
	private LinearLayout ll_wb;
	private LinearLayout ll_qqhy;
	private LinearLayout ll_qqkj;
	private LinearLayout ll_pyq;
	private LinearLayout back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommendmakemoney);
		initView();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
	}

	private void initView() {
		// TODO Auto-generated method stub
		back=(LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(this);
		tv_invitation_code=(TextView)findViewById(R.id.tv_invitation_code);
		tv_invitation_code.setText(User.loadFromLocal(this).getInvitation_code()+"");
		ll_wx=(LinearLayout)findViewById(R.id.ll_wx);
		ll_wx.setOnClickListener(this);
		ll_wb=(LinearLayout)findViewById(R.id.ll_wb);
		ll_wb.setOnClickListener(this);
		ll_qqhy=(LinearLayout)findViewById(R.id.ll_qqhy);
		ll_qqhy.setOnClickListener(this);
		ll_qqkj=(LinearLayout)findViewById(R.id.ll_qqkj);
		ll_qqkj.setOnClickListener(this);
		ll_pyq=(LinearLayout)findViewById(R.id.ll_pyq);
		ll_pyq.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.back:
			finish();
			break;
		case R.id.ll_wx:
					
			break;
		case R.id.ll_wb:
			
			break;
		case R.id.ll_qqhy:
			
			break;
		case R.id.ll_qqkj:
			
			break;
		case R.id.ll_pyq:
			
			break;

		default:
			break;
		}
	}
	
}
