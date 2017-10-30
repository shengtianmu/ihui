package com.zpstudio.ui.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zpstudio.R;

public class SharePopupMenuActivity extends Activity implements OnClickListener {
	private static final String TAG = "SharePopupMenuActivity";
	private static final String EXTRA_TEXT = "text";
	
	private Button btn_share_to_qq_session, btn_share_to_weixin_session ,btn_share_to_mms, btn_cancel;
	private String text = "";
	private Intent share;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_zpstudio_share_popup_menu_act);
		Intent i = getIntent();
        text = i.getStringExtra(EXTRA_TEXT);
        
        share = new Intent(android.content.Intent.ACTION_SEND);
	    share.setType("text/plain");
	    share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	    
	    // all share
		share.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.ihui));
		share.putExtra(Intent.EXTRA_TEXT, text); 
		
		btn_share_to_weixin_session  = (Button) this.findViewById(R.id.btn_share_to_weixin_session);
		btn_share_to_qq_session  = (Button) this.findViewById(R.id.btn_share_to_qq_session);
		btn_share_to_mms  = (Button) this.findViewById(R.id.btn_share_to_mms);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

		btn_share_to_weixin_session.setOnClickListener(this);
		btn_share_to_qq_session.setOnClickListener(this);
		btn_share_to_mms.setOnClickListener(this);
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
		case R.id.btn_share_to_mms:
			shareTo(text , "com.android.mms");
			break;
		case R.id.btn_share_to_qq_session:
			shareTo(text , "com.tencent.mobileqq");
			break;
		case R.id.btn_share_to_weixin_session:
			shareTo(text , "com.tencent.mm");
			break;
		case R.id.btn_cancel:
			break;
		default:
			break;
		}
		finish();
	}
	private void shareTo(String text , String method)
	{
		doShare(search(method) , text);
	}
	private void doShare(ResolveInfo appInfo , String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        if (appInfo != null) {
            sendIntent
                    .setComponent(new ComponentName(
                            appInfo.activityInfo.packageName,
                            appInfo.activityInfo.name));
        }
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        
        
    }

    private List<ResolveInfo> showAllShareApp() {
        java.util.List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        PackageManager pManager = getPackageManager();
        mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    }

    private ResolveInfo search(String method)
    {
    	List<ResolveInfo> list = showAllShareApp();
    	for(ResolveInfo info : list)
    	{
    		Log.i(TAG , info.activityInfo.packageName);
    		if(   info.activityInfo.packageName.contains(method))
    		{
    			return info;
    		}
    	}
    	return null;
    }

}
