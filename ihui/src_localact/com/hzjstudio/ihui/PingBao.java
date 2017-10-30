package com.hzjstudio.ihui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hzjstudio.ihui.utils.UISwitchButton;
import com.zpstudio.R;

public class PingBao extends Activity {
	private TextView fanhui;
	private UISwitchButton switch1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_pingbao);
		initView();
	}

	private void initView() {
		fanhui = (TextView) findViewById(R.id.fanhui);
		fanhui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		switch1 = (UISwitchButton) findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Toast.makeText(PingBao.this, "开启", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(PingBao.this, "关闭", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}
}
