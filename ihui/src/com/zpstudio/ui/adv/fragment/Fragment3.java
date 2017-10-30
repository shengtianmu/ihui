package com.zpstudio.ui.adv.fragment;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
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
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnUpdateUserAndWallet;

public class Fragment3 extends Fragment {
	
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
	private List<DownloadFileModel> mList = new ArrayList<DownloadFileModel>();;
	
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
			
//			Toast.makeText(getActivity(), "onSuccess", 1000).show();
//			IhuiClientApi.getInstance(getActivity()).getIndicationsAsync(
//					indicatonsListener);
			
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
//			Toast.makeText(getActivity(), "onFail", 1000).show();
			iv_no_wf3.setVisibility(View.VISIBLE);
			lv_game.setVisibility(View.GONE);
		}

	};
	private ImageView iv_no_wf3;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view==null) {
			view=inflater.inflate(R.layout.layout3,container, false);
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
		IhuiClientApi.getInstance(getActivity()).registerListener(listener);
		IhuiClientApi.getInstance(getActivity()).getUserAndWalletAsync();
		refreshGamePlayList();
//		loadGame();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub
		iv_no_wf3=(ImageView) view.findViewById(R.id.iv_no_wf3);
		mPullRefreshScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.pulltorefreshscrollview);
		mPullRefreshScrollView.setMode(Mode.BOTH);
		mPullRefreshScrollView
		.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				IhuiClientApi.getInstance(getActivity()).registerListener(listener);
				IhuiClientApi.getInstance(getActivity()).getUserAndWalletAsync();
				refreshGamePlayList();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ScrollView> refreshView) {
				loadMoreGamePlayList();
			}

		});
		tv_jinrishouyi = (TextView) view.findViewById(R.id.tv_jinrishouyi);
		tv_dangqianyue = (TextView) view.findViewById(R.id.tv_dangqianyue);
		tv_leijishouyi = (TextView) view.findViewById(R.id.tv_leijishouyi);
		
		Display display = getActivity().getWindowManager().getDefaultDisplay();  
        mScreenWidth = display.getWidth(); 
        lv_game=(PullToRefreshListView) view.findViewById(R.id.lv_game);
        lv_game.setMode(Mode.DISABLED);
