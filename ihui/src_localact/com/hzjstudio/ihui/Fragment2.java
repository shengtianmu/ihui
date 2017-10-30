package com.hzjstudio.ihui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hzjstudio.ihui.utils.MyGridView;
import com.hzjstudio.ihui.utils.TimeTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.Indiana;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnIndianaList;

public class Fragment2 extends Fragment implements OnClickListener {
	private static final String TAG = "Fragment2";
	private RelativeLayout rel_newest;
	private LinearLayout ll_opening;
	private LinearLayout ll_newest1;
	private LinearLayout ll_newest2;
	private LinearLayout ll_newest3;
	private ImageView img_newest1;
	private ImageView img_newest2;
	private ImageView img_newest3;
	private TextView tv_content1;
	private TextView tv_content2;
	private TextView tv_content3;
	private ImageView img_jishi1;
	private ImageView img_jishi2;
	private ImageView img_jishi3;
	private TimeTextView tv_user1;
	private TimeTextView tv_user2;
	private TimeTextView tv_user3;
	
	//动态加载布局
	private RelativeLayout rellayout_popularity;
	private RelativeLayout rellayout_overplus;
	private RelativeLayout rellayout_newest;
	private RelativeLayout rellayout_takedown;
	private RelativeLayout rellayout_takeup;

	private TextView tv_popularity;
	private TextView tv_overplus;
	private TextView tv_newest;
	private TextView tv_takedown;
	private TextView tv_takeup;

	private ImageView img_popularity;
	private ImageView img_overplus;
	private ImageView img_newest;
	private ImageView img_takedown;
	private ImageView img_takeup;
	
	private MyGridView gridView;
	private IndianaAdapter indianaAdapter;


	private View view;
	// private MyGridView gridView1;
	private PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	// GiftAdapter mGiftAdapter;
	// 定义FragmentTabHost对象
	private SliderLayout mImageSlider;
	private OnSliderClickListener mSliderClickListener = new OnSliderClickListener() {

		@Override
		public void onSliderClick(BaseSliderView slider) {
			// TODO Auto-generated method stub
			Adv adv = (Adv) slider.getObject();
			String link = Config.getFullUrl(getActivity(), adv.getLink());
			link = IhuiClientApi.getInstance(getActivity())
					.addLinkParameter(link, "adv_phone_id",
							String.valueOf(adv.getAdv_phone_id()));
			IhuiClientApi.getInstance(getActivity()).redirect(link);
			IhuiClientApi.getInstance(getActivity()).updateWalletAsync(
					adv.getAdv_phone_id(),
					IhuiClientApi.ADV_ACTION_TYPE_ATTENTION);
		}

	};

