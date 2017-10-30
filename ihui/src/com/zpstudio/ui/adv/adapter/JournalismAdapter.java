package com.zpstudio.ui.adv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.ui.adv.fragment.Journalism;

public class JournalismAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<Journalism>  list;
	private int mScreenWidth;

	public JournalismAdapter(List<Journalism>  list,Context context, int mScreenWidth){
		// XXX Auto-generated constructor stub
		this.list=list;
		this.mScreenWidth=mScreenWidth;
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
            convertView = mInflater.inflate(R.layout.adapter_journalism, null);
            holder.rl_tou=(RelativeLayout)convertView.findViewById(R.id.rl_tou);
            holder.rl_ti=(RelativeLayout)convertView.findViewById(R.id.rl_ti);
            holder.headlines_src=(ImageView)convertView.findViewById(R.id.headlines_src);
            LayoutParams para = (LayoutParams) holder.headlines_src.getLayoutParams();  
            para.height = mScreenWidth/36*19;//一屏幕显示8行  
            para.width = mScreenWidth;//一屏显示两列  
            holder.headlines_src.setLayoutParams(para);
            holder.headlines_text=(TextView)convertView.findViewById(R.id.headlines_text);
            
            holder.iv_src=(ImageView)convertView.findViewById(R.id.iv_src);
            holder.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
            holder.tv_text=(TextView)convertView.findViewById(R.id.tv_text);
            convertView.setTag(holder);
        }else{
        	holder = (ViewDetails)convertView.getTag();
        }
        if (position==0) {
        	holder.rl_tou.setVisibility(View.VISIBLE);
        	holder.rl_ti.setVisibility(View.GONE);
        	holder.headlines_text.setText(list.get(0).getTitles());
    		ImageLoader.getInstance().displayImage(list.get(0).getNews_pic(), holder.headlines_src);
		}else {
			holder.rl_tou.setVisibility(View.GONE);
			holder.rl_ti.setVisibility(View.VISIBLE);
			holder.tv_name.setText(list.get(position).getTitles());
	        holder.tv_text.setText("来源："+list.get(position).getSource());
	        ImageLoader.getInstance().displayImage(list.get(position).getNews_pic(), holder.iv_src);
		}
        
        return convertView;
		
        
	}
	static class ViewDetails{

		public TextView headlines_text;
		public ImageView headlines_src;
		public RelativeLayout rl_ti;
		public RelativeLayout rl_tou;
		public TextView tv_text;
		public TextView tv_name;
		public ImageView iv_src;
		
    }
}
