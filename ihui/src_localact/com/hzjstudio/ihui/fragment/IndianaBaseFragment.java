package com.hzjstudio.ihui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.hzjstudio.ihui.utils.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Gift;
import com.zpstudio.datacenter.db.Indiana;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnIndianaList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class IndianaBaseFragment extends Fragment {
	
	protected abstract String getIndianaType();
	
	private View view;
	private MyGridView gridview_pupularity;
	private PopularityAdapter adapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.com_hzjstudio_ihui_fragment_fragment_popularity,
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
		gridview_pupularity = (MyGridView) view.findViewById(R.id.gridview_popularity);
		adapter = new PopularityAdapter(getActivity());
		gridview_pupularity.setAdapter(adapter);
		gridview_pupularity.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Indiana obj = (Indiana)adapter.getItem(position);
				String link = "http://02727.com.cn/mallshop/indiana_details.php?id=" + obj.getIndiana_id();
				IhuiClientApi.getInstance(getActivity()).redirect(link);
			}});
		refreshIndiana();
		
	}
	void refreshIndiana() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(getActivity())
				.refreshIndianaAsync(getIndianaType() , new ListenerOnIndianaList() {
	
					@Override
					public void onSuccess(List<Indiana> list) {
						// TODO Auto-generated method stub
						adapter.add(list);
	
					}
	
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
					}
	
					@Override
					public void onNoMore() {
						// TODO Auto-generated method stub
					}

				});

	}

	static class PopularityHolder{
		public ImageView img_popularity;
		public TextView tv_popularity;
		public TextView tv_popularity_total;
		public TextView tv_popularity_residue;
		public ProgressBar pb_popularity;
	}
	
	class PopularityAdapter extends BaseAdapter{
		
		LayoutInflater mInflater;
		List<Indiana> mList = new ArrayList<Indiana>();
		public PopularityAdapter(Context context){
			mInflater = LayoutInflater.from(context);
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PopularityHolder holder = null;
			if(convertView == null){
				holder = new PopularityHolder();
				convertView = mInflater.inflate(R.layout.com_hzjstudio_ihui_fragment_fragment_popularity_list, null);
				convertView.setTag(holder);
				
				holder.img_popularity = (ImageView) convertView.findViewById(R.id.img_popularity);
				holder.tv_popularity = (TextView) convertView.findViewById(R.id.tv_popularity);
				holder.tv_popularity_total = (TextView) convertView.findViewById(R.id.tv_popularity_total);
				holder.tv_popularity_residue = (TextView) convertView.findViewById(R.id.tv_popularity_residue);
				holder.pb_popularity = (ProgressBar) convertView.findViewById(R.id.pb_popularity);
				
			}else {
				
				holder = (PopularityHolder) convertView.getTag();
			}
			
			Indiana obj = (Indiana)getItem(position);
			ImageLoader.getInstance().displayImage(obj.getPic_a(), holder.img_popularity);
			holder.tv_popularity.setText(obj.getTitle());
			holder.tv_popularity_total.setText(String.valueOf(obj.getAll_people()));
			holder.tv_popularity_residue.setText(String.valueOf(obj.getAll_people() - obj.getPeople()));
			int percent = (int)((1.0*obj.getPeople()/obj.getAll_people())*100);
			holder.pb_popularity.setProgress(percent);
			return convertView;
		}
		
		public void add(List<Indiana> objs)
		{
			mList.addAll(objs);
			notifyDataSetChanged();
			
		}
		
	}
}
