package com.hzjstudio.ihui;

import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hzjstudio.ihui.view.MainZhuanPan;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.Indication;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnIndications;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnUpdateUserAndWallet;

public class Fragment1 extends Fragment implements OnClickListener,
		OnSliderClickListener {
	private static final String TAG = "Fragment1";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	/**
	 * 存储上一个选择项的Index
	 */
	private SliderLayout mImageSlider;

	private TextView tv_jinrishouyi;
	private TextView tv_dangqianyue;
	private TextView tv_leijishouyi;

	private LinearLayout ll_aizhuan;
	private LinearLayout ll_huafei;
	private LinearLayout ll_xianjin;
	private LinearLayout ll_youhuiquan;
	private LinearLayout ll_zhuanqian;

	private TextView msg_new_counter1;
	private TextView msg_new_counter2;
	private TextView msg_new_counter3;
	private TextView msg_new_counter4;
	private TextView msg_new_counter5;

	private LinearLayout appxBannerContainer;

	private PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;

	// 这里是补充的内容
	private View view;

	User mUser = null;
	Wallet mWallet = null;
	List<Indication> mIndicaitonList = null;
	ListenerOnUpdateUserAndWallet listener = new ListenerOnUpdateUserAndWallet() {

		@Override
		public void onSuccess(User user, Wallet wallet) {
			// TODO Auto-generated method stub
			mUser = user;
			mWallet = wallet;
			IhuiClientApi.getInstance(getActivity()).getIndicationsAsync(
					indicatonsListener);

		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub

		}

	};
	ListenerOnIndications indicatonsListener = new ListenerOnIndications() {

		@Override
		public void onSuccess(List<Indication> indicationList) {
			// TODO Auto-generated method stub
			mIndicaitonList = indicationList;
			updateView();
			mPullRefreshScrollView.onRefreshComplete();
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			mPullRefreshScrollView.onRefreshComplete();
		}

	};

	private List<Adv> mGameList = null;
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
	private LinearLayout ll_cardmakemone;
	private LinearLayout ll_recommendmakemone;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.com_hzjstudio__ihui_fragment1,
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

	/**
	 * 将数据显示到控件上
	 */
	private void updateView() {

		DecimalFormat df = new DecimalFormat("###.###");
		String s = df.format(mUser.getCredit() / 100);
		tv_dangqianyue.setText(s);

		s = df.format(mWallet.getToday_credit() / 100);
		tv_jinrishouyi.setText(s);

		s = df.format(mWallet.getTotal_incoming() / 100);
		tv_leijishouyi.setText(s);

		if (null != mIndicaitonList) {
			for (Indication indicaton : mIndicaitonList) {
				// if (indicaton.getName().equals("GuideIndication")) {
				// msg_new_counter1
				// .setVisibility(indicaton.isbNeed() ? View.VISIBLE
				// : View.INVISIBLE);
				// }
				// else if (indicaton.getName().equals("TodayTaskIndication")) {
				// msg_new_counter2
				// .setVisibility(indicaton.isbNeed() ? View.VISIBLE
				// : View.INVISIBLE);
				// } else if
				// (indicaton.getName().equals("PuzzleGameIndication")) {
				// msg_new_counter3
				// .setVisibility(indicaton.isbNeed() ? View.VISIBLE
				// : View.INVISIBLE);
				// }
				if (indicaton.getName().equals("CouponIndication")) {
					msg_new_counter1
							.setVisibility(indicaton.isbNeed() ? View.VISIBLE
									: View.INVISIBLE);
				} else if (indicaton.getName().equals("TodayTaskIndication")) {
					msg_new_counter5
							.setVisibility(indicaton.isbNeed() ? View.VISIBLE
									: View.INVISIBLE);
				}
			}

		}
	}

	/**
	 * 初始化，获得数据,以及 UserAndWallet 对象获取
	 * 
	 * @param view
	 */
	private void init(View view) {
		mPullRefreshScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.pulltorefreshscrollview);
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						Log.i(TAG, "onRefresh");
						IhuiClientApi.getInstance(getActivity())
								.getUserAndWalletAsync();
						loadSlider();
						loadGame();
						IhuiClientApi.getInstance(getActivity())
								.getIndicationsAsync(indicatonsListener);

					}
				});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		mImageSlider = (SliderLayout) view.findViewById(R.id.imageslider);

		sp = getActivity().getSharedPreferences("config", 1);
		editor = sp.edit();

		mUser = IhuiClientApi.getInstance(getActivity()).getUser();
		mWallet = IhuiClientApi.getInstance(getActivity()).getWallet();

		tv_jinrishouyi = (TextView) view.findViewById(R.id.tv_jinrishouyi);
		tv_dangqianyue = (TextView) view.findViewById(R.id.tv_dangqianyue);
		ll_aizhuan = (LinearLayout) view.findViewById(R.id.ll_aizhuan);
		ll_huafei = (LinearLayout) view.findViewById(R.id.ll_huafei);
		ll_xianjin = (LinearLayout) view.findViewById(R.id.ll_xianjin);
		ll_youhuiquan = (LinearLayout) view.findViewById(R.id.ll_youhuiquan);
		ll_zhuanqian = (LinearLayout) view.findViewById(R.id.ll_zhuanqian);
		
		ll_cardmakemone = (LinearLayout) view.findViewById(R.id.ll_cardmakemone);
		ll_recommendmakemone = (LinearLayout) view.findViewById(R.id.ll_recommendmakemone);
		
		tv_leijishouyi = (TextView) view.findViewById(R.id.tv_leijishouyi);
		msg_new_counter1 = (TextView) view.findViewById(R.id.msg_new_counter1);
		msg_new_counter2 = (TextView) view.findViewById(R.id.msg_new_counter2);
		msg_new_counter3 = (TextView) view.findViewById(R.id.msg_new_counter3);
		msg_new_counter4 = (TextView) view.findViewById(R.id.msg_new_counter4);
		msg_new_counter5 = (TextView) view.findViewById(R.id.msg_new_counter5);
		if (sp.getBoolean("is_msg_new_counter1", false)) {
			msg_new_counter1.setVisibility(View.INVISIBLE);
		}
		if (sp.getBoolean("is_msg_new_counter2", false)) {
			msg_new_counter2.setVisibility(View.INVISIBLE);
		}
		if (sp.getBoolean("is_msg_new_counter3", false)) {
			msg_new_counter3.setVisibility(View.INVISIBLE);
		}
		if (sp.getBoolean("is_msg_new_counter4", false)) {
			msg_new_counter4.setVisibility(View.INVISIBLE);
		}
		if (sp.getBoolean("is_msg_new_counter5", false)) {
			msg_new_counter5.setVisibility(View.INVISIBLE);
		}

		// initBaiduBanner();

		listen();
		updateView();
		loadSlider();
		loadGame();
		IhuiClientApi.getInstance(getActivity()).registerListener(listener);
		IhuiClientApi.getInstance(getActivity()).getUserAndWalletAsync();
		IhuiClientApi.getInstance(getActivity()).getIndicationsAsync(
				indicatonsListener);

	}

	void loadSlider() {
		IhuiClientApi.getInstance(getActivity()).getFixedAdvListAsync(
				IhuiClientApi.HOMEPAGE_ADV_CLASS,
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

	void loadGame() {
		IhuiClientApi.getInstance(getActivity()).getFixedAdvListAsync(
				IhuiClientApi.GAME_ADV_CLASS, new ListenerOnGetFixedAdvList() {

					@Override
					public void onSuccess(List<Adv> advList) {
						// TODO Auto-generated method stub
						mGameList = advList;
						if (mGameList.size() > 0) {
						} else {

						}
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

	public void onDestroy() {

		IhuiClientApi.getInstance(getActivity()).unregisterListener(listener);
		super.onDestroy();
	}

	private void listen() {
		ll_aizhuan.setOnClickListener(this);
		ll_huafei.setOnClickListener(this);
		ll_xianjin.setOnClickListener(this);
		ll_youhuiquan.setOnClickListener(this);
		ll_zhuanqian.setOnClickListener(this);
		ll_cardmakemone.setOnClickListener(this);
		ll_recommendmakemone.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_aizhuan:
			msg_new_counter2.setVisibility(View.INVISIBLE);
			editor.putBoolean("is_msg_new_counter2", true);
			editor.commit();
			Intent intent = new Intent(getActivity(), MainZhuanPan.class);
			startActivity(intent);
			break;
		case R.id.ll_huafei:
			msg_new_counter3.setVisibility(View.INVISIBLE);
			editor.putBoolean("is_msg_new_counter3", true);
			editor.commit();
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://www.02727.con.cn/mallshop/list.php?id=38");
			break;

		case R.id.ll_xianjin:
			msg_new_counter4.setVisibility(View.INVISIBLE);
			editor.putBoolean("is_msg_new_counter4", true);
			editor.commit();
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://www.02727.con.cn/mallshop/list.php?id=36");

			break;

		case R.id.ll_youhuiquan:
			msg_new_counter5.setVisibility(View.INVISIBLE);
			editor.putBoolean("is_msg_new_counter5", true);
			editor.commit();
			IhuiClientApi.getInstance(getActivity()).redirect(
					"http://www.02727.com.cn/mallshop/couponlist.php");
			break;

		case R.id.ll_zhuanqian:
			msg_new_counter1.setVisibility(View.INVISIBLE);
			editor.putBoolean("is_msg_new_counter1", true);
			editor.commit();
			if (null != mGameList && mGameList.size() > 0) {
				/*
				 * for (Adv adv : mGameList) { String link = adv.getLink(); link
				 * =
				 * IhuiClientApi.getInstance(getActivity()).addLinkParameter(link
				 * , "adv_phone_id", String.valueOf(adv.getAdv_phone_id()));
				 * IhuiClientApi.getInstance(getActivity()).redirect( link);
				 * break; }
				 */
				startActivity(new Intent(this.getActivity(),
						GameDownloadActivity.class));
			} else {
				IhuiClientApi.getInstance(getActivity()).redirect(
						"http://m.02727.cn:8080/webim/web_todaytask.jsp");
			}
			break;
		case R.id.ll_cardmakemone:
			startActivity(new Intent(this.getActivity(),
					CardMakeMoneyActivity.class));
			break;
		case R.id.ll_recommendmakemone:
			startActivity(new Intent(this.getActivity(),
					RecommendMakeMoneyActivity.class));
			break;

		default:
			break;
		}
	}

	@Override
	public void onSliderClick(BaseSliderView slider) {
		// TODO Auto-generated method stub

	}

}