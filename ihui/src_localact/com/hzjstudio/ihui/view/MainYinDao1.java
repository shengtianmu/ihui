package com.hzjstudio.ihui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.zpstudio.R;

public class MainYinDao1 extends Activity {
	private ImageView im_yindao8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_mainyindao1);
		im_yindao8 = (ImageView) findViewById(R.id.im_yindao8);
		im_yindao8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainYinDao1.this, MainYinDao2.class);
				startActivity(intent);
				finish();
			}
		});

	}

}
