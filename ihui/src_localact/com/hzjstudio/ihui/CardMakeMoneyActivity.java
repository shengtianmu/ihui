package com.hzjstudio.ihui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.hzjstudio.ihui.adapter.CardMakeMoneyAdapter;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.ui.adv.view.HeightListView;

public class CardMakeMoneyActivity extends Activity{
	
	
	private HeightListView lv;
	private CardMakeMoneyAdapter adapter;
	private List<Adv> advlist=new ArrayList<Adv>();
	private LinearLayout back;
	private LinearLayout ll_gz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cardmakemoney);
		initView();
		initData();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		back=(LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lv=(HeightListView)findViewById(R.id.lv_cardmakemoney);
		adapter=new CardMakeMoneyAdapter(this,advlist);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Adv adv = advlist.get(arg2);
				String link = Config.getFullUrl(CardMakeMoneyActivity.this, adv.getLink());
				link = IhuiClientApi.getInstance(CardMakeMoneyActivity.this).addLinkParameter(link, "adv_phone_id",String.valueOf(adv.getAdv_phone_id()));
				IhuiClientApi.getInstance(CardMakeMoneyActivity.this).redirect(link);
				IhuiClientApi.getInstance(CardMakeMoneyActivity.this).updateWalletAsync(adv.getAdv_phone_id() , IhuiClientApi.ADV_ACTION_TYPE_ATTENTION);
			}
		});
		ll_gz=(LinearLayout)findViewById(R.id.ll_gz);
		ll_gz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IhuiClientApi.getInstance(CardMakeMoneyActivity.this).redirect(
						"http://02727.com.cn/mallshop/look_through.php?phone_number="+User.loadFromLocal(CardMakeMoneyActivity.this).getPhone_number());
			}
		});
		
		
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		getAddressupdat();
	}

	public void getAddressupdat()
	{
		IhuiClientApi.getInstance(this).refreshFixedAdvAsync(7,true,new ListenerOnGetFixedAdvList() {
			
			@Override
			public void onSuccess(List<Adv> advList){
				// TODO Auto-generated method stub
				for (int i = 0; i < advList.size(); i++) {
					advlist.add(advList.get(i));
				}
				Log.e("11", advList.toString());
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				
			}});
	}

}
