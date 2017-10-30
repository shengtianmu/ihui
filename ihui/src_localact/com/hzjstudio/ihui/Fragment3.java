package com.hzjstudio.ihui;

import info.androidhive.imageslider.ActGallery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hzjstudio.ihui.utils.SelectPicPopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Bater;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnBaterChanged;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnBaterList;
import com.zpstudio.util.DateTimeCalculate;

public class Fragment3 extends Fragment implements OnClickListener, ListenerOnBaterChanged {
	public static Fragment3 newInstance() {
		Fragment3 fragmentlist = new Fragment3();
		return fragmentlist;
	}

	
	private View view;
	private LayoutInflater inflater;
	private ImageView im_fabushangpin;

	private PullToRefreshListView mPullToRefreshListView;

	private BaterAdapter mBaterAdapter;
	Handler handler = new Handler();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		if (view == null) {
			view = inflater.inflate(R.layout.com_hzjstudio__ihui_fragment3,
					container, false);
			init(view);
		} else {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}
		return view;
	}

	private void init(View view) {
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.list_yihuo);
		mPullToRefreshListView.setMode(Mode.BOTH);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				refreshBaterList();
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadMoreBaterList();
			}

			
		});
		
		mBaterAdapter = new BaterAdapter();
		mPullToRefreshListView.setAdapter(mBaterAdapter);
		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				Bater bater = (Bater)parent.getAdapter().getItem(position);
				String link = "http://m.02727.cn/mallshop/details.php?id=" + bater.getId();
