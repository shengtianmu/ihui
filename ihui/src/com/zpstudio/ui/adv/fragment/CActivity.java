package com.zpstudio.ui.adv.fragment;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hzjstudio.ihui.DownloadFileModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGamePlayList;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnUpdateUserAndWallet;
import com.zpstudio.ui.adv.view.Utility;

public class CActivity extends Activity{
	private List<String> listIndexs=new ArrayList<String>();
	private int mScreenWidth;
	private View view;
	private TextView tv_jinrishouyi;
	private TextView tv_dangqianyue;
	private TextView tv_leijishouyi;
	private PullToRefreshListView lv_game;
	private DownloadAdapter adapter;
	private PullToRefreshScrollView mPullRefreshScrollView;
	Handler handler = new Handler();
	
	User mUser = null;
	Wallet mWallet = null;
	ListenerOnUpdateUserAndWallet listener = new ListenerOnUpdateUserAndWallet() {

		@Override
		public void onSuccess(User user, Wallet wallet) {
			// TODO Auto-generated method stub
			mUser = user;
			mWallet = wallet;
			
			DecimalFormat df = new DecimalFormat("###.###");
			String s = df.format(mUser.getCredit() / 100);
			tv_dangqianyue.setText(s);

			 s = df.format(mWallet.getToday_credit() / 100);
			 tv_jinrishouyi.setText(s);

			 s = df.format(mWallet.getTotal_incoming() / 100);
			 tv_leijishouyi.setText(s);
			
//			Toast.makeText(this, "onSuccess", 1000).show();
//			IhuiClientApi.getInstance(this).getIndicationsAsync(
//					indicatonsListener);
			
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
//			Toast.makeText(this, "onFail", 1000).show();
		}

	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout3);
		initView();
		initData();
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(this).registerListener(listener);
		IhuiClientApi.getInstance(this).getUserAndWalletAsync();
		refreshGamePlayList();
		loadGame();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub
		mPullRefreshScrollView = (PullToRefreshScrollView)findViewById(R.id.pulltorefreshscrollview);
		mPullRefreshScrollView.setMode(Mode.BOTH);
		mPullRefreshScrollView
		.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				IhuiClientApi.getInstance(CActivity.this).registerListener(listener);
				IhuiClientApi.getInstance(CActivity.this).getUserAndWalletAsync();
				refreshGamePlayList();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ScrollView> refreshView) {
				loadMoreGamePlayList();
			}

		});
		tv_jinrishouyi = (TextView)findViewById(R.id.tv_jinrishouyi);
		tv_dangqianyue = (TextView)findViewById(R.id.tv_dangqianyue);
		tv_leijishouyi = (TextView)findViewById(R.id.tv_leijishouyi);
		
		Display display = getWindowManager().getDefaultDisplay();  
        mScreenWidth = display.getWidth(); 
        lv_game=(PullToRefreshListView)findViewById(R.id.lv_game);
        
