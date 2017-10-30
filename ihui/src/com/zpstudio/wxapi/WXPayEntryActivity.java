package com.zpstudio.wxapi;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.op.IhuiClientApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	private TextView tv_msg = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_zpstudio_ihui_weixin_pay_result);
        tv_msg = (TextView)findViewById(R.id.tv_msg);
        
    	api = WXAPIFactory.createWXAPI(this, IhuiClientApi.OPEN_WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			tv_msg.setText(String.valueOf(resp.errCode));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(String.valueOf(resp.errCode));
			builder.show();
		}
	}
}