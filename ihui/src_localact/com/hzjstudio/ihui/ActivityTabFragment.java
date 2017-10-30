package com.hzjstudio.ihui;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.core.SearchResult;
//import com.baidu.mapapi.search.geocode.GeoCodeResult;
//import com.baidu.mapapi.search.geocode.GeoCoder;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.SoftwareVersion;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.Indication;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnIndications;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnJournalismResult;
import com.zpstudio.ui.adv.fragment.Journalism;
import com.zpstudio.util.SystemUtil;
import com.zpstudio.widget.RedPointImageView;


public class ActivityTabFragment extends FragmentActivity 
//implements OnGetGeoCoderResultListener 
{
	static final String TAG = "ActivityTabFragment";
	static final int KEY_NORMAL = 0;
	static final int KEY_SELECTED = 1;
	private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	
	/**Fragment3.class,
	 * {R.drawable.icon_meassage_nor,R.drawable.icon_meassage_sel},
	 * R.string.fujin, 
	 */
	
	private Class fragmentArray[] = {
									 Fragment1.class,
									 Fragment2.class,
									 Fragment4.class,
									 };
	
//	private int [][]mImageViewArray = {
//									 {R.drawable.icon_selfinfo_nor,R.drawable.icon_selfinfo_sel},
//									 {R.drawable.icon_square_nor,R.drawable.icon_square_sel},
//									 {R.drawable.com_hzjstudio__ihui_icon_more_nor,R.drawable.com_hzjstudio__ihui_icon_more_sel}
//									 };

	private int [][]mImageViewArray = {
			 						{R.drawable.com_hzjstudio__ihui_shouyi_nor,R.drawable.com_hzjstudio__ihui_shouyi_sel},
			 						{R.drawable.com_hzjstudio__ihui_duobao_nor,R.drawable.com_hzjstudio__ihui_duobao_sel},
			 						{R.drawable.com_hzjstudio__ihui_wode_nor,R.drawable.com_hzjstudio__ihui_wode_sel}
			 						};
	
	private int mTextviewArray[] = {
									   R.string.shouye,
									   R.string.indiana, 
									   R.string.wode, 
									   };
	int mTextColor[] = {R.color.balck_gray ,R.color.orange1 };
	private Button mBtnTitleReback = null;
	private TextView mTitleText = null;
	
	ListenerOnIndications indicatonsListener = new ListenerOnIndications() {

		@Override
		public void onSuccess(List<Indication> indicationList) {
			for(Indication indication : indicationList)
			{
				if(indication.getName().equals(IhuiClientApi.INDICATION_NAME_GIFT))
				{
					if(indication.isbNeed())
					{
						showRedPoint(1 , true);
					}
					else
					{
					}
				}
				else if(indication.getName().equals(IhuiClientApi.INDICATION_NAME_BATERPOST))
				{
					if(indication.isbNeed())
					{
						//showRedPoint(2 , true);
					}
					else
					{
					}
				}
			}
		}

		@Override
		public void onFail(String msg) {
		}

	};
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true; // 是否首次定位
//	GeoCoder mSearch = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        User user = IhuiClientApi.getInstance(this).getUser();
        Log.i(TAG , "onCreate");
        if(null == user)
        {
        	Intent localIntent = new Intent(this, Main.class);
        	startActivity(localIntent);
        	finish();
        }
        else
        {
        	setContentView(R.layout.com_hzjstudio__ihui_fragmenttabhost);
        	initView();
        }
        
//        CharSequence titleLable = "地理编码功能";
//		setTitle(titleLable);
//        mSearch = GeoCoder.newInstance();
//		mSearch.setOnGetGeoCodeResultListener(this);
        
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        
    }
	private void initView(){
		 mBtnTitleReback = (Button)findViewById(R.id.title_reback_btn);
	     mTitleText      = (TextView)findViewById(R.id.title_text);
	        
		layoutInflater = LayoutInflater.from(this);
				
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),R.id.realtabcontent);	
		
		int count = fragmentArray.length;	
				
		for(int i = 0; i < count; i++){	
			View view = createTabItemView(i , 0 == i?KEY_SELECTED:KEY_NORMAL);
			TabSpec tabSpec = mTabHost.newTabSpec(getString(mTextviewArray[i])).setIndicator(view);
			
			mTabHost.addTab(tabSpec, fragmentArray[i], null);

		}
		
		mTabHost.setOnTabChangedListener(
				new OnTabChangeListener() {

					@Override
					public void onTabChanged(String tag) {
						// TODO Auto-generated method stub
						for(int i = 0 ;i < mTextviewArray.length ; i ++)
						{
							String curTag = getString(mTextviewArray[i]);
							View view = mTabHost.getTabWidget().getChildAt(i);
							if(tag.equals(curTag))
							{
								
								refreshTabIndication(view , KEY_SELECTED);
								showRedPoint(i , false);
							}
							else
							{
								refreshTabIndication(view , KEY_NORMAL);
							}
						}
					}
		});
		
		IhuiClientApi.getInstance(this).startCommonDeamon();
		IhuiClientApi.getInstance(this).registerIndicationsListener(indicatonsListener);
		SoftwareVersion.softwareUpdate(this);
	}
	private void showRedPoint(int index , boolean bShow)
	{
		View view = mTabHost.getTabWidget().getChildAt(index);
		RedPointImageView imageView = (RedPointImageView) view.findViewById(R.id.imageview);
		imageView.setRightText(bShow?"":null);
	}
	private void refreshTabIndication(View view , int key)
	{
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(((int [])imageView.getTag())[key]);
		
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setTextColor(getResources().getColor(((int [])textView.getTag())[key]));
	}
	
	private View createTabItemView(int index , int key){
		View view = layoutInflater.inflate(R.layout.com_hzjstudio__ihui_tab_item_view, null);
	
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index][key]);
		imageView.setTag(mImageViewArray[index]);
		
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);
		textView.setTextColor(getResources().getColor(mTextColor[key]));
		textView.setTag(mTextColor);
		
		return view;
	}
	
	public void onBack(View v) {
		 SystemUtil.triggerReturn();
	}
	
	public void setBarTitle(int title)
	{
		mTitleText.setText(SystemUtil.getResourceString(this,title));
	}
	
	public void enableReback(boolean enable)
	{
		if(!enable)
		{
			mBtnTitleReback.setVisibility(View.GONE);
		}
		else
		{
			mBtnTitleReback.setVisibility(View.VISIBLE);
		}
	}
    
	public void onDestroy(){
		IhuiClientApi.getInstance(this).stopCommonDeamon();
		super.onDestroy();
	}

	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
        	moveTaskToBack(true);
            return true;
        } 
        else 
        {
            return super.onKeyDown(keyCode, event);
        }
    }
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ) {
                return;
            }
            
            if (isFirstLoc) {
                isFirstLoc = false;
//                LatLng ll = new LatLng(location.getLatitude(),
//                        location.getLongitude());
                Log.e("111", location.getLatitude()+":"+
                        location.getLongitude());
              mLocClient.stop();
              getJournalismList(location.getLatitude()+","+
                      location.getLongitude());
//                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
//				.location(ll));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    
    
    public void getJournalismList(String location)
	{
    	
    	AsyncHttpClient mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("ak", "b9axQ1jzyQrDtl7qyTKDX4ew");
//		params.put("callback", "renderReverse");
		params.put("location", location);
		params.put("output", "json");
		params.put("pois", "1");
		
		mClient.get(this, "http://api.map.baidu.com/geocoder/v2/?", 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
//				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
//					JournalismResult result = mGson.fromJson(new String(responseBody), JournalismResult.class);
					JSONObject object=null;
					try {
						String json = new String(responseBody , "UTF-8");
						Log.e("111", "222"+""+json);
						object = new JSONObject(json);
						JSONObject  data=null;
						JSONObject  addressComponent=null;
						if (object.getString("status").equals("0")) {
							data=object.getJSONObject("result");
							addressComponent=data.getJSONObject("addressComponent");
							Log.e("111", "222"+""+addressComponent.getString("country")+addressComponent.getString("province")+addressComponent.getString("city"));
							getAddressupdat(addressComponent.getString("country"), addressComponent.getString("province"), addressComponent.getString("city"), addressComponent.getString("district"));
						}else {
							isFirstLoc = true;
							mLocClient.start();
						}
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					isFirstLoc = true;
					mLocClient.start();
				}
				
			}

		});
	}
    
    public void getAddressupdat(final String country,final String province,final String city,String district)
	{
    	final User user=User.loadFromLocal(ActivityTabFragment.this);
    	AsyncHttpClient mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("country", country);
		params.put("province", province);
		params.put("city", city);
		params.put("district",district);
		params.put("phone_number", user.getPhone_number()+"");
		
		mClient.get(this, "http://m.02727.cn:8080/webim/do_addressupdate.jsp", 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
//				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					JSONObject object=null;
					try {
						String json = new String(responseBody , "UTF-8");
						Log.e("111", "333"+""+json);
						object = new JSONObject(json);
						
						user.setCountry(country);
						user.setProvince(province);
						user.setCity(city);
						user.saveToLocal(ActivityTabFragment.this, user, null);
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					isFirstLoc = true;
					mLocClient.start();
				}
				
			}

		});
	}
    
}

