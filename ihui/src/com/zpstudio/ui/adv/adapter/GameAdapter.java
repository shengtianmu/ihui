package com.zpstudio.ui.adv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hzjstudio.ihui.utils.TextProgressBar;
import com.zpstudio.R;

public class GameAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<String>  list;

	public GameAdapter(List<String>  list,Context context){
		// XXX Auto-generated constructor stub
		this.list=list;
		this.mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
            convertView = mInflater.inflate(R.layout.adapter_game, null);
            convertView.setTag(holder);
        }else{
        	holder = (ViewDetails)convertView.getTag();
        }
//        holder.textProgressBar.setText("下载");
        return convertView;
		
        
	}
	static class ViewDetails{

		public TextProgressBar textProgressBar;
		
    }
}
