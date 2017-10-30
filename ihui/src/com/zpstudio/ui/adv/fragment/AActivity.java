package com.zpstudio.ui.adv.fragment;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnJournalismResult;
import com.zpstudio.ui.adv.adapter.HeadlinesViewPagerAdapter;
import com.zpstudio.ui.adv.adapter.JournalismAdapter;
import com.zpstudio.ui.adv.view.HeightListView;
import com.zpstudio.ui.adv.view.MyView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

public class AActivity extends Activity {
	
	private View view;
	private ViewPager vp_headlines;
	private HeightListView lv_journalism;
	private int mScreenWidth;
	private List<String> listvp=new ArrayList<String>();
	private List<String> listlv=new ArrayList<String>();
	private HeadlinesViewPagerAdapter vp_adapter;
	private JournalismAdapter lv_adapter;
	private MyView v_spot;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private List<Journalism> list=new ArrayList<Journalism>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout1);
		initView();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 8; i++) {
			listvp.add(i+"");
			listlv.add(i+"");
		}
		vp_adapter.notifyDataSetChanged();
		v_spot.setMyView(listvp.size(), 1);
//		lv_adapter.notifyDataSetChanged();
		loadMoreIndiana();
	}
	
	
	
	void loadMoreIndiana() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(this)
				.getJournalismList(1,new ListenerOnJournalismResult() {
					
					@Override
					public void onSuccess(List<Journalism> journalismList) {
						// TODO Auto-generated method stub
						Log.e("111", "222"+""+journalismList.toString());
						
						for (int i = 0; i < journalismList.size(); i++) {
							list.add(journalismList.get(i));
						}
						lv_adapter.notifyDataSetChanged();
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stu
						Toast.makeText(AActivity.this, msg, 1000).show();
					}

					@Override
					public void onNoMore(String msg) {
						// TODO Auto-generated method stub
						Toast.makeText(AActivity.this, msg, 1000).show();
					}
				});
	}
	

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub
//		mPullRefreshScrollView = (PullToRefreshScrollView)findViewById(R.id.pulltorefreshscrollview1);
//		mPullRefreshScrollView.setMode(Mode.BOTH);
//		mPullRefreshScrollView
//		.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
//
//			@Override
//			public void onPullDownToRefresh(
//					PullToRefreshBase<ScrollView> refreshView) {
//				// TODO Auto-generated method stub
//				loadMoreIndiana();
//				mPullRefreshScrollView.onRefreshComplete();
//			}
//
//			@Override
//			public void onPullUpToRefresh(
//					PullToRefreshBase<ScrollView> refreshView) {
//				mPullRefreshScrollView.onRefreshComplete();
//			}
//
//		});
//		
//		Display display = getWindowManager().getDefaultDisplay();  
//        mScreenWidth = display.getWidth(); 
//		vp_headlines=(ViewPager) findViewById(R.id.vp_headlines);
//		LayoutParams para = (LayoutParams) vp_headlines.getLayoutParams();  
//        para.height = mScreenWidth/36*19;//一屏幕显示8行  
//        para.width = mScreenWidth;//一屏显示两列  
//        vp_headlines.setLayoutParams(para);
//        
//		vp_adapter=new HeadlinesViewPagerAdapter(list, this, mScreenWidth);
//		vp_headlines.setAdapter(vp_adapter);
//		
//		v_spot=(MyView)findViewById(R.id.v_spot);
//		
//		vp_headlines.setOnPageChangeListener(new OnPageChangeListener() {
//			
//			@Override
//			public void onPageSelected(int arg0) {
//				// XXX Auto-generated method stub
//				v_spot.setMyView(listvp.size(), arg0+1);
//			}
//			
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				// XXX Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//				// XXX Auto-generated method stub
//				
//			}
//		}); 
//		
//		lv_journalism=(HeightListView) findViewById(R.id.lv_journalism);
//		lv_adapter=new JournalismAdapter(list,this);
//		lv_journalism.setAdapter(lv_adapter);
//		lv_journalism.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				Intent intent=new Intent();
//				intent.setClass(AActivity.this, JournalismActivity.class);
//				Bundle bundle=new Bundle();
//				bundle.putString("id", list.get(arg2).getId());
//				if (list.get(arg2).isIs()) {
//					bundle.putString("content", list.get(arg2).getContent());
//					bundle.putString("link", list.get(arg2).getLink());
//				}
//				bundle.putBoolean("is", list.get(arg2).isIs());
//				intent.putExtras(bundle);
//				startActivity(intent);
//			}
//		});
	}
	
}
