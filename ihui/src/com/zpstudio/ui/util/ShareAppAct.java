package com.zpstudio.ui.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zpstudio.R;

public class ShareAppAct extends Activity{
	private static final String TAG = "ShareAppAct";
	private static final String EXTRA_TEXT = "text";
	private ListView listView;
    private java.util.List<ResolveInfo> listApp;
    
    private String text = "";
    
    public static void share(Activity activity , int requestCode ,
    		String text)
    {
    	/* 系统调用方式
    	 */
    	/*
    	Intent share = new Intent(android.content.Intent.ACTION_SEND);
	    share.setType("text/plain");
	    share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	    
	    // all share
		share.putExtra(Intent.EXTRA_SUBJECT,  activity.getString(R.string.ihui));
		share.putExtra(Intent.EXTRA_TEXT, text);  
		activity.startActivity(Intent.createChooser(share, activity.getString(R.string.share)));
		*/
		
    	/* */
    	
    	/* sharedSDK方式
    	 * 
    	OnekeyShare oks = new OnekeyShare(); 
		oks.setText(text);
		oks.show(activity);
		*/   
    	
    	/*自定义方式*/
    	/*
    	Intent intent = new Intent(activity , ShareAppAct.class);
    	intent.putExtra(EXTRA_TEXT, text);
    	activity.startActivityForResult(intent, requestCode);
    	*/
    	
    	/* popup menu */
    	Intent intent = new Intent(activity , SharePopupMenuActivity.class);
    	intent.putExtra(EXTRA_TEXT, text);
    	activity.startActivity(intent);
    	
    }
    
    
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shareapp_act);
        Intent i = getIntent();
        text = i.getStringExtra(EXTRA_TEXT);
        
        listView = (ListView) findViewById(R.id.listView);

        listApp = filter(showAllShareApp());
        if (listApp != null) {
            listView.setAdapter(new MyAdapter());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                	doShare(listApp.get(position));
                }
            });
        }
    }

    private void doShare(ResolveInfo appInfo) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        if (appInfo != null) {
            sendIntent
                    .setComponent(new ComponentName(
                            appInfo.activityInfo.packageName,
                            appInfo.activityInfo.name));
        }
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        
        
    }

    private java.util.List<ResolveInfo> showAllShareApp() {
        java.util.List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        PackageManager pManager = getPackageManager();
        mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    }

    private List<ResolveInfo> filter(List<ResolveInfo> list)
    {
    	List<ResolveInfo> filtered = new ArrayList<ResolveInfo>();
    	for(ResolveInfo info : list)
    	{
    		Log.i(TAG , info.activityInfo.packageName);
    		if(   info.activityInfo.packageName.contains("tencent")
    			||info.activityInfo.packageName.contains("mms")
    			||info.activityInfo.packageName.contains("email")
    			||info.activityInfo.packageName.contains("mms"))
    		{
    			filtered.add(info);
    		}
    	}
    	return filtered;
    }
    class MyAdapter extends BaseAdapter {

        PackageManager pm;
        public MyAdapter(){
            pm=getPackageManager();
        }


        @Override
        public int getCount() {
            return listApp.size();
        }

        @Override
        public Object getItem(int position) {
            return listApp.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ShareAppAct.this).inflate(R.layout.shareapp_item, parent, false);
                holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
                holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tvPackageName = (TextView) convertView.findViewById(R.id.tv_app_package_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ResolveInfo appInfo = listApp.get(position);
            holder.ivLogo.setImageDrawable(appInfo.loadIcon(pm));
            holder.tvAppName.setText(appInfo.loadLabel(pm));
            holder.tvPackageName.setText(appInfo.activityInfo.packageName);

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivLogo;
        TextView tvAppName;
        TextView tvPackageName;
    }

   
}
