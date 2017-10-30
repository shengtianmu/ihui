package com.zpstudio.ui.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zpstudio.R;
import com.zpstudio.util.WeixinShareManager;

public class WebMorePopupMenuActivity extends Activity implements OnClickListener {

	public static final String EXTRA_ACTION_RESULT_SELECTED = "selected";
	public static final int  MENU_NONE = WeixinShareManager.WEIXIN_SHARE_TYPE_SESSION ;
	public static final int  MENU_SHARE_TO_TIMELINE = WeixinShareManager.WEIXIN_SHARE_TYPE_TIMELINE;
	public static final int  MENU_SHARE_TO_SESSION = WeixinShareManager.WEIXIN_SHARE_TYPE_SESSION ;
	private Button btn_share_to_weixin_timeline, btn_share_to_weixin_session , btn_cancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_zpstudio_web_more_popup_menu_act);
		btn_share_to_weixin_timeline = (Button) this.findViewById(R.id.btn_share_to_weixin_timeline);
		btn_share_to_weixin_session  = (Button) this.findViewById(R.id.btn_share_to_weixin_session);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

		btn_share_to_weixin_timeline.setOnClickListener(this);
		btn_share_to_weixin_session.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		
		setResult(Activity.RESULT_CANCELED);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share_to_weixin_timeline:
			setMenuSelectedResult(Activity.RESULT_OK , MENU_SHARE_TO_TIMELINE);
			break;
		case R.id.btn_share_to_weixin_session:
			setMenuSelectedResult(Activity.RESULT_OK , MENU_SHARE_TO_SESSION);
			break;
		case R.id.btn_cancel:
			break;
		default:
			break;
		}
		finish();
	}
	
	public void setMenuSelectedResult(int result , int menu)
	{
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_ACTION_RESULT_SELECTED,  menu); 
		setResult(result , new Intent().putExtras(bundle));
	}

}
