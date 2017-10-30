package com.zpstudio.ui.adv.fragment;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.app.MainApplication;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.ui.adv.LockScreenAdv;
import com.zpstudio.ui.adv.view.DSLView;
import com.zpstudio.ui.adv.view.Unlock;
import com.zpstudio.ui.adv.view.VerticalViewPager;

public class Fragment2 extends Fragment {
	
	private View view;
	private DSLView dslView;
	private LockScreenAdv mActivity;
	private Handler mHandler;
	private Animation alphaAnimation;
	private ImageView iv_leff;
	private ImageView iv_right;
	private Animation alphaAnimation2;
	private VerticalViewPager verticalViewPager;
	private DummyAdapter adapter;
	private ImageView iv_top;
	private ImageView iv_butto;
	private TextView tv_mone;
	private static final float MIN_SCALE = 1f;
	private static final float MIN_ALPHA = 1f;
	public static List<Adv> advs=new ArrayList<Adv>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view==null) {
			view=inflater.inflate(R.layout.layout2,container, false);
			initView();
		}
		ViewGroup parent = (ViewGroup) view.getParent();  
	    if (parent != null) {  
	       parent.removeView(view);  
	    }   
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (LockScreenAdv) activity;
		mHandler=mActivity.getHandler();
	}
	
	
	private void onUnlock(boolean bRight)
	{
		MainApplication.is=verticalViewPager.getCurrentItem()+MainApplication.is+1;
		Message msg = new Message();
		msg.what = bRight?LockScreenAdv.MSG_ON_UNLOCK_RIGHT:LockScreenAdv.MSG_ON_UNLOCK_LEFT;
		msg.obj = null;
		if (advs.size()>0) {
			msg.obj = advs.get(verticalViewPager.getCurrentItem());
		}
		mHandler.sendMessage(msg);
		adapter.notifyDataSetChanged();
	}

	private void initView() {
		// TODO Auto-generated method stub
		iv_leff = (ImageView) view.findViewById(R.id.iv_leff);
		iv_right = (ImageView) view.findViewById(R.id.iv_right);
		iv_top = (ImageView) view.findViewById(R.id.iv_top);
		iv_butto = (ImageView) view.findViewById(R.id.iv_butto);
		tv_mone= (TextView) view.findViewById(R.id.tv_mone);

		alphaAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.alpha_anim);
		alphaAnimation2 = AnimationUtils.loadAnimation(getActivity(),
				R.anim.alpha_anim);
		iv_leff.startAnimation(alphaAnimation);
		iv_right.startAnimation(alphaAnimation2);

		dslView = (DSLView) view.findViewById(R.id.dSLView1);
		dslView.setUnlock(new Unlock() {

			@Override
			public void leff() {
				// TODO Auto-generated method stub
				if (advs.size()==0) {
					Message msg = new Message();
					msg.obj = 1;
					msg.what = 3;
					mHandler.sendMessage(msg);
				}else {
					onUnlock(false);
				}
			}

			@Override
			public void Right() {
				// TODO Auto-generated method stub
				onUnlock(true);
			}
		});

		verticalViewPager = (VerticalViewPager) view
				.findViewById(R.id.verticalviewpager);
		advs.clear();
		List<Adv> readyList = Adv.getAll(getActivity());
		for (int i = 0; i < readyList.size(); i++) {
			advs.add(readyList.get((i + MainApplication.is + 1)% readyList.size()));
			if (i==0) {
				if (advs.get(0).getPrice()==0) {
					dslView.Replace(advs.get(0).getPrice(), true);
					tv_mone.setText("");
				}else {
					dslView.Replace(advs.get(0).getPrice(), false);
					tv_mone.setText("+"+((double)advs.get(0).getPrice())/100);
				}
			}
		}
		adapter = new DummyAdapter(getActivity().getSupportFragmentManager());
		verticalViewPager.setAdapter(adapter);
		verticalViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
			@Override
			public void transformPage(View view, float position) {
				int pageHeight = view.getHeight();

				if (position < -1) { // [-Infinity,-1)
					// This page is way off-screen to the left.
//					view.setAlpha(0);

				} else if (position <= 1) { // [-1,1]
					if (position < 0) {
						view.setTranslationY(0);
					} else {
						view.setTranslationY(pageHeight * -position);
					}

				} else { // (1,+Infinity]
					// This page is way off-screen to the right.
//					view.setAlpha(0);
				}
			}
		});
		verticalViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if (arg0==0) {
					iv_top.setVisibility(View.VISIBLE);
					iv_butto.setVisibility(View.INVISIBLE);
				}else if (advs.size()-1==arg0) {
					iv_top.setVisibility(View.INVISIBLE);
					iv_butto.setVisibility(View.VISIBLE);
				}else {
					iv_top.setVisibility(View.VISIBLE);
					iv_butto.setVisibility(View.VISIBLE);
				}
				
				if (advs.get(arg0).getPrice()==0) {
					Log.e("222", "1"+advs.get(arg0).getPrice());
					dslView.Replace(advs.get(arg0).getPrice(), true);
					tv_mone.setText("");
				}else {
					dslView.Replace(advs.get(arg0).getPrice(), false);
					tv_mone.setText("+"+((double)advs.get(arg0).getPrice())/100);
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		IhuiClientApi.getInstance(getActivity()).refreshHangOnAdvAsync();
		
	}
	
	public class DummyAdapter extends FragmentPagerAdapter {

		public DummyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			if (advs.size()==0) {
				return 1;
			}
			return advs.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
//			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				Toast.makeText(getActivity(), "PAGE 1", 1000).show();
				return "PAGE 1";
			case 1:
				Toast.makeText(getActivity(), "PAGE 2", 1000).show();
				return "PAGE 2";
			case 2:
				Toast.makeText(getActivity(), "PAGE 3", 1000).show();
				return "PAGE 3";
			}
			return null;
		}

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
			
			ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
			if (advs.size()!=0) {
				ImageLoader.getInstance().displayImage(advs.get(getArguments().getInt(ARG_SECTION_NUMBER)-1).getContent(), imageView);
				Log.e("111", getArguments().getInt(ARG_SECTION_NUMBER)+"");
			}else {
				imageView.setBackgroundResource(R.drawable.com_hzjstudio__ihui_frontpage);
			}
			
//			TextView textView = (TextView) rootView.findViewById(R.id.textview);
//			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}

	}

}