//      adapter=new GameAdapter(listIndexs, this);
        
        adapter = new DownloadAdapter(this);
        
        lv_game.setAdapter(adapter);
	}
	
	
	public void refreshGamePlayList()
	{
//		IhuiClientApi.getInstance(this).refreshGamePlayListAsync(new ListenerOnGamePlayList(){
//
//			@Override
//			public void onSuccess(List<DownloadFileModel> download) {
//				// TODO Auto-generated method stub
//				adapter.removeAll();
//				adapter.add(download);
//				mPullRefreshScrollView.onRefreshComplete();
////				Utility.setListViewHeightBasedOnChildren(lv_game);
//			}
//
//			@Override
//			public void onNoMore() {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void onFail(String msg) {
//				// TODO Auto-generated method stub
//				mPullRefreshScrollView.onRefreshComplete();
//			}});
	}
	
	public void loadMoreGamePlayList()
	{
//		IhuiClientApi.getInstance(this).loadMoreGamePlayListAsync(new ListenerOnGamePlayList(){
//
//			@Override
//			public void onSuccess(List<DownloadFileModel> download) {
//				// TODO Auto-generated method stub
//				adapter.add(download);
//				mPullRefreshScrollView.onRefreshComplete();
//			}
//
//			@Override
//			public void onNoMore() {
//				// TODO Auto-generated method stub
//				handler.postDelayed(new Runnable(){
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						showToast(getString(R.string.nomore));
//						mPullRefreshScrollView.onRefreshComplete();
//					}}, 500);
//				
//			}
//
//			@Override
//			public void onFail(String msg) {
//				// TODO Auto-generated method stub
//				mPullRefreshScrollView.onRefreshComplete();
//			}});
	}

	void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	
	public void onResume()
	{
		super.onResume();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<DownloadFileModel> list = adapter.checkInstalled();
			}
			
		}, 100);
		
		
	}
	public void onDestroy()
	{
//		if(mInstalledReceiver != null) 
//		{
//			unregisterReceiver(mInstalledReceiver);
//		}
		FileDownloader.pauseAll();
		super.onDestroy();
		
	}
	
	void loadGame() {
//		IhuiClientApi.getInstance(this).getFixedAdvListAsync(
//				IhuiClientApi.GAME_ADV_CLASS,
//				new ListenerOnGetFixedAdvList() {
//
//					@Override
//					public void onSuccess(List<Adv> advList) {
//						// TODO Auto-generated method stub
//						
//						adapter.add(buildDownloadFileModel(advList));
//					}
//
//					@Override
//					public void onFail(String msg) {
//						// TODO Auto-generated method stub
//					}
//
//				});
	}
	
	List<DownloadFileModel> buildDownloadFileModel(List<Adv> list)
	{
		List<DownloadFileModel> targets = new ArrayList<DownloadFileModel>();
		for(Adv item:list)
		{
			DownloadFileModel model = new DownloadFileModel(this , item);
			
			targets.add(model);
		}
		return targets;
	}
	
	
	void downloadComplete(String link)
	{
		DownloadFileModel obj = adapter.findModelByLink(link);
		if(obj != null)
		{
			IhuiClientApi.getInstance(this).downloadScoreAddAsync(obj.getAdv().getAdv_phone_id());
		}
	}
	
	
	class DownloadHolder {
		public ImageView iv_icon;
		public TextView tv_points;
		public TextView tv_desc;
		public ProgressBar pb_progress;
		public TextView tv_action;
	}
	
	
	class DownloadAdapter extends BaseAdapter implements OnFileDownloadStatusListener {
		LayoutInflater mInflater;
		Context mContext;
		private List<DownloadFileModel> mList = new ArrayList<DownloadFileModel>();
		
		// cached convert views
	    private Map<String, WeakReference<View>> mConvertViews = new LinkedHashMap<String, WeakReference<View>>();

		DecimalFormat df = new DecimalFormat("###.###");
		public DownloadAdapter(Context context)
		{
			super();
			mContext = context;
			mInflater = LayoutInflater.from(context);
			// registerDownloadStatusListener 
	        FileDownloader.registerDownloadStatusListener(this);
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return mList.get(position).getAdv().getAdv_phone_id();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DownloadHolder holder = null;
			DownloadFileModel obj = (DownloadFileModel)getItem(position);
			if (obj == null || TextUtils.isEmpty(obj.getAdv().getLink())) {
	            return null;
	        }

			String url = obj.getAdv().getLink();
	        Log.e("wlf2", "getView,DownloadFileModel.getAdv().getLink():" + obj.getAdv().getLink());
	        View cacheConvertView = null;
	        WeakReference<View> weakCacheConvertView = mConvertViews.get(url);

	        if (weakCacheConvertView == null) {
	            // not exist
	            cacheConvertView = mInflater.inflate(
						R.layout.adapter_game, null);
	            
	            
				holder = createHolder(cacheConvertView);
				
	            mConvertViews.put(url, new WeakReference<View>(cacheConvertView));
	        } else {
	            cacheConvertView = weakCacheConvertView.get();
	            if (cacheConvertView == null) {
	                // not exist
	                cacheConvertView = mInflater.inflate(
							R.layout.adapter_game, null);
	                
	                holder = createHolder(cacheConvertView);
	    			
	                mConvertViews.put(url, new WeakReference<View>(cacheConvertView));
	            }
	        }
	        
	        holder = (DownloadHolder)cacheConvertView.getTag();
			
	        registerEventListener(holder , obj);
	        
			ImageLoader.getInstance().displayImage(obj.getAdv().getContent(), holder.iv_icon);
			holder.tv_desc.setText(obj.getAdv().getDes());
			holder.tv_points.setText(df.format(obj.getAdv().getCredit() / 100));
			
			updateView(obj , holder );
			
			
			return cacheConvertView;
		}
		void updateView(DownloadFileModel obj, DownloadHolder holder)
		{
			double percent = obj.getPercent();
			Log.e("wlf21", "update barProgress:" + percent);
			if(obj.isbInstalled())
			{
				holder.tv_action.setText(mContext.getString(R.string.open));
				holder.pb_progress.setProgress(holder.pb_progress.getMax()); 
			}
			else if(obj.isDownloaded())
			{
				holder.tv_action.setText(mContext.getString(R.string.install));
				holder.pb_progress.setProgress(holder.pb_progress.getMax()); 
			}
			else if(obj.isDownloading())
			{
				Log.e("wlf22", "update barProgress:" + percent);
				holder.tv_action.setText((int)(percent*100)+"%");
				holder.pb_progress.setProgress((int)(percent*100)); 
			}
			else if(obj.isPaused())
			{
				holder.tv_action.setText(mContext.getString(R.string.redownload));
				holder.pb_progress.setProgress((int)(percent*100)); 
			}
			else if(obj.isNew())
			{
				holder.tv_action.setText(mContext.getString(R.string.download));
				holder.pb_progress.setProgress(0); 
			}
		}
		DownloadHolder createHolder(View view)
		{
			DownloadHolder holder = new DownloadHolder();
			view.setTag(holder);
            holder.iv_icon = (ImageView) view
					.findViewById(R.id.iv_icon);
			holder.tv_points = (TextView) view
					.findViewById(R.id.tv_points);
			holder.tv_desc = (TextView) view
					.findViewById(R.id.tv_desc);
			holder.pb_progress = (ProgressBar) view
					.findViewById(R.id.pb_progress);
			holder.tv_action = (TextView) view
					.findViewById(R.id.tv_action);
			return holder;
		}
		public void add(DownloadFileModel obj)
		{
			mList.add(obj);
			notifyDataSetChanged();
		}
		
		public void removeAll()
		{
			mList.clear();
			notifyDataSetChanged();
		}
		
		public void add(List<DownloadFileModel> objs)
		{
			mList.addAll(objs);
			notifyDataSetChanged();
		}
		public List<DownloadFileModel> checkInstalled()
		{
			List<DownloadFileModel> list = new ArrayList<DownloadFileModel>();
			for(DownloadFileModel obj : mList)
			{
				boolean oldstatus = obj.isbInstalled();
				obj.checkInstalled();
				boolean newstatus = obj.isbInstalled();
				if(!oldstatus && newstatus)
				{
					list.add(obj);
				}
			}
			notifyDataSetChanged();
			return list;
		}
		private void registerEventListener(final DownloadHolder holder, final DownloadFileModel obj) {
			holder.tv_action.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                	if(obj.isbInstalled())
        			{
                		obj.startActivity();
        			}
        			else if(obj.isDownloaded())
        			{
        				obj.install();
        			}
        			else if(obj.isDownloading())
        			{
        				FileDownloader.pause(obj.getAdv().getLink());
        			}
        			else if(obj.isPaused())
        			{
        				FileDownloader.reStart(obj.getAdv().getLink());
        			}
        			else if(obj.isNew())
        			{
        				FileDownloader.start(obj.getAdv().getLink());
        			}
        			
                }
			});
		}
		public DownloadFileModel findModelByPackageName(String packagename)
		{
			for(DownloadFileModel obj : mList)
			{
				if(!TextUtils.isEmpty(obj.getPackageName()) && obj.getPackageName().equals(packagename))
				{
					return obj;
				}
			}
			return null;
		}
		private DownloadFileModel findModelByLink(String link)
		{
			for(DownloadFileModel obj : mList)
			{
				if(obj.getAdv().getLink().equals(link))
				{
					return obj;
				}
			}
			return null;
		}
		@Override
		public void onFileDownloadStatusWaiting(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void onFileDownloadStatusPreparing(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void onFileDownloadStatusPrepared(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void onFileDownloadStatusDownloading(
				final DownloadFileInfo downloadFileInfo, float downloadSpeed,
				long remainingTime) {
			// TODO Auto-generated method stub
			Log.e("homer4" , "onFileDownloadStatusCompleted  ");
			if (downloadFileInfo == null) {
	            return;
	        }

	        final WeakReference<View> weakCacheConvertView = mConvertViews.get(downloadFileInfo.getUrl());
	        if (weakCacheConvertView == null || weakCacheConvertView.get() == null) {
	            return;
	        }

	        
	        handler.postDelayed(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					View cacheConvertView = weakCacheConvertView.get();
			        DownloadHolder holder = (DownloadHolder)cacheConvertView.getTag();
			        DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
			        updateView(obj , holder );
				}}, 500);
//	        adapter.notifyDataSetInvalidated();
	        
		}


		@Override
		public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.e("homer3" , "onFileDownloadStatusCompleted  ");
			if (downloadFileInfo == null) {
	            return;
	        }

	        WeakReference<View> weakCacheConvertView = mConvertViews.get(downloadFileInfo.getUrl());
	        if (weakCacheConvertView == null || weakCacheConvertView.get() == null) {
	            return;
	        }

	        View cacheConvertView = weakCacheConvertView.get();
	        DownloadHolder holder = (DownloadHolder)cacheConvertView.getTag();
	        DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
	        updateView(obj , holder );
	        
		}


		@Override
		public void onFileDownloadStatusCompleted(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i("homer2" , "onFileDownloadStatusCompleted  ");
			if (downloadFileInfo == null) {
	            return;
	        }

	        WeakReference<View> weakCacheConvertView = mConvertViews.get(downloadFileInfo.getUrl());
	        if (weakCacheConvertView == null || weakCacheConvertView.get() == null) {
	            return;
	        }

	        View cacheConvertView = weakCacheConvertView.get();
	        DownloadHolder holder = (DownloadHolder)cacheConvertView.getTag();
	        DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
	        updateView(obj , holder );
	        obj.install();
	        downloadComplete(downloadFileInfo.getUrl());
		}


		@Override
		public void onFileDownloadStatusFailed(String url,
				DownloadFileInfo downloadFileInfo,
				FileDownloadStatusFailReason failReason) {
			// TODO Auto-generated method stub
			Log.e("homer1" , "onFileDownloadStatusCompleted  ");
			if (downloadFileInfo == null) {
	            return;
	        }

	        WeakReference<View> weakCacheConvertView = mConvertViews.get(downloadFileInfo.getUrl());
	        if (weakCacheConvertView == null || weakCacheConvertView.get() == null) {
	            return;
	        }

	        View cacheConvertView = weakCacheConvertView.get();
	        DownloadHolder holder = (DownloadHolder)cacheConvertView.getTag();
	        DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
	        updateView(obj , holder );
		}
	}
}
