package com.zpstudio.ui.adv.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.ui.adv.fragment.Journalism;
import com.zpstudio.ui.adv.fragment.JournalismActivity;

public class HeadlinesViewPagerAdapter extends PagerAdapter { 
	
	private List<Journalism> listIndexs;
	private LayoutInflater mInflater;
	private int mScreenWidth;
	private Context context;
	
	public HeadlinesViewPagerAdapter(List<Journalism> list,Context context, int mScreenWidth) {
		// XXX Auto-generated constructor stub
		this.context=context;
		this.mInflater=LayoutInflater.from(context);
		this.listIndexs=list;
		this.mScreenWidth=mScreenWidth;
	}
	
	@Override
	public int getCount() {
		if (listIndexs.size()>0) {
			return 1;
		}
		return listIndexs.size();
	} 
	
	@Override
	public Object instantiateItem(View arg0, final int arg1) {
		View view = mInflater.inflate(R.layout.adapter_headlines, null);
		ImageView src=(ImageView) view.findViewById(R.id.headlines_src);
		
		TextView text=(TextView) view.findViewById(R.id.headlines_text);
		text.setText(listIndexs.get(0).getTitles());
		LayoutParams para = (LayoutParams) src.getLayoutParams();  
        para.height = mScreenWidth/36*19;//一屏幕显示8行  
        para.width = mScreenWidth;//一屏显示两列  
        src.setLayoutParams(para);
		ImageLoader.getInstance().displayImage(listIndexs.get(0).getNews_pic(), src);
		((ViewPager) arg0).addView(view);
		return view;
	}  
	
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}  
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;          
	} 
	
	@Override 
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		
	}    
	
	@Override
	public Parcelable saveState() {
		return null;
	}   
	
	@Override
	public void startUpdate(View arg0) {
		
	}           
	@Override
	public void finishUpdate(View arg0) {
		
	} 
}
