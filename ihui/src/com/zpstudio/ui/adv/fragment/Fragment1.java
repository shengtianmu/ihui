package com.zpstudio.ui.adv.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnJournalismResult;
import com.zpstudio.ui.adv.adapter.HeadlinesViewPagerAdapter;
import com.zpstudio.ui.adv.adapter.JournalismAdapter;
import com.zpstudio.ui.adv.view.ChildViewPager;
import com.zpstudio.ui.adv.view.MyView;

public class Fragment1 extends Fragment {
	
	private View view;
	private ChildViewPager  vp_headlines;
	private PullToRefreshListView lv_journalism;
	private int mScreenWidth;
	private HeadlinesViewPagerAdapter vp_adapter;
	private JournalismAdapter lv_adapter;
	private MyView v_spot;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private List<Journalism> list=new ArrayList<Journalism>();
	private int b=0;
	private ImageView iv_no_wf1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		view= inflater.inflate(R.layout.layout1, container, false);
//		intView();
//		initData();
//		return view;
		if (view==null) {
			view=inflater.inflate(R.layout.layout1,container, false);
			initView();
			initData();
		}
		ViewGroup parent = (ViewGroup) view.getParent();  
	    if (parent != null) {  
	       parent.removeView(view);  
	    }   
		return view;
	}

	private void initData() {
		// TODO Auto-generated method stub
		loadMoreIndiana(true);
	}
	
	
	
	void loadMoreIndiana(final Boolean is) {
		// TODO Auto-generated method stub
		int i=1;
		if (!is) {
			if (list.size()%b==0) {
				i=list.size()/b+1;
			}else {
				Toast.makeText(getActivity(), "没有数据了", 1000).show();
//				i=list.size()/10+2;
				return;
			}
		}
		
		IhuiClientApi.getInstance(getActivity())
				.getJournalismList(i,new ListenerOnJournalismResult() {
					
					@Override
					public void onSuccess(List<Journalism> journalismList) {
						// TODO Auto-generated method stub
						if (b==0) {
							b=journalismList.size();
						}
						Log.e("111", "222"+""+journalismList.toString());
						
						if (is) {
//							if (list.size()==0) {
//								
//
//								list.clear();
//								for (int i = 0; i < journalismList.size(); i++) {
//									list.add(journalismList.get(i));
//								}
////								vp_adapter=new HeadlinesViewPagerAdapter(list, getActivity(), mScreenWidth);
////								vp_headlines.setAdapter(vp_adapter);
//							}else {
//								list.clear();
//							}
							list.clear();
							
						}
//						else {
//							for (int i = 0; i < journalismList.size(); i++) {
//								list.add(journalismList.get(i));
//							}
//						}
						for (int i = 0; i < journalismList.size(); i++) {
							list.add(journalismList.get(i));
						}
						lv_adapter.notifyDataSetChanged();
						lv_journalism.onRefreshComplete();
						Log.e("111", "1111"+"22");
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stu
						iv_no_wf1.setVisibility(View.VISIBLE);
						Toast.makeText(getActivity(), msg, 1000).show();
						lv_journalism.onRefreshComplete();
					}

					@Override
					public void onNoMore(String msg) {
						// TODO Auto-generated method stub
						iv_no_wf1.setVisibility(View.VISIBLE);
						Toast.makeText(getActivity(), msg, 1000).show();
						lv_journalism.onRefreshComplete();
					}
				});
	}
	

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub
//		mPullRefreshScrollView = (PullToRefreshScrollView) view
//				.findViewById(R.id.pulltorefreshscrollview1);
//		mPullRefreshScrollView.setMode(Mode.BOTH);
//		mPullRefreshScrollView
//		.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
//
//			@Override
//			public void onPullDownToRefresh(
//					PullToRefreshBase<ScrollView> refreshView) {
//				// TODO Auto-generated method stub
//				loadMoreIndiana(true);
//				mPullRefreshScrollView.onRefreshComplete();
//			}
//
//			@Override
//			public void onPullUpToRefresh(
//					PullToRefreshBase<ScrollView> refreshView) {
//				loadMoreIndiana(false);
//				mPullRefreshScrollView.onRefreshComplete();
//			}
//
//		});
		
		Display display = getActivity().getWindowManager().getDefaultDisplay();  
        mScreenWidth = display.getWidth(); 
		
//      vp_adapter=new HeadlinesViewPagerAdapter(list, getActivity(), mScreenWidth);
//		vp_headlines.setAdapter(vp_adapter);
//		v_spot=(MyView)view.findViewById(R.id.v_spot);
//		vp_headlines=(ChildViewPager) view.findViewById(R.id.vp_headlines);
//		LayoutParams para = (LayoutParams) vp_headlines.getLayoutParams();  
//        para.height = mScreenWidth/36*19;//一屏幕显示8行  
//        para.width = mScreenWidth;//一屏显示两列  
//        vp_headlines.setLayoutParams(para);
//		
//		vp_headlines.setOnSingleTouchListener(new OnSingleTouchListener() {
//			
//			@Override
//			public void onSingleTouch() {
//				// TODO Auto-generated method stub
//				Intent intent=new Intent();
//				intent.setClass(getActivity(), JournalismActivity.class);
//				Bundle bundle=new Bundle();
//				bundle.putString("id", list.get(0).getId());
//				bundle.putString("page", 1+"");
////				bundle.putString("link", list.get(0).getLink());
//				intent.putExtras(bundle);
//				startActivity(intent);
//			}
//		});
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
        
        iv_no_wf1=(ImageView) view.findViewById(R.id.iv_no_wf1);
        iv_no_wf1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initData();
			}
		});
		
		lv_journalism=(PullToRefreshListView) view.findViewById(R.id.lv_journalism);
		lv_journalism.setMode(Mode.BOTH);
		lv_journalism.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadMoreIndiana(true);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadMoreIndiana(false);
			}

			
		});
		
		
		lv_adapter=new JournalismAdapter(list,getActivity(),mScreenWidth);
		lv_journalism.setAdapter(lv_adapter);
		lv_journalism.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getActivity(), JournalismActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("id", list.get(arg2-1).getId());
				
//				if (list.get(arg2).isIs()) {
//					bundle.putString("content", list.get(arg2).getContent());
//					
//				}
//				if (arg2==0) {
//					bundle.putString("page", 1+"");
//				}else if (arg2%b==0) {
//					bundle.putString("page", list.size()/b+"");
//				}else {
//					bundle.putString("page", list.size()/b+1+"");
//				}
					
				bundle.putString("page", arg2/b+1+"");
				bundle.putString("link", list.get(arg2-1).getLink());
//				bundle.putBoolean("is", list.get(arg2).isIs());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
	
	
	
}
