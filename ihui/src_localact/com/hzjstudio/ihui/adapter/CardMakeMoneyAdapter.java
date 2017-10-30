package com.hzjstudio.ihui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjstudio.ihui.utils.TextProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Adv;

public class CardMakeMoneyAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<Adv> advList;

	public CardMakeMoneyAdapter(Context context, List<Adv> advList){
		// XXX Auto-generated constructor stub
		this.advList=advList;
		this.mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return advList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewDetails holder;
        if(convertView == null){
            holder = new ViewDetails();
            convertView = mInflater.inflate(R.layout.adapter_cardmakemoney, null);
            holder.iv_icon=(ImageView)convertView.findViewById(R.id.iv_icon);
            holder.tv_desc=(TextView)convertView.findViewById(R.id.tv_desc);
            holder.tv_xiazaisong=(TextView)convertView.findViewById(R.id.tv_xiazaisong);
            holder.tv_action=(TextView)convertView.findViewById(R.id.tv_action);
            convertView.setTag(holder);
        }else{
        	holder = (ViewDetails)convertView.getTag();
        }
        Adv adv=advList.get(position);
        holder.tv_desc.setText(adv.getDes());
        holder.tv_action.setText("+"+adv.getPrice()+"元");
        holder.tv_xiazaisong.setText(adv.getDesc1()+"");
        ImageLoader.getInstance().displayImage(adv.getContent(), holder.iv_icon);
        
        return convertView;
		
        
	}
	static class ViewDetails{

		public TextView tv_action;
		public TextView tv_xiazaisong;
		public TextView tv_desc;
		public ImageView iv_icon;
		public TextProgressBar textProgressBar;
		
    }
}
