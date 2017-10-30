package com.hzjstudio.ihui;

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
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener.Type;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener.FileDownloadStatusFailReason;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hzjstudio.ihui.utils.InstalledReceiver;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;

public class GameDownloadActivity extends Activity {
	static final String TAG = "GameDownloadActivity";
	private PullToRefreshListView mPullToRefreshListView;
	private TextView gameplay_fanhui;

	private DownloadAdapter adapter;
	
	public CheckBox cb_gameplay;
	
	InstalledReceiver mInstalledReceiver = null;
	Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio_ihui_gamedownload);
		
//		mInstalledReceiver = new InstalledReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("android.intent.action.PACKAGE_ADDED");
//		filter.addAction("android.intent.action.PACKAGE_REMOVED");
//		filter.addDataScheme("package");
//		registerReceiver(mInstalledReceiver, filter);
		init();
	}
	
	private void init() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_gameplay);
		mPullToRefreshListView.setMode(Mode.BOTH);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mPullToRefreshListView.onRefreshComplete();
					}
					
				}, 500);
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadMoreGamePlayList();
			}

			
		});
		
		adapter = new DownloadAdapter(this);
		mPullToRefreshListView.setAdapter(adapter);
        
		gameplay_fanhui = (TextView) findViewById(R.id.gameplay_fanhui);
		gameplay_fanhui.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		refreshGamePlayList();
	}
	
	public void refreshGamePlayList()
	{
		IhuiClientApi.getInstance(this).refreshFixedAdvAsync(IhuiClientApi.GAME_ADV_CLASS ,true,new ListenerOnGetFixedAdvList() {

			@Override
			public void onSuccess(List<Adv> advList) {
				// TODO Auto-generated method stub
				
				adapter.replace(buildDownloadFileModel(advList));
				mPullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				mPullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
				mPullToRefreshListView.onRefreshComplete();
				
			}

		});
	}
	public void loadMoreGamePlayList()
	{
		IhuiClientApi.getInstance(this).loadMoreFixedAdvAsync(IhuiClientApi.GAME_ADV_CLASS ,new ListenerOnGetFixedAdvList() {

			@Override
			public void onSuccess(List<Adv> advList) {
				// TODO Auto-generated method stub
				
				adapter.add(buildDownloadFileModel(advList));
				mPullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				mPullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
				mPullToRefreshListView.onRefreshComplete();
				
			}

		});
	}

	void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	
	protected void onResume()
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
	protected void onDestroy()
	{
//		if(mInstalledReceiver != null) 
//		{
//			unregisterReceiver(mInstalledReceiver);
//		}
		FileDownloader.pauseAll();
		adapter.destroy();
		super.onDestroy();
		
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
	
	class DownloadAdapter extends BaseAdapter implements OnFileDownloadStatusListener,OnDownloadFileChangeListener {
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
			notifyDataSetChanged();
		}
		
		public void removeAll()
		{
			mList.clear();
			notifyDataSetChanged();
		}
		public void replace(List<DownloadFileModel> objs)
		{
			mList.clear();
			mList.addAll(objs);
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
		private DownloadFileModel onFileDownloadStatus(DownloadFileInfo downloadFileInfo)
		{
			DownloadFileModel obj = null;
			if (downloadFileInfo == null) {
				Log.e(TAG , "onFileDownloadStatus downloadFileInfo is null");
	            return obj;
	        }

	        WeakReference<View> weakCacheConvertView = mConvertViews.get(downloadFileInfo.getUrl());
	        if (weakCacheConvertView == null || weakCacheConvertView.get() == null) {
	        	Log.e(TAG , "onFileDownloadStatus (weakCacheConvertView == null || weakCacheConvertView.get() == null)");
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
			Log.i(TAG , "onFileDownloadStatusWaiting  ");
			onFileDownloadStatus(downloadFileInfo);
			
		}


		@Override
		public void onFileDownloadStatusPreparing(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i(TAG , "onFileDownloadStatusPreparing  ");
			onFileDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onFileDownloadStatusPrepared(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i(TAG , "onFileDownloadStatusPrepared  ");
			onFileDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onFileDownloadStatusDownloading(
				DownloadFileInfo downloadFileInfo, float downloadSpeed,
				long remainingTime) {
			// TODO Auto-generated method stub
			Log.i(TAG , "onFileDownloadStatusDownloading  ");
			onFileDownloadStatus(downloadFileInfo);
	        
		}


		@Override
		public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i(TAG , "onFileDownloadStatusPaused  ");
			onFileDownloadStatus(downloadFileInfo);
	        
		}


		@Override
		public void onFileDownloadStatusCompleted(
				DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			Log.i(TAG , "onFileDownloadStatusCompleted  ");
			DownloadFileModel obj = onFileDownloadStatus(downloadFileInfo);
	        if(null != obj)
	        {
	        	obj.install();
	        }
	        else
			{
				Log.e(TAG, "onFileDownloadStatusCompleted:"  + downloadFileInfo.getUrl()+ " has no obj");
			}
	        downloadComplete(downloadFileInfo.getUrl());
		}


		@Override
		public void onFileDownloadStatusFailed(String url,
				DownloadFileInfo downloadFileInfo,
				FileDownloadStatusFailReason failReason) {
			// TODO Auto-generated method stub
			Log.i("homer" , "onFileDownloadStatusFailed  ");
			onFileDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			
			if(null == downloadFileInfo)
			{
				Log.e(TAG, "onDownloadFileCreated downloadFileInfo is null");
				return ;
			}
			Log.i(TAG , "onDownloadFileCreated " + downloadFileInfo.getUrl());
			DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
			if(null != obj)
			{
				obj.setDownloadFileInfo(downloadFileInfo);
			}
			else
			{
				Log.e(TAG, "onDownloadFileCreated:"  + downloadFileInfo.getUrl()+ " has no obj");
			}
			onFileDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo,
				Type type) {
			// TODO Auto-generated method stub
			if(null == downloadFileInfo)
			{
				Log.e(TAG, "onDownloadFileUpdated downloadFileInfo is null");
				return ;
			}
			Log.i(TAG , "onDownloadFileUpdated " + downloadFileInfo.getUrl());
			DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
			if(null != obj)
			{
				obj.setDownloadFileInfo(downloadFileInfo);
			}
			else
			{
				Log.e(TAG, "onDownloadFileUpdated:"  + downloadFileInfo.getUrl()+ " has no obj");
			}
			onFileDownloadStatus(downloadFileInfo);
		}


		@Override
		public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
			// TODO Auto-generated method stub
			if(null == downloadFileInfo)
			{
				Log.e(TAG, "onDownloadFileDeleted downloadFileInfo is null");
				return ;
			}
			Log.i(TAG , "onDownloadFileDeleted " + downloadFileInfo.getUrl());
			DownloadFileModel obj = findModelByLink(downloadFileInfo.getUrl());
			if(null != obj)
			{
				obj.setDownloadFileInfo(null);
			}
			else
			{
				Log.e(TAG, "onDownloadFileDeleted:"  + downloadFileInfo.getUrl()+ " has no obj");
			}
			onFileDownloadStatus(downloadFileInfo);
		}
	}
	
	
}