	boolean isPullDownRefresh = true;
	Handler handler = new Handler();
	class OpeningIndianaViewHolder
	{
		public LinearLayout ll_newest;
		public ImageView img_newest;
		public TextView tv_content;
		public ImageView img_jishi;
		public TimeTextView tv_user;
		public OpeningIndianaViewHolder(LinearLayout ll_newest , ImageView img_newest , TextView tv_content , 
				ImageView img_jishi ,
				TimeTextView tv_user)
		{
			this.ll_newest  = ll_newest;
			this.img_newest = img_newest;
			this.tv_content = tv_content;
			this.img_jishi  = img_jishi;
			this.tv_user    = tv_user;
		}
	}
	List<OpeningIndianaViewHolder> mOpeningIndianaViewHolders = new ArrayList<OpeningIndianaViewHolder>();
	SimpleDateFormat mDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	String mIndianaType = IhuiClientApi.INDIANA_TYPE_people_desc;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.com_hzjstudio__ihui_fragment2,
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
		gridView = (MyGridView) view.findViewById(R.id.gridView);
		indianaAdapter = new IndianaAdapter(getActivity());
		gridView.setAdapter(indianaAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Indiana obj = (Indiana)indianaAdapter.getItem(position);
				String link = "http://02727.com.cn/mallshop/indiana_details.php?id=" + obj.getIndiana_id();
				IhuiClientApi.getInstance(getActivity()).redirect(link);
			}});
		
		rellayout_popularity = (RelativeLayout) view.findViewById(R.id.rellayout_popularity);
		rellayout_overplus = (RelativeLayout) view.findViewById(R.id.rellayout_overplus);
		rellayout_newest = (RelativeLayout) view.findViewById(R.id.rellayout_newest);
		rellayout_takedown = (RelativeLayout) view.findViewById(R.id.rellayout_takedown);
		rellayout_takeup = (RelativeLayout) view.findViewById(R.id.rellayout_takeup);

		tv_popularity = (TextView) view.findViewById(R.id.tv_popularity);
		tv_overplus = (TextView) view.findViewById(R.id.tv_overplus);
		tv_newest = (TextView) view.findViewById(R.id.tv_newest);
		tv_takedown = (TextView) view.findViewById(R.id.tv_takedown);
		tv_takeup = (TextView) view.findViewById(R.id.tv_takeup);

		img_popularity = (ImageView) view.findViewById(R.id.img_popularity);
		img_overplus = (ImageView) view.findViewById(R.id.img_overplus);
		img_newest = (ImageView) view.findViewById(R.id.img_newest);
		img_takedown = (ImageView) view.findViewById(R.id.img_takedown);
		img_takeup = (ImageView) view.findViewById(R.id.img_takeup);

		rellayout_popularity.setOnClickListener(this);
		rellayout_overplus.setOnClickListener(this);
		rellayout_newest.setOnClickListener(this);
		rellayout_takedown.setOnClickListener(this);
		rellayout_takeup.setOnClickListener(this);
		
		mPullRefreshScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.pulltorefreshscrollview);
		mPullRefreshScrollView.setMode(Mode.BOTH);
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						loadSlider();
						refreshIndianaByExposeTimeDesc();
						refreshIndiana(mIndianaType);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						loadMoreIndiana(mIndianaType);
					}

				});
		mScrollView = mPullRefreshScrollView.getRefreshableView();

		mImageSlider = (SliderLayout) view.findViewById(R.id.imageslider);

		rel_newest = (RelativeLayout) view.findViewById(R.id.rel_newest);
		ll_newest1 = (LinearLayout) view.findViewById(R.id.ll_newest1);
		ll_newest2 = (LinearLayout) view.findViewById(R.id.ll_newest2);
		ll_newest3 = (LinearLayout) view.findViewById(R.id.ll_newest3);

		img_newest1 = (ImageView) view.findViewById(R.id.img_newest1);
		img_newest2 = (ImageView) view.findViewById(R.id.img_newest2);
		img_newest3 = (ImageView) view.findViewById(R.id.img_newest3);

		tv_content1 = (TextView) view.findViewById(R.id.tv_content1);
		tv_content2 = (TextView) view.findViewById(R.id.tv_content2);
		tv_content3 = (TextView) view.findViewById(R.id.tv_content3);

		img_jishi1 = (ImageView) view.findViewById(R.id.img_jishi1);
		img_jishi2 = (ImageView) view.findViewById(R.id.img_jishi2);
		img_jishi3 = (ImageView) view.findViewById(R.id.img_jishi3);

		tv_user1 = (TimeTextView) view.findViewById(R.id.tv_user1);
		tv_user2 = (TimeTextView) view.findViewById(R.id.tv_user2);
		tv_user3 = (TimeTextView) view.findViewById(R.id.tv_user3);
		ll_opening = (LinearLayout) view.findViewById(R.id.ll_opening);
		
		
		rel_newest.setOnClickListener(this);
		ll_newest1.setOnClickListener(this);
		ll_newest2.setOnClickListener(this);
		ll_newest3.setOnClickListener(this);
		
		mOpeningIndianaViewHolders.add(new OpeningIndianaViewHolder(
				ll_newest1,img_newest1 , tv_content1 , img_jishi1 ,tv_user1));
		mOpeningIndianaViewHolders.add(new OpeningIndianaViewHolder(
				ll_newest2,img_newest2 , tv_content2 , img_jishi2 ,tv_user2));
		mOpeningIndianaViewHolders.add(new OpeningIndianaViewHolder(
				ll_newest3,img_newest3 , tv_content3 , img_jishi3 ,tv_user3));
		
		mIndianaType = IhuiClientApi.INDIANA_TYPE_people_desc;
		loadSlider();
		refreshIndianaByExposeTimeDesc();
		refreshIndiana(mIndianaType);

	}

	void refreshIndianaByExposeTimeDesc() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(getActivity())
				.refreshIndianaAsync(IhuiClientApi.INDIANA_TYPE_expose_time_desc , new ListenerOnIndianaList() {
	
					@Override
					public void onSuccess(List<Indiana> list) {
						// TODO Auto-generated method stub
						
						for(int i = 0 ; i < mOpeningIndianaViewHolders.size() ; i ++)
						{
							OpeningIndianaViewHolder holder = mOpeningIndianaViewHolders.get(i);
							holder.ll_newest.setVisibility(View.GONE);
						}
						for(int i = 0 ; i < mOpeningIndianaViewHolders.size() && i < list.size() ; i ++)
						{
							Indiana indiana = list.get(i);
							OpeningIndianaViewHolder holder = mOpeningIndianaViewHolders.get(i);
							ImageLoader.getInstance().displayImage(indiana.getPic_a(), holder.img_newest);
							holder.tv_content.setText(indiana.getTitle());
							
							Date expose;
							try {
								expose = mDf.parse(indiana.getExpose_time());
								Date now = new Date();
								long diff = 0;
								int expireDelay = 0;
								if(expose.getTime() > now.getTime())
								{
									
									diff = expose.getTime() - now.getTime();//这样得到的差值是微秒级别
									expireDelay = 1000;
								}
								
								
								
								if (holder.tv_user.isRun()) {
									holder.tv_user.setRun(false);
								}
								holder.tv_user.setTick(diff);
								holder.tv_user.setExpiredDelay(expireDelay);
								holder.tv_user.setExpiredText(getString(R.string.opening_indiana));
								holder.tv_user.setAfterExpiredText(indiana.getLuckyNickname());
								holder.tv_user.setAfterExpiredImage(holder.img_jishi , R.drawable.com_hzjstudio_ihui_kaijiang);
								holder.tv_user.run();
								holder.ll_newest.setTag(indiana);
								holder.ll_newest.setVisibility(View.VISIBLE);
								
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						if(list.size() == 0)
						{
							ll_opening.setVisibility(View.GONE);
						}
						else
						{
							ll_opening.setVisibility(View.VISIBLE);
						}
						mPullRefreshScrollView
								.onRefreshComplete();
	
					}
	
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mPullRefreshScrollView
								.onRefreshComplete();
					}
	
					@Override
					public void onNoMore() {
						// TODO Auto-generated method stub
					}

				});

	}

	void loadMoreIndianaByExposeTimeDesc() {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(getActivity())
				.loadMoreIndianaAsync(IhuiClientApi.INDIANA_TYPE_expose_time_desc , new ListenerOnIndianaList() {
	
					@Override
					public void onSuccess(List<Indiana> list) {
						// TODO Auto-generated method stub
	
						mPullRefreshScrollView
								.onRefreshComplete();
	
					}
	
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mPullRefreshScrollView
								.onRefreshComplete();
					}
	
					@Override
					public void onNoMore() {
						// TODO Auto-generated method stub
					}

				});

	}
	
	void refreshIndiana(String type) {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(getActivity())
				.refreshIndianaAsync(type , new ListenerOnIndianaList() {
	
					@Override
					public void onSuccess(List<Indiana> list) {
						// TODO Auto-generated method stub
						indianaAdapter.replace(list);
						mPullRefreshScrollView.onRefreshComplete();
					}
	
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mPullRefreshScrollView.onRefreshComplete();
					}
	
					@Override
					public void onNoMore() {
						// TODO Auto-generated method stub
					}

				});

	}
	
	void loadMoreIndiana(String type) {
		// TODO Auto-generated method stub
		IhuiClientApi.getInstance(getActivity())
				.loadMoreIndianaAsync(type , new ListenerOnIndianaList() {
	
					@Override
					public void onSuccess(List<Indiana> list) {
						// TODO Auto-generated method stub
						indianaAdapter.add(list);
						mPullRefreshScrollView.onRefreshComplete();
					}
	
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mPullRefreshScrollView.onRefreshComplete();
					}
					
					@Override
					public void onNoMore() {
						// TODO Auto-generated method stub
						handler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								showToast(getActivity().getString(R.string.nomore));
								mPullRefreshScrollView.onRefreshComplete();
							}}, 500);
						
					}

				});
	}
	
	void loadSlider() {
		IhuiClientApi.getInstance(getActivity()).getFixedAdvListAsync(
				IhuiClientApi.MALLSHOP_ADV_CLASS,
				new ListenerOnGetFixedAdvList() {

					@Override
					public void onSuccess(List<Adv> advList) {
						// TODO Auto-generated method stub
						mImageSlider.stopAutoCycle();
						mImageSlider.removeAllSliders();
						for (Adv adv : advList) {

							TextSliderView textSliderView = new TextSliderView(
									getActivity());
							textSliderView
									.image(Config.getFullAdvImageUrl(
											getActivity(), adv.getContent()))
									.setScaleType(BaseSliderView.ScaleType.Fit)
									.setOnSliderClickListener(
											mSliderClickListener);

							textSliderView.setObject(adv);

							mImageSlider.addSlider(textSliderView);
						}
						if (advList.size() > 1) {

							mImageSlider.startAutoCycle(0, 3000, true);
						}
						mPullRefreshScrollView.onRefreshComplete();
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mPullRefreshScrollView.onRefreshComplete();
					}

					@Override
					public void onNoMore() {
						// TODO Auto-generated method stub
						
					}

				});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rel_newest:
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://www.02727.com.cn/mallshop/publish.php?");
			break;

		case R.id.ll_newest1:
		case R.id.ll_newest2:
		case R.id.ll_newest3:
			Indiana obj = (Indiana)v.getTag();
			if(null != obj)
			{
				String link = "http://02727.com.cn/mallshop/indiana_details.php?id=" + obj.getIndiana_id();
				IhuiClientApi.getInstance(getActivity()).redirect(link);
			}
			break;
		case R.id.rellayout_popularity:
			img_popularity.setVisibility(View.VISIBLE);
			img_overplus.setVisibility(View.INVISIBLE);
			img_newest.setVisibility(View.INVISIBLE);
			img_takedown.setVisibility(View.INVISIBLE);
			img_takeup.setVisibility(View.INVISIBLE);
			tv_popularity.setTextColor(Color.parseColor("#ff5500"));
			tv_overplus.setTextColor(Color.parseColor("#333333"));
			tv_newest.setTextColor(Color.parseColor("#333333"));
			tv_takedown.setTextColor(Color.parseColor("#333333"));
			tv_takeup.setTextColor(Color.parseColor("#333333"));
			mIndianaType = IhuiClientApi.INDIANA_TYPE_people_desc;
			refreshIndiana(mIndianaType);
			break;

		case R.id.rellayout_overplus:
			img_popularity.setVisibility(View.INVISIBLE);
			img_overplus.setVisibility(View.VISIBLE);
			img_newest.setVisibility(View.INVISIBLE);
			img_takedown.setVisibility(View.INVISIBLE);
			img_takeup.setVisibility(View.INVISIBLE);
			tv_popularity.setTextColor(Color.parseColor("#333333"));
			tv_overplus.setTextColor(Color.parseColor("#f35f65"));
			tv_newest.setTextColor(Color.parseColor("#333333"));
			tv_takedown.setTextColor(Color.parseColor("#333333"));
			tv_takeup.setTextColor(Color.parseColor("#333333"));
			mIndianaType = IhuiClientApi.INDIANA_TYPE_overplus_asc;
			refreshIndiana(mIndianaType);
			break;

		case R.id.rellayout_newest:
			img_popularity.setVisibility(View.INVISIBLE);
			img_overplus.setVisibility(View.INVISIBLE);
			img_newest.setVisibility(View.VISIBLE);
			img_takedown.setVisibility(View.INVISIBLE);
			img_takeup.setVisibility(View.INVISIBLE);
			tv_popularity.setTextColor(Color.parseColor("#333333"));
			tv_overplus.setTextColor(Color.parseColor("#333333"));
			tv_newest.setTextColor(Color.parseColor("#f35f65"));
			tv_takedown.setTextColor(Color.parseColor("#333333"));
			tv_takeup.setTextColor(Color.parseColor("#333333"));
			mIndianaType = IhuiClientApi.INDIANA_TYPE_indiana_id_desc;
			refreshIndiana(mIndianaType);
			break;

		case R.id.rellayout_takedown:
			img_popularity.setVisibility(View.INVISIBLE);
			img_overplus.setVisibility(View.INVISIBLE);
			img_newest.setVisibility(View.INVISIBLE);
			img_takedown.setVisibility(View.VISIBLE);
			img_takeup.setVisibility(View.INVISIBLE);
			tv_popularity.setTextColor(Color.parseColor("#333333"));
			tv_overplus.setTextColor(Color.parseColor("#333333"));
			tv_newest.setTextColor(Color.parseColor("#333333"));
			tv_takedown.setTextColor(Color.parseColor("#f35f65"));
			tv_takeup.setTextColor(Color.parseColor("#333333"));
			mIndianaType = IhuiClientApi.INDIANA_TYPE_all_people_desc;
			refreshIndiana(mIndianaType);
			break;

		case R.id.rellayout_takeup:
			img_popularity.setVisibility(View.INVISIBLE);
			img_overplus.setVisibility(View.INVISIBLE);
			img_newest.setVisibility(View.INVISIBLE);
			img_takedown.setVisibility(View.INVISIBLE);
			img_takeup.setVisibility(View.VISIBLE);
			tv_popularity.setTextColor(Color.parseColor("#333333"));
			tv_overplus.setTextColor(Color.parseColor("#333333"));
			tv_newest.setTextColor(Color.parseColor("#333333"));
			tv_takedown.setTextColor(Color.parseColor("#333333"));
			tv_takeup.setTextColor(Color.parseColor("#f35f65"));
			mIndianaType = IhuiClientApi.INDIANA_TYPE_all_people_asc;
			refreshIndiana(mIndianaType);
			break;

		default:
			break;
		}

	}

	public void onDestroy() {
		super.onDestroy();
	}

	void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
	
	static class IndianaHolder{
		public ImageView img_newest;
		public TextView tv_newest;
		public TextView tv_newest_total;
		public TextView tv_newest_residue;
		public ProgressBar pb_newest;
	}
	
	class IndianaAdapter extends BaseAdapter{
		
		LayoutInflater mInflater;
		List<Indiana> mList = new ArrayList<Indiana>();
		
		public IndianaAdapter(Context context){
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
			IndianaHolder holder = null;
			if(convertView == null){
				holder = new IndianaHolder();
				convertView = mInflater.inflate(R.layout.com_hzjstudio_ihui_fragment_fragment_newest_list, null);
				convertView.setTag(holder);
				
				holder.img_newest = (ImageView) convertView.findViewById(R.id.img_newest);
				holder.tv_newest = (TextView) convertView.findViewById(R.id.tv_newest);
				holder.tv_newest_total = (TextView) convertView.findViewById(R.id.tv_newest_total);
				holder.tv_newest_residue = (TextView) convertView.findViewById(R.id.tv_newest_residue);
				holder.pb_newest = (ProgressBar) convertView.findViewById(R.id.pb_newest);
				
			}else {
				
				holder = (IndianaHolder) convertView.getTag();
			}
			
			Indiana obj = (Indiana)getItem(position);
			ImageLoader.getInstance().displayImage(obj.getPic_a(), holder.img_newest);
			holder.tv_newest.setText(obj.getTitle());
			holder.tv_newest_total.setText(String.valueOf(obj.getAll_people()));
			holder.tv_newest_residue.setText(String.valueOf(obj.getAll_people() - obj.getPeople()));
			int percent = (int)((100*obj.getPeople()/obj.getAll_people()));
			holder.pb_newest.setProgress(percent);
			
			return convertView;
		}
		
		public void replace(List<Indiana> objs)
		{
			mList.clear();
			mList.addAll(objs);
			Log.i(TAG , " replace indiana " + mList.size());
			notifyDataSetChanged();
		}
		public void add(List<Indiana> objs)
		{
			mList.addAll(objs);
			notifyDataSetChanged();
		}
		
		
	}
}
