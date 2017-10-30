package com.hzjstudio.ihui.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.TurnTableResult;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerTurnTableGameGet;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerTurnTableGamePlay;

public class MainZhuanPan extends Activity implements OnClickListener {
	private ImageView im_fh;
	private ImageView im_jl;
	private TextView tv_player;
	private ImageView mLuckyPanView;
	private ImageView mStartBtn;
	private RelativeLayout relativeLayout1;

	TurnTableResult mGameResult = null;
	double mPlayerAvailableCredit = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_luckyturntable);

		im_fh = (ImageView) findViewById(R.id.im_fh);
		im_jl = (ImageView) findViewById(R.id.im_jl);
		tv_player = (TextView) findViewById(R.id.tv_player);
		mLuckyPanView = (ImageView) findViewById(R.id.id_luckypan);
		mStartBtn = (ImageView) findViewById(R.id.id_start_btn);

		// 显示
		im_fh.setOnClickListener(this);
		im_jl.setOnClickListener(this);
		mStartBtn.setOnClickListener(this);

		relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
		relativeLayout1.bringToFront();

		mPlayerAvailableCredit = IhuiClientApi.getInstance(this).getUser()
				.getCredit();

		updateView();

		IhuiClientApi.getInstance(this).getLuckyTurnTableAsync(
				new ListenerTurnTableGameGet() {

					@Override
					public void onSuccess(String image) {

						// 得到可用的图片
						ImageLoader.getInstance().displayImage(image,
								mLuckyPanView);
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						Toast.makeText(MainZhuanPan.this, msg,
								Toast.LENGTH_LONG).show();

					}
				});

	}

	/**
	 * 将数据显示到控件上
	 */
	private void updateView() {
		DecimalFormat df = new DecimalFormat("###.###");
		if (null != mGameResult) {
			mPlayerAvailableCredit = mGameResult.getPlayerAvailableCredit();
		}
		String s = df.format(mPlayerAvailableCredit / 100 ) + "元";
		tv_player.setText(s);
	}

	private AnimationListener al = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			Toast.makeText(MainZhuanPan.this, mGameResult.getNotifyMsg(),
					Toast.LENGTH_SHORT).show();
			updateView();
			mStartBtn.setClickable(true);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.im_fh:
			finish();
			break;

		case R.id.im_jl:
			IhuiClientApi.getInstance(this).redirect(
					"http://02727.com.cn/mallshop/get_record.php");
			break;

		case R.id.id_start_btn:
			mStartBtn.setClickable(false);
			IhuiClientApi.getInstance(this).playLuckyTurnTableAsync(
					new ListenerTurnTableGamePlay() {

						@Override
						public void onSuccess(TurnTableResult gameResult) {
							// TODO Auto-generated method stub
							mGameResult = gameResult;
							RotateAnimation rotateAnimation = new RotateAnimation(
									0, 0 - (float) gameResult.getAngle(),
									Animation.RELATIVE_TO_SELF, 0.5f,
									Animation.RELATIVE_TO_SELF, 0.5f);
							rotateAnimation.setDuration(5000);
							rotateAnimation.setFillAfter(true);
							rotateAnimation.setAnimationListener(al);
							mLuckyPanView.startAnimation(rotateAnimation);
						}

						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
							Toast.makeText(MainZhuanPan.this, msg,
									Toast.LENGTH_LONG).show();
							mStartBtn.setClickable(true);
						}
					});

			break;

		default:
			break;
		}

	}

}