//				String link = "http://m.02727.cn/mallshop/bater.php?id=" + bater.getId();
				IhuiClientApi.getInstance(getActivity()).redirect(link);
			}
		});
		im_fabushangpin = (ImageView) view.findViewById(R.id.im_fabushangpin);
		im_fabushangpin.setOnClickListener(this);
		refreshBaterList();
		IhuiClientApi.getInstance(getActivity()).registerBaterListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.im_fabushangpin:
			Intent intent = new Intent(getActivity(),
					SelectPicPopupWindow.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	public void onDestroy() {

		IhuiClientApi.getInstance(getActivity()).unregisterBaterListener(this);
		super.onDestroy();
	}
	public void refreshBaterList()
	{
		IhuiClientApi.getInstance(getActivity()).refreshBaterListAsync(new ListenerOnBaterList(){

			@Override
			public void onSuccess(List<Bater> baters) {
				// TODO Auto-generated method stub
				mBaterAdapter.removeAll();
				mBaterAdapter.add(baters);
				mPullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				mPullToRefreshListView.onRefreshComplete();
			}});
	}
	public void loadMoreBaterList()
	{
		IhuiClientApi.getInstance(getActivity()).loadMoreBaterListAsync(new ListenerOnBaterList(){

			@Override
			public void onSuccess(List<Bater> baters) {
				// TODO Auto-generated method stub
				mBaterAdapter.add(baters);
				mPullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						showToast(getActivity().getString(R.string.nomore));
						mPullToRefreshListView.onRefreshComplete();
					}}, 500);
				
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				mPullToRefreshListView.onRefreshComplete();
			}});
	}
	
	
	void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
	 static class BaterViewHolder {
		 public ImageView me_portrait;
		 public TextView me_name;
		 public TextView me_shijian;
		 public TextView me_jiage;
		 public ImageView me_im[];
		 public TextView me_biaoti;
		 public TextView me_neirong;
		 public TextView me_xiangqing;
		 public CheckBox me_shouchang;
		 public ImageView me_pinglun;
		 public LinearLayout me_ll_pinglun;
		 public EditText me_et_pinglun;
		 public Button me_btn_pinglun;
//		 public TextView text_phop;
//		 public TextView text_address;
		 
		 public ListView lv_comments;
		 public CommentAdapter commentAdpater;
		 
	 }

	class BaterAdapter extends BaseAdapter {
		List<Bater> mList = new ArrayList<Bater>();
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
			return mList.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BaterViewHolder holder = null;
			if(null == convertView)
			{
				holder = new BaterViewHolder();
				convertView = inflater.inflate(
						R.layout.com_hzjstudio_ihui_yihuo_list, null);
				convertView.setTag(holder);
				
				holder.me_portrait = (ImageView) convertView
						.findViewById(R.id.me_portrait);
				holder.me_name = (TextView) convertView.findViewById(R.id.me_name);
				holder.me_shijian = (TextView) convertView.findViewById(R.id.me_shijian);
				holder.me_jiage = (TextView) convertView.findViewById(R.id.me_jiage);
				holder.me_im = new ImageView[3];
				holder.me_im[0] = (ImageView) convertView.findViewById(R.id.me_im1);
				holder.me_im[1] = (ImageView) convertView.findViewById(R.id.me_im2);
				holder.me_im[2] = (ImageView) convertView.findViewById(R.id.me_im3);
//				holder.me_im[3] = (ImageView) convertView.findViewById(R.id.me_im4);
				holder.me_biaoti = (TextView) convertView.findViewById(R.id.me_biaoti);
//				holder.text_phop = (TextView) convertView.findViewById(R.id.text_phop);
//				holder.text_address = (TextView) convertView.findViewById(R.id.text_address);
				
				
				holder.me_neirong = (TextView) convertView.findViewById(R.id.me_neirong);
				holder.me_xiangqing = (TextView) convertView
						.findViewById(R.id.me_xiangqing);
//				holder.me_shouchang = (CheckBox) convertView
//						.findViewById(R.id.me_shouchang);
//				holder.me_pinglun = (ImageView) convertView.findViewById(R.id.me_pinglun);
//				holder.me_ll_pinglun = (LinearLayout) convertView
//						.findViewById(R.id.me_ll_pinglun);
//				holder.me_et_pinglun = (EditText) convertView
//						.findViewById(R.id.me_et_pinglun);
//				holder.me_btn_pinglun = (Button) convertView
//						.findViewById(R.id.me_btn_pinglun);
//				holder.lv_comments = (ListView) convertView.findViewById(R.id.lv_comments);
//				holder.commentAdpater = new CommentAdapter();
//				holder.lv_comments.setAdapter(holder.commentAdpater);
			}
			else
			{
				holder =(BaterViewHolder)convertView.getTag();
			}
			
			Bater bater = mList.get(position);
			ImageLoader.getInstance().displayImage(bater.getAvatar(), holder.me_portrait);
			holder.me_name.setText(bater.getNickname());
			
			String value = DateTimeCalculate.fromThenOn(bater.getCreatetime()).getDiffrence();  
			String names = value.replace(".0", "");  
			
			holder.me_shijian.setText(names);
			holder.me_jiage.setText(String.valueOf(bater.getExtraDatainfo().getPrice()));
//			holder.text_phop.setText(bater.getExtraDatainfo().getContact());
//			holder.text_address.setText(bater.getExtraDatainfo().getAddress());
			for(int i = 0 ; i < holder.me_im.length ; i ++)
			{
				ImageView imageView = holder.me_im[i];
				imageView.setVisibility(View.GONE);
				
			}
			
			for(int i = 0 ; i < bater.getMedias_thumb().size() && i < holder.me_im.length ; i ++)
			{
				ImageView imageView = holder.me_im[i];
				ImageLoader.getInstance().displayImage( bater.getMedias_thumb().get(i).getThumbUrl(), imageView);
				imageView.setTag(bater);
				imageView.setOnClickListener(new MyOnClickListener(bater , i));
				imageView.setVisibility(View.VISIBLE);
				
			}
			holder.me_biaoti.setText(bater.getTitle());
			holder.me_neirong.setText(bater.getContent());
			return convertView;

		}
		
		void add(Bater bater)
		{
			mList.add(0,bater);
			notifyDataSetChanged();
		}
		void add(List<Bater> list)
		{
			mList.addAll(list);
			notifyDataSetChanged();
		}
		void removeAll()
		{
			mList.clear();
			notifyDataSetChanged();
		}
		
		public class MyOnClickListener implements OnClickListener {
			Bater bater ;
			int pos;
			public MyOnClickListener(Bater bater , int pos)
			{
				this.bater = bater;
				this.pos = pos;
			}
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewImages(bater.getMedias() , pos);
			}
			
		}

	}
	
	private class CommentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(
					R.layout.com_hzjstudio_ihui_pinglun, null);
			
			return convertView;
		}

	}
	void viewImages(List<String> urls , int pos)
	{
		Intent i = new Intent(getActivity(), ActGallery.class);
		i.putExtra(ActGallery.EXTRA_POSITION, pos);
		i.putExtra(ActGallery.EXTRA_PATHS, urls.toArray(new String[0]));
		startActivity(i);
	}
	@Override
	public void onAdd(Bater bater) {
		// TODO Auto-generated method stub
		mBaterAdapter.add(bater);
	}


}
