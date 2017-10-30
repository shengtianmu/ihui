package com.zpstudio.ui.adv.fragment;

import com.zpstudio.R;
import com.zpstudio.ui.adv.LockScreenAdv;
import com.zpstudio.ui.adv.view.DSLView;
import com.zpstudio.ui.adv.view.Unlock;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

public class BActivity extends Activity {
	
	private DSLView dslView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout2);
		initView();
		initData();
	}
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		mActivity = (LockScreenAdv) activity;
//		mHandler=mActivity.getHandler();
//	}
//	public void inter() {
//		Message msg = new Message();
//		msg.obj = 1;
//		msg.what = 1;
//		mHandler.sendMessage(msg);
//	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		
		dslView=(DSLView)findViewById(R.id.dSLView1);
        dslView.setUnlock(new Unlock() {
			
			@Override
			public void leff() {
				// TODO Auto-generated method stub
//				Toast.makeText(MainActivity.this, "leff", 100).show();
//				inter();
			}
			
			@Override
			public void Right() {
				// TODO Auto-generated method stub
//				Toast.makeText(MainActivity.this, "Right", 100).show();
//				inter();
			}
		});
	}
	
}