//        lv_game.setEmptyView(newEmptyView)
//      adapter=new GameAdapter(listIndexs, getActivity());
        adapter = new DownloadAdapter(getActivity());
        lv_game.setAdapter(adapter);
	}
	
	
	private void setGo() {
		// TODO Auto-generated method stub
		LayoutParams para = (LayoutParams) lv_game.getLayoutParams();  
        para.height = mList.size()*(dip2px(getActivity(),70)+2);//一屏幕显示8行  
        para.width = mScreenWidth;//一屏显示两列  
        lv_game.setLayoutParams(para);
	}
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    
	
    public void refreshGamePlayList()
	{
		IhuiClientApi.getInstance(getActivity()).refreshFixedAdvAsync(IhuiClientApi.GAME_ADV_CLASS ,true,new ListenerOnGetFixedAdvList() {

			@Override
			public void onSuccess(List<Adv> advList) {
				// TODO Auto-generated method stub
				
				adapter.replace(buildDownloadFileModel(advList));
				
				mPullRefreshScrollView.onRefreshComplete();
				
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				iv_no_wf3.setVisibility(View.VISIBLE);
				mPullRefreshScrollView.onRefreshComplete();
				lv_game.setVisibility(View.GONE);
			}

			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
				iv_no_wf3.setVisibility(View.VISIBLE);
				mPullRefreshScrollView.onRefreshComplete();
				lv_game.setVisibility(View.GONE);
				
			}

		});
	}
	public void loadMoreGamePlayList()
	{
		IhuiClientApi.getInstance(getActivity()).loadMoreFixedAdvAsync(IhuiClientApi.GAME_ADV_CLASS ,new ListenerOnGetFixedAdvList() {

			@Override
			public void onSuccess(List<Adv> advList) {
				// TODO Auto-generated method stub
				
				adapter.add(buildDownloadFileModel(advList));
//				
				mPullRefreshScrollView.onRefreshComplete();
				
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				mPullRefreshScrollView.onRefreshComplete();
				iv_no_wf3.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
				mPullRefreshScrollView.onRefreshComplete();
				iv_no_wf3.setVisibility(View.VISIBLE);
				
			}

		});
	}

	void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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
	List<DownloadFileModel> buildDownloadFileModel(List<Adv> list)
	{
		List<DownloadFileModel> targets = new ArrayList<DownloadFileModel>();
		for(Adv item:list)
		{
			DownloadFileModel model = new DownloadFileModel(getActivity() , item);
			
			targets.add(model);
		}
		return targets;
	}
	void downloadComplete(String link)
	{
		DownloadFileModel obj = adapter.findModelByLink(link);
		if(obj != null)
		{
			
			IhuiClientApi.getInstance(getActivity()).downloadScoreAddAsync(obj.getAdv().getAdv_phone_id());
		}
	}
	class DownloadHolder {
		public ImageView iv_icon;
		public TextView tv_points;
		public TextView tv_desc;
		public ProgressBar pb_progress;
		public TextView tv_action;
	}
	
	class DownloadAdapter extends BaseAdapter implements OnFileDownloadStatusListener,OnDownloadFileChangeListener {
		LayoutInflater mInflater;
		Context mContext;
//		private List<DownloadFileModel> mList = new ArrayList<DownloadFileModel>();
		
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
	        FileDownloader.registerDownloadFileChangeListener(this);
		}
		public void destroy()
		{
			FileDownloader.unregisterDownloadFileChangeListener(this);
			FileDownloader.unregisterDownloadStatusListener(this);
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
	        Log.e("wlf", "getView,DownloadFileModel.getAdv().getLink():" + obj.getAdv().getLink());
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
			Log.i("wlf", "update barProgress:" + percent);
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
			setGo();
			notifyDataSetChanged();
		}
		
		public void removeAll()
		{
			mList.clear();
			setGo();
			notifyDataSetChanged();
		}
		public void replace(List<DownloadFileModel> objs)
		{
			mList.clear();
			mList.addAll(objs);
			setGo();
			notifyDataSetChanged();
		}
		public void add(List<DownloadFileModel> objs)
		{
			mList.addAll(objs);
			setGo();
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
			setGo();
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
		private DownloadFileModel onFildDownloadStatus(DownloadFileInfo downloadFileInfo)
		{
			DownloadFileModel obj = null;
			if (downloadFileInfo == null) {
	            return obj;
	        }

	        WeakReference<View> weakCacheConvertView = mConvertViews.get(downloadFileInfo.getUrl());
	        if (weakCacheConvertView == null || weakCacheConvertView.get() == null) {
	            return obj;
	        }

	        View cacheConvertView = weakCacheConvertView.get();
	        DownloadHolder holder = (DownloadHolder)cacheConvertView.getTag();
	        obj = findModelByLink(downloadFileInfo.getUrl());
	        updateView(obj , holder );
	        return obj;
		}
		@Override
		public void onFileDownloadStatusWaiting(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusWaiting  ");
			onFildDownloadStatus(downloadFileInfo);
			
		}


		@Override
		public void onFileDownloadStatusPreparing(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusPreparing  ");
			onFildDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onFileDownloadStatusPrepared(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusPrepared  ");
			onFildDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onFileDownloadStatusDownloading(
				DownloadFileInfo downloadFileInfo, float downloadSpeed,
				long remainingTime) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusDownloading  ");
			onFildDownloadStatus(downloadFileInfo);
	        
		}


		@Override
		public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusPaused  ");
			onFildDownloadStatus(downloadFileInfo);
	        
		}


		@Override
		public void onFileDownloadStatusCompleted(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusCompleted  ");
			DownloadFileModel obj = onFildDownloadStatus(downloadFileInfo);
	        obj.install();
	        downloadComplete(downloadFileInfo.getUrl());
		}


		@Override
		public void onFileDownloadStatusFailed(String url,
				DownloadFileInfo downloadFileInfo,
				FileDownloadStatusFailReason failReason) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusFailed  ");
			onFildDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
			if(null != obj)
			{
				obj.setDownloadFileInfo(downloadFileInfo);
			}
			onFildDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo,
				Type type) {
			// TODO Auto-generated method stub
			DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
			if(null != obj)
			{
				obj.setDownloadFileInfo(downloadFileInfo);
			}
			onFildDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
			if(null != obj)
			{
				obj.setDownloadFileInfo(null);
			}
			onFildDownloadStatus(downloadFileInfo);
		}
	}
}
