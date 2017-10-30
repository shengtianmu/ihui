package com.zpstudio.datacenter.db.op;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.Gson;
import com.hzjstudio.ihui.DownloadFileModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zpstudio.R;
import com.zpstudio.app.MainApplication;
import com.zpstudio.bbs.bean.ImageInfo;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.AppSetting;
import com.zpstudio.datacenter.db.Bater;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.datacenter.db.Gift;
import com.zpstudio.datacenter.db.Indiana;
import com.zpstudio.datacenter.db.Location;
import com.zpstudio.datacenter.db.SoftwareVersion;
import com.zpstudio.datacenter.db.TurnTableGame;
import com.zpstudio.datacenter.db.TurnTableResult;
import com.zpstudio.datacenter.db.User;
import com.zpstudio.datacenter.db.WHotel;
import com.zpstudio.datacenter.db.Wallet;
import com.zpstudio.datacenter.db.weixin.pay.PayReqEx;
import com.zpstudio.ui.adv.LockScreenService;
import com.zpstudio.ui.adv.fragment.Journalism;
import com.zpstudio.ui.game.puzzle.PuzzleGamePickerAct;
import com.zpstudio.ui.util.ActBrowser;
import com.zpstudio.ui.util.AddressPickerAct;
import com.zpstudio.ui.util.ShareAppAct;
import com.zpstudio.util.SystemUtil;

public class IhuiClientApi {
	static final String TAG = "IhuiClientApi";
	public static final int ADV_ACTION_TYPE_UNLOCK = 1;
	public static final int ADV_ACTION_TYPE_ATTENTION = 2;
	
	public static final long USER_FEATHER_BITMAP_SUPPORT_IOS = 1 ;
	public static final long USER_FEATHER_BITMAP_SUPPORT_ANDROID = 2 ;
	
	private static final String URL_DEBUG_INFO = "/webim/do_uploadlog.jsp";
	private static final String URL_REGISTER = "/webim/do_register.jsp";
	private static final String URL_GETAGENTLIST = "/webim/do_agentaddresslistget.jsp";
	private static final String URL_LOGIN = "/webim/do_login.jsp";
	private static final String URL_ANONYNOUSLOGIN ="/webim/do_anonymouslogin.jsp";
	private static final String URL_INVTERLINK ="/webim/do_inviterlink.jsp";
	private static final String URL_BINDPHONENUMBER="/webim/do_bindphonenumber.jsp";
	private static final String URL_createWeixinAdv = "/webim/do_weixinadvcreate.jsp";
	private static final String URL_PASSWORDCLAIM = "/webim/do_passwordclaim.jsp";
	private static final String URL_SMSVALIDATE = "/webim/do_smsvalidation.jsp";
	private static final String URL_GETUSERANDWALLET = "/webim/do_userandwalletget.jsp";
	private static final String URL_FIXEDADVLISTGET = "/webim/do_fixedadvlistget.jsp";
	private static final String URL_DYRECOMMENDSELLERS = "/webim/do_dyrecommendsellerget.jsp";
	private static final String URL_PUZZLEGAMELISTGET = "/webim/do_puzzlegamelistget.jsp";
	private static final String URL_PUZZLEGAMEWIN="/webim/do_puzzlegamewin.jsp";
	private static final String URL_USERINFOMODIFY="/webim/do_userinfomodify.jsp";
	private static final String URL_PORTRAITCHANGE = "/webim/do_portraitchange.jsp";
	private static final String URL_UPDATEWALLET = "/webim/do_updatewallet.jsp";
	private static final String URL_INDICATIONLISTGET = "/webim/do_indicationlistget.jsp";
	private static final String URL_PUBLISHBATER = "/bbs/do_baterpostadd.jsp";
	private static final String URL_UPLOADSINGLEFILE = "/bbs/do_uploadsinglefile.jsp";
	private static final String URL_GETBATERPOSTLIST = "/bbs/do_baterpostlistget.jsp";
	private static final String URL_GIFTLISTGET = "/webim/do_giftlistget.jsp";
	private static final String GLOBAL_PATH = Environment.getExternalStorageDirectory() + "/ihui/global/";
	private static final String URL_LUCKEYTURNTABLEGET = "/webim/do_luckturntableget.jsp";
	private static final String URL_LUCKEYTURNTABLEPLAY = "/webim/do_luckturntableplay.jsp";
	private static final String URL_DOTUISCOREADD="/webim/do_dotuiscoreadd.jsp";
	private static final String URL_DOWNLOADSCOREADD="/webim/do_downloadscoreadd.jsp";
	private static final String URL_INDIANALISTGET = "/webim/do_indianalistget.jsp";
	private static final String URL_WEIXINPREPAY = "http://02727.com.cn:8080/webim/do_weixinprepay.jsp";
	
	public static final String INDICATION_NAME_GIFT = "giftlistrefresh";
	public static final String INDICATION_NAME_BATERPOST = "baterpostlistrefresh";
	public static final int   HANGON_ADV_CLASS = 1;
	public static final int   HOMEPAGE_ADV_CLASS = 2;
	public static final int   MALLSHOP_ADV_CLASS=3;
	public static final int   NEARBY_ADV_CLASS=4;
	public static final int   GAME_ADV_CLASS=5;
	
	public static final String INDIANA_TYPE_expose_time_desc= "expose_time_desc";
	public static final String INDIANA_TYPE_opening = "opening";
	public static final String INDIANA_TYPE_overplus_desc = "overplus_desc";
	public static final String INDIANA_TYPE_overplus_asc = "overplus_asc";
	public static final String INDIANA_TYPE_people_desc = "people_desc";
	public static final String INDIANA_TYPE_people_asc = "people_asc";
	public static final String INDIANA_TYPE_indiana_id_desc = "indiana_id_desc";
	public static final String INDIANA_TYPE_all_people_desc = "all_people_desc";
	public static final String INDIANA_TYPE_all_people_asc = "all_people_asc";
	
	public static final String FIXEDADV_PREFIX = "fixedadv_";
	
	private static final int INDICATION_TITLE_ID_ANDROID = 21;
	private static final int PAGE_END = -1;
	
	public static final String OPEN_WEIXIN_APP_ID = "wx41e6ecf30fa87a21";
	
	private static final Map<String, Class<?>> Uri2Activity = new HashMap<String, Class<?>>();
	static{
    }
	
	private Map<String, PageSizeInfo> mPageSizeInfoMap = new HashMap<String, PageSizeInfo>();
	
	
	int mGiftListPage = 0;
	int mGiftListSize  = 10; 
	long mGiftListRefreshUnixTime ;
	
	int mBaterListPage = 0;
	int mBaterListSize  = 10; 
	long mBaterListRefreshUnixTime ;
	
	Context mContext = null;
	Handler mHandler = new Handler(){  
        public void handleMessage(Message msg) {   
        	Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_LONG).show();
            super.handleMessage(msg);   
       }   
	};  
	AsyncHttpClient mClient = null;
	Gson mGson = null;
	public static IhuiClientApi mInstance = null;
	public Location defaultLocation = null;
	public LocationClient mLocationClient;
	public BDLocationListener mMyLocationListener;
	
	String msAddress="上海。。。。";
	double mLatitude=31.27014;
	double mLongitude=121.530806;
	List<ListenerOnGPS> mListenerOnGPSList = new ArrayList<ListenerOnGPS>();
	List<ListenerOnUpdateUserAndWallet> mListenerOnUpdateUserAndWalletList = new ArrayList<ListenerOnUpdateUserAndWallet>();
	List<ListenerOnBaterChanged> mListenerOnBaterChangedList = new ArrayList<ListenerOnBaterChanged>();
	List<ListenerOnIndications> mListenerOnIndications = new ArrayList<ListenerOnIndications>();
	
	private long dotuiScoreAdvPhoneId = 0;
	
	private IWXAPI mIWXAPI;
	public class PageSizeInfo
	{
		public int page;
		public int size;
		public long refreshUnixTime;
	}
	IhuiClientApi(Context context)
	{
		mContext = context;
		mClient = new AsyncHttpClient();
		mGson = new Gson();
		defaultLocation = new Location();
		defaultLocation.setAddress(msAddress);
		defaultLocation.setLat(mLatitude);
		defaultLocation.setLng(mLongitude);
		
		mLocationClient = MainApplication.gLocationClient;
		
		mMyLocationListener = new BDLocationListener(){
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();
				msAddress = location.getAddrStr();
				Location.save(mContext, mLongitude, 
						mLatitude, 
						msAddress);
				
				Location loc = Location.load(mContext);
				for(ListenerOnGPS listener : mListenerOnGPSList)
				{
					listener.onIndication(loc);
				}
				
				stopLocation();
			}};
			
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("gcj02");
		int span=1000;
		
		option.setScanSpan(span);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		
		mIWXAPI = WXAPIFactory.createWXAPI(mContext, OPEN_WEIXIN_APP_ID);
		mIWXAPI.registerApp(OPEN_WEIXIN_APP_ID);
	}
	public static IhuiClientApi getInstance(Context context)
	{
		if(null == mInstance) 
		{
			mInstance = new IhuiClientApi(context);
		}
		return mInstance;
	}
	public IWXAPI getWeixinPayApi()
	{
		return mIWXAPI;
	}
	public void uploadLog(String who , String log)
	{
		RequestParams params = new RequestParams();
		params.put("who", who);
		params.put("description", log);
	
		mClient.get(mContext , Config.getFullUrl(mContext, URL_DEBUG_INFO ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					postMessage(new String(responseBody));
				}
				else
				{
				}
				
			}
			
		});
	}
	
	
	public void DoBindphonenumber(final String  phone_number,final String  invitation_code,final ListenerOnLogin listener)
	{	
		User user = getUser();
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("old", user.getPhone_number()+"");
		params.put("phone_number", phone_number+"");
		params.put("invitation_code", invitation_code+"");
		
//		old:系统生成的phone_number
//		phone_number：要绑定的电话号码
		
		Log.e("111", params.toString()+":");
		
		mClient.post(mContext , Config.getFullUrl(mContext, URL_BINDPHONENUMBER ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					String json = new String(responseBody);
					Log.i(TAG , json);
					UserAndWalletResult result = mGson.fromJson(json, UserAndWalletResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.userAndWallet.me , result.userAndWallet.wallet);
						updateUser(result.userAndWallet.me);
						updateWallet(result.userAndWallet.wallet);
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	
	public void DoAnonymouslogin(final ListenerOnLogin listener)
	{	
		
		int versionCode = SoftwareVersion.getVersionCode(mContext);
		String versionName = SoftwareVersion.getVersion(mContext);
		String model       = android.os.Build.MODEL;
		String manufacturer= android.os.Build.MANUFACTURER;
		String imei        = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		String system_version        =android.os.Build.VERSION.RELEASE;
		
		WindowManager mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);    
        int width = mWindowManager.getDefaultDisplay().getWidth();    
        int height = mWindowManager.getDefaultDisplay().getHeight(); 
		
		
		
		
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("country", "中国");
		params.put("province", "上海市");
		params.put("city", "上海市");
		params.put("district", "杨浦区");
		params.put("model_number", model+"");
		params.put("display_resolution", width+"*"+height);
		params.put("system_version", system_version+"");
		Log.e("111", params.toString()+":");
		
		mClient.post(mContext , Config.getFullUrl(mContext, URL_ANONYNOUSLOGIN ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					String json = new String(responseBody);
					Log.e(TAG , json);
					UserAndWalletResult result = mGson.fromJson(json, UserAndWalletResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.userAndWallet.me , result.userAndWallet.wallet);
						updateUser(result.userAndWallet.me);
						updateWallet(result.userAndWallet.wallet);
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	
	
	public void setInviterlink(String invitation_code,final ListenerOnLogin listener)
	{	User user = getUser();
		RequestParams params = new RequestParams();
		params.put("invitation_code", invitation_code+"");
//		params.put("phone_number", user.getPhone_number());
		
		Log.e("111", params.toString()+":");
		
		mClient.post(mContext , "http://m.02727.cn:8080/webim/do_inviterlink.jsp?phone_number="+user.getPhone_number(), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					String json = new String(responseBody);
					Log.e(TAG , json);
					UserAndWalletResult result = mGson.fromJson(json, UserAndWalletResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.userAndWallet.me , result.userAndWallet.wallet);
						updateUser(result.userAndWallet.me);
						updateWallet(result.userAndWallet.wallet);
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	
	
	
	public void getJournalismList(int is,final ListenerOnJournalismResult listener)
	{
		User user = getUser();
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("page", is+"");
		params.put("phone_number", user.getPhone_number());
//		page=1&page_size=10&phone_number=18374912449
		Log.e("111", params.toString());		
		mClient.get(mContext, "http://www.02727.com.cn/nozzle/newlist.php?", 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
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
						JSONArray  data=null;
						JSONArray  ads=null;
						if (object.getString("status").equals("4")) {
							data=object.getJSONArray("data");
//							ads=object.getJSONArray("ads");
							
							List<Journalism> journalismList=new ArrayList<Journalism>();
							JSONObject object2=null;
							JSONObject object3=null;
							Journalism journalism=null;
							for (int i = 0; i < data.length(); i++) {
								object2=data.getJSONObject(i);
								journalism=new Journalism();
								journalism.setId(object2.getString("id"));
								journalism.setCreate_time(object2.getString("create_time"));
								journalism.setNews_pic(object2.getString("news_pic"));
								journalism.setSource(object2.getString("source"));
								journalism.setTitles(object2.getString("titles"));
								journalism.setTj(object2.getString("tj"));
								journalism.setLink(object2.getString("link"));
								
//								if (i < ads.length()) {
//									object3=ads.getJSONObject(i);
//									journalism.setAdmin_city(object3.getString("admin_city"));
//									journalism.setValid_end(object3.getString("valid_end"));
//									journalism.setContent(object3.getString("content"));
//									journalism.setLink(object3.getString("link"));
//									journalism.setCredit(object3.getString("credit"));
//									journalism.setIs(true);
//								}else {
//									journalism.setIs(false);
//								}
								
								journalismList.add(journalism);
							}
							listener.onSuccess(journalismList);
						}else {
							listener.onNoMore(object.getString("describtion"));
						}
						
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
//					if(4 == result.errorCode)
//					{	
//						Log.e("111", "223"+""+new String(responseBody));
//						Log.e("111", "223"+""+result.errorCode+":"+result.journalismList);
//						listener.onSuccess(result.journalismList);
//					}
//					else
//					{
//						Log.e("111", "224"+""+result.errorCode);
//						listener.onNoMore(result.description);
//					}
					
				}
				else
				{
					Log.e("111", "225"+""+statusCode);
					listener.onFail("fail");
				}
				
			}

		});
	}
	
	
	public static interface ListenerOnJournalismResult{
		public void onSuccess(List<Journalism> journalismList);
		public void onNoMore(String msg);
		public void onFail(String msg);
	}
	
	
	/**
	 * @return the dotuiScoreAdvPhoneId
	 */
	public long getDotuiScoreAdvPhoneId() {
		return dotuiScoreAdvPhoneId;
	}
	/**
	 * @param dotuiScoreAdvPhoneId the dotuiScoreAdvPhoneId to set
	 */
	public void setDotuiScoreAdvPhoneId(long dotuiScoreAdvPhoneId) {
		this.dotuiScoreAdvPhoneId = dotuiScoreAdvPhoneId;
	}
	public void createWeixinAdv(String url , final ListenerOnCreateWeixinAdv listener)
	{
		RequestParams params = new RequestParams();
		params.put("url", url);
	
		mClient.get(mContext , Config.getFullUrl(mContext, URL_createWeixinAdv ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					CreateWeixinAdvResult result = mGson.fromJson(new String(responseBody), CreateWeixinAdvResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.weixinAdv);
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	public void registerAsync(String phone_number,
			String sex,
			int    age,
			String password,
			String name ,
			String country,
			String province,
			String city ,
			String dynamic_password,
			final ListenerOnRegister listener
			)
	{
		RequestParams params = new RequestParams();
		//JSONObject params = new JSONObject(); 
		//List<NameValuePair> params = new ArrayList<NameValuePair>();
		int versionCode = SoftwareVersion.getVersionCode(mContext);
		String versionName = SoftwareVersion.getVersion(mContext);
		String model       = android.os.Build.MODEL;
		String manufacturer= android.os.Build.MANUFACTURER;
		String imei        = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		
		
		params.add("phone_number", phone_number);
		params.add("sex", sex);
		params.add("age", String.valueOf(age));
		params.add("password", password);
		params.add("name", name);
		params.add("country", country);
		params.add("province", province);
		params.add("city", city);
		params.add("district", "杨浦区");
		params.add("dynamic_password", dynamic_password);
		params.add("versionCode", String.valueOf(versionCode));
		params.add("versionName", versionName);
		params.add("model", model);
		params.add("manufacturer", manufacturer);
		params.add("imei", imei);
		params.add("feather_bitmap", String.valueOf(USER_FEATHER_BITMAP_SUPPORT_ANDROID));
		
		
		//StringEntity entity = new StringEntity(params.toString(), "utf-8");
		//mClient.get(mContext, Config.getFullUrl(mContext, URL_REGISTER ), entity, "application/x-www-form-urlencoded; charset=utf-8", new AsyncHttpResponseHandler(){
		mClient.get(mContext , Config.getFullUrl(mContext, URL_REGISTER ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					UserAndWalletResult result = mGson.fromJson(new String(responseBody), UserAndWalletResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.userAndWallet.me , result.userAndWallet.wallet);
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	public void loginAsync(final String phone_number , final String password , final ListenerOnLogin listener)
	{
		RequestParams params = new RequestParams();
		params.put("phone_number", phone_number);
		params.put("password", password);
		params.put("feather_bitmap", String.valueOf(USER_FEATHER_BITMAP_SUPPORT_ANDROID));
		
		mClient.post(mContext , Config.getFullUrl(mContext, URL_LOGIN ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					String json = new String(responseBody);
					Log.e(TAG , json);
					UserAndWalletResult result = mGson.fromJson(json, UserAndWalletResult.class);
					if(0 == result.errorCode)
					{
						if(result.userAndWallet.me.getPhone_number().equals(phone_number) && result.userAndWallet.me.getPassword().equals(password))
						{
							listener.onSuccess(result.userAndWallet.me , result.userAndWallet.wallet);
							updateUser(result.userAndWallet.me);
							updateWallet(result.userAndWallet.wallet);
						}
						else
						{
							listener.onFail("phone_number or password mismatch");
						}
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	public void getAgentListAsync(final ListenerOnAgentList listener)
	{
		RequestParams params = new RequestParams();
		
		mClient.get(mContext , Config.getFullUrl(mContext, URL_GETAGENTLIST ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					AgentListResult result = mGson.fromJson(new String(responseBody), AgentListResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.agentAddress);
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	public void smsValidateAsync(String phone_number , String dynamic_pswd )
	{
		smsValidateAsync(phone_number , dynamic_pswd , new ListenerOnSmsValidate(){

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				
			}});
	}
	public void smsValidateAsync(String phone_number , String dynamic_pswd , final ListenerOnSmsValidate listener)
	{
		RequestParams params = new RequestParams();
		params.put("phone_number" , phone_number);
		params.put("dynamic_pswd" , dynamic_pswd);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_SMSVALIDATE ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					Result result = mGson.fromJson(new String(responseBody), Result.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess();
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	public void passwordClaimAsync(String phone_number,
			String new_password,
			String dynamic_passowrd, 
			final ListenerOnPasswordClaim listener)
	{
		RequestParams params = new RequestParams();
		params.put("phone_number" , phone_number);
		params.put("new_password" , new_password);
		params.put("dynamic_passowrd" , dynamic_passowrd);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_PASSWORDCLAIM ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					Result result = mGson.fromJson(new String(responseBody), Result.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess();
						
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	public void getUserAndWalletAsync()
	{
		getUserAndWalletAsync(new ListenerOnUpdateUserAndWallet(){

			@Override
			public void onSuccess(User user, Wallet wallet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	public void getUserAndWalletAsync(final ListenerOnUpdateUserAndWallet listener)
	{
		RequestParams params = new RequestParams();
		params.put("phone_number" , getUser().getPhone_number());
		mClient.get(mContext , Config.getFullUrl(mContext, URL_GETUSERANDWALLET ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					UserAndWalletResult result = mGson.fromJson(new String(responseBody), UserAndWalletResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.userAndWallet.me, result.userAndWallet.wallet);
						updateUser(result.userAndWallet.me);
						updateWallet(result.userAndWallet.wallet);
						broadcaseUserAndWallet(result.userAndWallet.me , result.userAndWallet.wallet);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	public void refreshFixedAdvAsync(int iClass , boolean is, final ListenerOnGetFixedAdvList listener)
	{
		String type = FIXEDADV_PREFIX + iClass;
		PageSizeInfo info = mPageSizeInfoMap.get(type);
		if(null == info)
		{
			info = new PageSizeInfo();
			mPageSizeInfoMap.put(type, info);
		}
		info.page = 0;
		info.size = 10;
		
		info.refreshUnixTime = new Date().getTime()/1000;
		getFixedAdvListAsync(iClass ,info ,listener);
	}
	public void loadMoreFixedAdvAsync(int iClass , final ListenerOnGetFixedAdvList listener)
	{
		String type = FIXEDADV_PREFIX + iClass;
		PageSizeInfo info = mPageSizeInfoMap.get(type);
		if(null == info)
		{
			return ;
		}
		
		getFixedAdvListAsync(iClass , info , listener);
		
	}
	public void getFixedAdvListAsync(int iClass , final ListenerOnGetFixedAdvList listener)
	{
		refreshFixedAdvAsync(iClass ,true, listener);
	}
	public void getFixedAdvListAsync(int iClass , final PageSizeInfo info , final ListenerOnGetFixedAdvList listener)
	{
		boolean bValid = true;
		User user = getUser();
		
		if(PAGE_END == info.page)
		{
			bValid = false;
		}
		if(null == user)
		{
			bValid = false;
		}
		
		if(!bValid)
		{
			listener.onNoMore();
			return;
		}
		RequestParams params = new RequestParams();
		params.put("phone_number" , user.getPhone_number());
		params.put("iclass" , iClass);
		params.put("start" , info.page * info.size);
		params.put("size" , info.size);
		params.put("refreshtime" , info.refreshUnixTime);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_FIXEDADVLISTGET ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					Log.e("11", new String(responseBody));
					FixedAdvListResult result = mGson.fromJson(new String(responseBody), FixedAdvListResult.class);
					if(0 == result.errorCode)
					{
						if(result.advList.size() < info.size)
						{
							info.page = PAGE_END;
						}
						else
						{
							info.page ++;
						}
						listener.onSuccess(result.advList);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	public void refreshHangOnAdvAsync()
	{
		refreshFixedAdvAsync(IhuiClientApi.HANGON_ADV_CLASS,true,new ListenerOnGetFixedAdvList() {
			
			@Override
			public void onSuccess(List<Adv> advList){
				// TODO Auto-generated method stub
				Adv.deleteAll(mContext);
				Adv.insert(mContext, advList);
			}
			
			@Override
			public void onNoMore() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public void getDyRecommendSellersAsync(final ListenerOnGetDyRecommendSellers listener)
	{
		Location location = getLocation();
		RequestParams params = new RequestParams();
		params.put("phone_number" , getUser().getPhone_number());
		String lng,lat;
		lng = String.valueOf(location.getLng());
		lat = String.valueOf(location.getLat());
		params.put("lng" , lng);
		params.put("lat" , lat);
		
		mClient.get(mContext , Config.getFullUrl(mContext, URL_DYRECOMMENDSELLERS ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					DyRecommendSellerListResult result = mGson.fromJson(new String(responseBody), DyRecommendSellerListResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.sellers);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
		
	}
	
	public void modifyUserInfoAsync(String name , int age , String sex , 
			String country , String province , String city ,
			final ListenerOnUpdateUserAndWallet listener
			)
	{
		int versionCode = SoftwareVersion.getVersionCode(mContext);
		String versionName = SoftwareVersion.getVersion(mContext);
		String model       = android.os.Build.MODEL;
		String manufacturer= android.os.Build.MANUFACTURER;
		String imei        = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		User user = getUser();
		
		RequestParams params = new RequestParams();
		
		params.add("phone_number", user.getPhone_number());
		params.add("sex", sex);
		params.add("age", String.valueOf(age));
		params.add("password", user.getPassword());
		params.add("name", name);
		params.add("country", country);
		params.add("province", province);
		params.add("city", city);
		params.add("versionCode", String.valueOf(versionCode));
		params.add("versionName", versionName);
		params.add("model", model);
		params.add("manufacturer", manufacturer);
		params.add("imei", imei);
		
		mClient.get(mContext , Config.getFullUrl(mContext, URL_USERINFOMODIFY ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					UserAndWalletResult result = mGson.fromJson(new String(responseBody), UserAndWalletResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.userAndWallet.me , result.userAndWallet.wallet);
						broadcaseUserAndWallet(result.userAndWallet.me , result.userAndWallet.wallet);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
		
	}
	
	public void changePortraitAsync(String path , final ListenerOnPortraitChanged listener) {
		
		File content = null;
		content = new File(path);
		User user = getUser();
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		try {
			params.put("content", content);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showToast(path + " is not exist");
			return ;
		}
		String filename = user.getPhone_number();
		Random ran =new Random(System.currentTimeMillis());
		for (int i = 0; i < 10; i++) {
			filename =filename + ran.nextInt(100);
		} 
		params.put("phone_number", user.getPhone_number());
		params.put("filename",filename+".png");
		mClient.post(mContext, Config.getFullUrl(mContext, URL_PORTRAITCHANGE ), 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					PortraitChangeResult result = mGson.fromJson(new String(responseBody), PortraitChangeResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.me);
						getUserAndWalletAsync();
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}

		});
	}
	
	public void updateWalletAsync(long adv_phone_id , int action_type)
	{
		updateWalletAsync(adv_phone_id , action_type , new ListenerOnUpdateWallet(){

			@Override
			public void onSuccess(Wallet wallet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	public void updateWalletAsync(long adv_phone_id , int action_type ,
			final ListenerOnUpdateWallet listener)
	{
		User user = getUser();
		Location location = getLocation();
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("adv_phone_id", adv_phone_id);
		params.put("action_type", action_type);
		params.put("phone_number", user.getPhone_number());
		params.put("lng", location.getLng());
		params.put("lat", location.getLat());
		mClient.post(mContext, Config.getFullUrl(mContext, URL_UPDATEWALLET ), 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					UpdateWalletResult result = mGson.fromJson(new String(responseBody), UpdateWalletResult.class);
					if(0 == result.errorCode)
					{
						Wallet wallet = result.wallet;
						listener.onSuccess(result.wallet);
						if(wallet.getNotify_credit() > 0)
						{
							cancelNotificationAsync(wallet.getPhone_number().hashCode());
							
							sendNotificationAsync("" , 
									"恭喜您抢到" + SystemUtil.getYuan(wallet.getNotify_credit()) + "元红包",
									wallet.getPhone_number().hashCode() ,
									true);
							
						}
						else if(!TextUtils.isEmpty(wallet.getForceMsg()))
						{
							cancelNotificationAsync(wallet.getPhone_number().hashCode());
							sendNotificationAsync("" , 
									wallet.getForceMsg(),
									wallet.getPhone_number().hashCode() ,
									true);
						}
						getUserAndWalletAsync();
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}

		});
	}
	
	public void getIndicationsAsync(final ListenerOnIndications listener)
	{
		User user = getUser();
		Location location = getLocation();
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phone_number", user.getPhone_number());
		params.put("title_id", INDICATION_TITLE_ID_ANDROID);
		params.put("lng", location.getLng());
		params.put("lat", location.getLat());
		mClient.post(mContext, Config.getFullUrl(mContext, URL_INDICATIONLISTGET ), 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					IndicationsResult result = mGson.fromJson(new String(responseBody), IndicationsResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.indicationList);
						
						notifyIndicationsListeners(result.indicationList);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}

		});
	}
	
	public void uploadSingleFileAsync(String uri , final ListenerOnUploadSingleFile listener)
	{
		RequestParams params = new RequestParams();
		try {
				
			File f = new File(new URI(uri.toString()));
			params.put("upload" , f);
				
		} 
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		mClient.post(mContext, Config.getFullUrl(mContext, URL_UPLOADSINGLEFILE ), 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail(arg3.getMessage());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					UploadSingleFileResult result = mGson.fromJson(new String(responseBody), UploadSingleFileResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.uploadInfo);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}

		});
	}
	
	private void submitBaterAsync(String title , String content , List<String> medias ,List<ImageInfo> medias_thumb,
			double price , String address , String contact ,
			final ListenerOnPublishBater listener)
	{
		User user = getUser();
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("owner", user.getPhone_number());
		params.put("title", title);
		params.put("content", content);
		params.put("medias" , mGson.toJson(medias));
		params.put("medias_thumb" , mGson.toJson(medias_thumb));
		Bater.ExtraDataInfo extraDataInfo = new Bater.ExtraDataInfo();
		extraDataInfo.price = price;
		extraDataInfo.address = address;
		extraDataInfo.contact = contact;
		
		params.put("extra_data", mGson.toJson(extraDataInfo));
		
		mClient.get(mContext, Config.getFullUrl(mContext, URL_PUBLISHBATER ), 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail(arg3.getMessage());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					PublishBaterResult result = mGson.fromJson(new String(responseBody), PublishBaterResult.class);
					if(0 == result.errorCode)
					{
						Bater bater = result.bater;
						bater.setExtraDatainfo(mGson.fromJson(bater.getExtra_data(), Bater.ExtraDataInfo.class));
						listener.onSuccess(bater);
						broadcaseBaterAdd(bater);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}

		});
	}
	public void publishBaterAsync(final String title , final String content , final List<String> input_medias ,
			final double price , final String address , final String contact ,
			final ListenerOnPublishBater listener)
	{
		final Handler handler = new Handler(){
			int count = input_medias.size();
			int successCount = 0;
			int failCount = 0;
			List<String> medias = new ArrayList<String>();
			List<ImageInfo> medias_thumb = new ArrayList<ImageInfo>();
			public void handleMessage(Message msg)
			{
				if(0 == msg.what)
				{
					successCount ++;
					medias.add(((UploadInfo)msg.obj).media);
					medias_thumb.add(((UploadInfo)msg.obj).media_thumb);
				}
				else
				{
					failCount ++;
				}
				
				if(count == (successCount + failCount))
				{
					if(successCount == count)
					{
						submitBaterAsync(title , content , medias , medias_thumb ,
								price , address , contact , listener);
					}
					else
					{
						listener.onFail("failed");
					}
				}
			}
		};
		final ListenerOnUploadSingleFile fileUploadListener = new ListenerOnUploadSingleFile(){
			
			@Override
			public void onSuccess(UploadInfo uploadInfo) {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.what = 0 ;
				msg.obj = uploadInfo;
				handler.sendMessage(msg);
			}

			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(-1);
			}};
		for(String media:input_medias)
		{
			uploadSingleFileAsync(media, fileUploadListener);
		}
	}
	
	
	public void refreshBaterListAsync(final ListenerOnBaterList listener)
	{
		mBaterListPage = 0;
		mBaterListSize = 20;
		mBaterListRefreshUnixTime = new Date().getTime()/1000;
		getBaterListAsync(mBaterListPage , mBaterListSize, mBaterListRefreshUnixTime ,listener);
	}
	public void loadMoreBaterListAsync(final ListenerOnBaterList listener)
	{
		getBaterListAsync(mBaterListPage , mBaterListSize , mBaterListRefreshUnixTime,listener);
	}
	private void getBaterListAsync(int page , int size , long refreshtime , final ListenerOnBaterList listener)
	{
		if(PAGE_END == mBaterListPage)
		{
			listener.onNoMore();
			return;
		}
		
		User user = getUser();
		mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("owner", user.getPhone_number());
		params.put("start" , page * size);
		params.put("size" , size);
		params.put("refreshtime" , refreshtime);
		mClient.get(mContext, Config.getFullUrl(mContext, URL_GETBATERPOSTLIST ), 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					BaterListResult result = mGson.fromJson(new String(responseBody), BaterListResult.class);
					
					for(Bater bater:result.baters)
					{
						bater.setExtraDatainfo(mGson.fromJson(bater.getExtra_data(), Bater.ExtraDataInfo.class));
					}
					if(0 == result.errorCode)
					{
						if(result.baters.size() < mBaterListSize)
						{
							mBaterListPage = PAGE_END;
						}
						else
						{
							mBaterListPage ++;
						}
						
						listener.onSuccess(result.baters);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}

		});
	}
	
	public void getLuckyTurnTableAsync(final ListenerTurnTableGameGet listener)
	{
		RequestParams params = new RequestParams();
		
		mClient.get(mContext , Config.getFullUrl(mContext, URL_LUCKEYTURNTABLEGET ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					TurnTableGameGetResult result = mGson.fromJson(new String(responseBody), TurnTableGameGetResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.turnTableGame.getImage());
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	public void playLuckyTurnTableAsync(final ListenerTurnTableGamePlay listener)
	{
		User user = getUser();
		RequestParams params = new RequestParams();
		params.put("player", user.getPhone_number());
		mClient.get(mContext , Config.getFullUrl(mContext, URL_LUCKEYTURNTABLEPLAY ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					TurnTableGamePlayResult result = mGson.fromJson(new String(responseBody), TurnTableGamePlayResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.gameResult);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	public void refreshGiftAsync(final ListenerOnGiftList listener)
	{
		mGiftListPage = 0;
		mGiftListSize = 20;
		mGiftListRefreshUnixTime = new Date().getTime()/1000;
		getGiftListAsync(mGiftListPage , mGiftListSize, mGiftListRefreshUnixTime ,listener);
	}
	public void loadMoreGiftAsync(final ListenerOnGiftList listener)
	{
		getGiftListAsync(mGiftListPage , mGiftListSize , mGiftListRefreshUnixTime,listener);
		
	}
	private void getGiftListAsync(final int page ,final int size , long refreshtime , final ListenerOnGiftList listener)
	{
		if(PAGE_END == mGiftListPage)
		{
			listener.onNoMore();
			return;
		}
		Location location = getLocation();
		RequestParams params = new RequestParams();
		params.put("phone_number" , getUser().getPhone_number());
		String lng,lat;
		lng = String.valueOf(location.getLng());
		lat = String.valueOf(location.getLat());
		params.put("lng" , lng);
		params.put("lat" , lat);
		params.put("start" , page * size);
		params.put("size" , size);
		params.put("refreshtime" , refreshtime);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_GIFTLISTGET ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					GiftListResult result = mGson.fromJson(new String(responseBody), GiftListResult.class);
					if(0 == result.errorCode)
					{
						if(result.gifts.size() < mGiftListSize)
						{
							mGiftListPage = PAGE_END;
						}
						else
						{
							mGiftListPage ++;
						}
						listener.onSuccess(result.gifts);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	
	public User getUser()
	{
		return User.loadFromLocal(mContext);
	}
	public Wallet getWallet()
	{
		return Wallet.loadFromLocal(mContext);
	}
	public Location getLocation()
	{
		Location location =  Location.load(mContext);
		if(null == location)
		{
			location = defaultLocation;
		}
		
		return location;
	}
	public void deleteContext()
	{
		User.deleteAll(mContext);
	}
	void stopScreenService()
	{
		mContext.stopService(new Intent(mContext, LockScreenService.class));
	}
    void startScreenService()
    {
    	mContext.startService(new Intent(mContext,LockScreenService.class));  
    }
    
	void updateUser(User user)
	{
		User.saveToLocal(mContext, user, null);
		startScreenService();
	}
	void updateWallet(Wallet wallet)
	{
		Wallet.saveToLocal(mContext, wallet);
	}
	public String addMoreInfo(String link)
	{
		Location location = getLocation();
		
		link = addLinkParameter(link , "phone_number" , getUser().getPhone_number());
		link = addLinkParameter(link , "password" , getUser().getPassword());
		link = addLinkParameter(link , "lat" , String.valueOf(location.getLat()));
		link = addLinkParameter(link , "lng" , String.valueOf(location.getLng()));
		return link;
	}
	public void redirect(String link)
	{
		link = addMoreInfo(link);
		Uri uri = Uri.parse(link);
    	
		if((null != uri) || (null != uri.getScheme()))
		{
			String schema = uri.getScheme();
			if(   schema.equalsIgnoreCase("https")
				||schema.equalsIgnoreCase("http"))
			{
				onWebViewUrl(link);
			}
			else
			{
				Class<?> destAct = null;
				for (String key : Uri2Activity.keySet()) {  
				    if(uri.toString().startsWith(key))
				    {
				    	destAct = Uri2Activity.get(key);
				    	Intent intent = new Intent(mContext ,destAct); 
				    	intent.setData(uri);
						mContext.startActivity(intent);
				    	break;
				    }
				}  
			}
			
			
		}
		
	}
	public String addLinkParameter(String link , String key , String value)
	{
		if(link.indexOf("?") > 0 )
		{
		    return link + "&" + key+ "=" + value;
		}
		else
		{
			return link + "?" + key+ "=" + value;
		}
	}
	public void onWebViewUrl(String url)
	{
		Intent i = new Intent();
		i.setClass(mContext, ActBrowser.class);
		ArrayList<String > blacklist = new ArrayList<String>();
		i.putStringArrayListExtra(ActBrowser.INTENT_URL_BLACKLIST, blacklist);
		i.putExtra(ActBrowser.INTENT_WEB_URL, url);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		mContext.startActivity(i);
	}
	public void startLocation(ListenerOnGPS listener){
		if(mLocationClient.isStarted())
		{
			Location loc = new Location();
			loc.setAddress(msAddress);
			loc.setLat(mLatitude);
			loc.setLng(mLongitude);
			listener.onIndication(loc);
			return;
		}
		
		mListenerOnGPSList.add(listener);
		mLocationClient.registerLocationListener(mMyLocationListener);
		mLocationClient.start();
	}
	
	public void stopLocation()
	{
		if(!mLocationClient.isStarted())
		{
			return;
		}
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(mMyLocationListener);
		mListenerOnGPSList.clear();
	}
	
	public void playPuzzleGame()
	{
		String url = Config.getFullUrl(mContext, URL_PUZZLEGAMELISTGET );
		url = addMoreInfo(url);
		Intent i = new Intent(mContext, PuzzleGamePickerAct.class);
		i.putExtra(PuzzleGamePickerAct.EXTRA_URL, url);
		mContext.startActivity(i);
		
	}
	public void shareBySms(Activity activity)
	{
		
		String text = mContext.getString(R.string.share_to_context);
		initShareIntent(activity , null , text , null);
			
	}
	public boolean getScreenSaverSetting()
	{
		AppSetting oldSetting = AppSetting.loadFromLocal(mContext);
		return  0 != oldSetting.getEnable_lockscreen();
	}
	public void enableScreenSaver(boolean enable)
	{
		AppSetting oldSetting = AppSetting.loadFromLocal(mContext);
		oldSetting.setEnable_lockscreen(enable?1:0);
		AppSetting.saveToLocal(mContext, oldSetting);
	}
	public void startCommonDeamon()
	{
		/*
		if(null != mNativeBroadcastReceiver)
		{
			return;
		}
		mNativeBroadcastReceiver = new NativeBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
    	filter.addAction(Config.INTENT_PUZZLE_GAME_WIN);    
		mContext.registerReceiver(mNativeBroadcastReceiver, filter);
		*/
	}
	public void stopCommonDeamon()
	{
		/*
		if(null == mNativeBroadcastReceiver)
		{
			return;
		}
		mContext.unregisterReceiver(mNativeBroadcastReceiver);
		mNativeBroadcastReceiver = null;
		*/
	}
	public void registerListener(ListenerOnUpdateUserAndWallet listener)
	{
		mListenerOnUpdateUserAndWalletList.add(listener);
	}
	public void unregisterListener(ListenerOnUpdateUserAndWallet listener)
	{
		mListenerOnUpdateUserAndWalletList.remove(listener);
	}
	public void registerBaterListener(ListenerOnBaterChanged listener)
	{
		mListenerOnBaterChangedList.add(listener);
	}
	public void unregisterBaterListener(ListenerOnBaterChanged listener)
	{
		mListenerOnBaterChangedList.remove(listener);
	}
	public void registerIndicationsListener(ListenerOnIndications listener)
	{
		mListenerOnIndications.add(listener);
	}
	public void unregisterIndicationsListener(ListenerOnIndications listener)
	{
		mListenerOnIndications.remove(listener);
	}
	public void notifyIndicationsListeners(List<Indication> indicationList )
	{
		for(ListenerOnIndications listener : mListenerOnIndications)
		{
			listener.onSuccess(indicationList);
		}
	}
	public void sendNotificationAsync(String title,
										String text,
										int   id,
										boolean bCancelOnClick)
	{

		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
											.setSmallIcon(R.drawable.ihui)
											.setContentText(text)
											.setContentTitle(title)
											.setAutoCancel(bCancelOnClick)
											.setTicker(text)
											.setDefaults(Notification.DEFAULT_ALL)
											.setOnlyAlertOnce(true);
		
		Intent notificationIntent = new Intent(mContext , mContext.getClass());
		
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.setContentIntent(contentIntent);
		
		NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(id, builder.build());
			
	}
	
	public void cancelNotificationAsync(int id)
	{
		NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(id);
			
	}
	public void puzzlegameWinInd(final String id)
	{
		RequestParams params = new RequestParams();
		params.put("phone_number" , getUser().getPhone_number());
		params.put("password" , getUser().getPassword());
		params.put("game_id" , id);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_PUZZLEGAMEWIN ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) 
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) 
			{
				// TODO Auto-generated method stub
				if (statusCode == 200) 
				{
					WalletResult result = mGson.fromJson(new String(responseBody), WalletResult.class);
					Log.i(TAG , mGson.toJson(result));
					if(0 == result.errorCode)
					{
						double credit = result.wallet.getNotify_credit();
						String notifyMsg;
			    		if(credit > 0)
			    		{
			    			notifyMsg = "游戏收入" + SystemUtil.getYuan(credit);
			    		}
			    		else
			    		{
			    			notifyMsg = "手慢了,抢完了!!";
			    		}
			    		
			    		
			    		sendNotificationAsync(notifyMsg,
			    				notifyMsg,
			    				(getUser().getPhone_number()+":puzzlegame:"+id).hashCode() ,
			    	        	true
			    	        	);
			    		getUserAndWalletAsync();
					}
					
				}
			}
		});
	}
	
	public void dotuiScoreAddAsync(long score)
	{
		RequestParams params = new RequestParams();
		params.put("phone_number" , getUser().getPhone_number());
		params.put("password" , getUser().getPassword());
		params.put("adv_phone_id" , getDotuiScoreAdvPhoneId());
		params.put("score" , score);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_DOTUISCOREADD ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) 
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) 
			{
				// TODO Auto-generated method stub
				if (statusCode == 200) 
				{
					WalletResult result = mGson.fromJson(new String(responseBody), WalletResult.class);
					Log.i(TAG , mGson.toJson(result));
					if(0 == result.errorCode)
					{
						double credit = result.wallet.getNotify_credit();
						String notifyMsg;
			    		if(credit > 0)
			    		{
			    			notifyMsg = "收入" + SystemUtil.getYuan(credit)+"元";
			    			
			    			sendNotificationAsync(notifyMsg,
				    				notifyMsg,
				    				(getUser().getPhone_number()+":dotuiScore:"+getDotuiScoreAdvPhoneId()).hashCode() ,
				    	        	true
				    	        	);
				    		getUserAndWalletAsync();
			    		}
					}
				}
			}
		});
	}
	
	public void downloadScoreAddAsync(final long adv_phone_id)
	{
		RequestParams params = new RequestParams();
		params.put("phone_number" , getUser().getPhone_number());
		params.put("password" , getUser().getPassword());
		params.put("adv_phone_id" , adv_phone_id);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_DOWNLOADSCOREADD ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) 
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) 
			{
				// TODO Auto-generated method stub
				if (statusCode == 200) 
				{
					WalletResult result = mGson.fromJson(new String(responseBody), WalletResult.class);
					Log.i(TAG , mGson.toJson(result));
					if(0 == result.errorCode)
					{
						double credit = result.wallet.getNotify_credit();
						String notifyMsg;
			    		if(credit > 0)
			    		{
			    			notifyMsg = "收入" + SystemUtil.getYuan(credit)+"元";
			    			
			    			sendNotificationAsync(notifyMsg,
				    				notifyMsg,
				    				(getUser().getPhone_number()+":download:"+adv_phone_id).hashCode() ,
				    	        	true
				    	        	);
				    		getUserAndWalletAsync();
			    		}
					}
				}
			}
		});
	}
	
	public void refreshIndianaAsync(String type , final ListenerOnIndianaList listener)
	{
		PageSizeInfo info = mPageSizeInfoMap.get(type);
		if(null == info)
		{
			info = new PageSizeInfo();
			mPageSizeInfoMap.put(type, info);
		}
		info.page = 0;
		info.size = 20;
		info.refreshUnixTime = new Date().getTime()/1000;
		getIndianaListAsync(type ,info ,listener);
	}
	public void loadMoreIndianaAsync(String type , final ListenerOnIndianaList listener)
	{
		PageSizeInfo info = mPageSizeInfoMap.get(type);
		if(null == info)
		{
			return ;
		}
		
		getIndianaListAsync(type , info , listener);
		
	}
	private void getIndianaListAsync(String type ,final PageSizeInfo info , final ListenerOnIndianaList listener)
	{
		if(PAGE_END == info.page)
		{
			listener.onNoMore();
			return;
		}
		RequestParams params = new RequestParams();
		params.put("start" , info.page * info.size);
		params.put("size" , info.size);
		params.put("type" , type);
		params.put("refreshtime" , info.refreshUnixTime);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_INDIANALISTGET ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					IndianaListResult result = mGson.fromJson(new String(responseBody), IndianaListResult.class);
					if(0 == result.errorCode)
					{
						if(result.list.size() < info.size)
						{
							info.page = PAGE_END;
						}
						else
						{
							info.page ++;
						}
						listener.onSuccess(result.list);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	public void prepareWeixinpayAsync(	String body ,
										String out_trade_no ,
										String total_fee ,
										final ListenerOnWeixinPayReqEx listener)
	{
		RequestParams params = new RequestParams();
		params.put("body" , body);
		params.put("out_trade_no" , out_trade_no);
		params.put("total_fee" , total_fee);
		mClient.get(mContext , Config.getFullUrl(mContext, URL_WEIXINPREPAY ), params , new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				listener.onFail("fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					WeixinPayReqExResult result = mGson.fromJson(new String(responseBody), WeixinPayReqExResult.class);
					if(0 == result.errorCode)
					{
						listener.onSuccess(result.payReqEx);
					}
					else
					{
						listener.onFail(result.description);
					}
					
				}
				else
				{
					listener.onFail("fail");
				}
				
			}
			
		});
	}
	private void broadcaseUserAndWallet(User me , Wallet wallet)
	{
		for(ListenerOnUpdateUserAndWallet curListener : mListenerOnUpdateUserAndWalletList)
		{
			curListener.onSuccess(me , wallet);
		}
	}
	
	private void broadcaseBaterAdd(Bater bater)
	{
		for(ListenerOnBaterChanged curListener : mListenerOnBaterChangedList)
		{
			curListener.onAdd(bater);
		}
	}
	private void initShareIntent(Activity activity , String type , String msg , Uri uri) {
    	
    	if(null == type)
    	{
    		ShareAppAct.share(activity, -1, msg);
    	}
	   
	}
	void showToast(String msg)
	{
		Toast.makeText(mContext, msg,
				Toast.LENGTH_LONG).show();
	}
	void postMessage(String log)
	{
		Message msg = mHandler.obtainMessage(0, log);
        mHandler.sendMessage(msg);
	}
	
	public static class Result
	{
		public int errorCode;
		public String description;
	}
	public static class WeixinAdv
	{
		public String title;
		public String content;
		public String url;
		public String picture;
		public int picResource;
	}
	public class Indication
	{
		public String name;
		public boolean bNeed;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the bNeed
		 */
		public boolean isbNeed() {
			return bNeed;
		}
		/**
		 * @param bNeed the bNeed to set
		 */
		public void setbNeed(boolean bNeed) {
			this.bNeed = bNeed;
		}
		
	}
	public static class CreateWeixinAdvResult extends Result
	{
		public WeixinAdv weixinAdv;
	}
	public static class UserAndWallet
	{
		public User me;
		public Wallet wallet;
	}
	public static class UploadInfo{
		public String media ;
		public ImageInfo media_thumb ;
	}

	public static class UserAndWalletResult extends Result
	{
		UserAndWallet userAndWallet;
	}
	public static class WalletResult extends Result
	{
		public Wallet wallet;
	}
	
	public static class AgentListResult extends Result
	{
		AddressPickerAct.Address agentAddress;
	}
	
	public static class FixedAdvListResult extends Result
	{
		List<Adv> advList;
	}
	
	public static class DyRecommendSellerListResult extends Result
	{
		List<WHotel> sellers;
	}
	public static class PortraitChangeResult extends Result
	{
		User me;
	}
	public static class UpdateWalletResult extends Result
	{
		Wallet wallet;
	}
	public static class IndicationsResult extends Result
	{
		List<Indication> indicationList;
	}
	public static class PublishBaterResult extends Result
	{
		Bater bater;
	}
	
	public static class UploadSingleFileResult extends Result
	{
		UploadInfo uploadInfo;
	}
	public class BaterListResult extends Result{
		List<Bater> baters;
	}
	
	public class GamePlayListResult extends Result{
		List<DownloadFileModel> downloads;
	}

	public class GiftListResult extends Result{
		List<Gift> gifts;
	}
	public class IndianaListResult extends Result{
		List<Indiana> list;
	}
	public class TurnTableGameGetResult extends Result{
		TurnTableGame turnTableGame;
	}
	public class TurnTableGamePlayResult extends Result{
		TurnTableResult gameResult;
	}
	public class WeixinPayReqExResult extends Result{
		PayReqEx payReqEx;
	}
	public static interface ListenerOnCreateWeixinAdv{
		public void onSuccess(WeixinAdv weixinAdv);
		public void onFail(String msg);
	}
	
	public static interface ListenerOnRegister{
		public void onSuccess(User user , Wallet wallet);
		public void onFail(String msg);
	}
	
	public static interface ListenerOnAgentList{
		public void onSuccess(AddressPickerAct.Address agentAddress);
		public void onFail(String msg);
	}
	
	public static interface ListenerOnLogin{
		public void onSuccess(User user , Wallet wallet);
		public void onFail(String msg);
	}
	public static interface ListenerOnPasswordClaim{
		public void onSuccess();
		public void onFail(String msg);
	}
	public static interface ListenerOnSmsValidate{
		public void onSuccess();
		public void onFail(String msg);
	}
	public static interface ListenerOnUpdateUserAndWallet{
		public void onSuccess(User user , Wallet wallet);
		public void onFail(String msg);
	}
	public static interface ListenerOnGetFixedAdvList{
		public void onSuccess(List<Adv> advList);
		public void onFail(String msg);
		public void onNoMore();
	}
	public static interface ListenerOnGetDyRecommendSellers{
		public void onSuccess(List<WHotel> sellers);
		public void onFail(String msg);
	}
	public static interface ListenerOnGPS{
		public void onIndication(Location gps);
	}
	public static interface ListenerOnPortraitChanged{
		public void onSuccess(User user);
		public void onFail(String msg);
	}
	public static interface ListenerOnUpdateWallet{
		public void onSuccess(Wallet wallet);
		public void onFail(String msg);
	}
	public static interface ListenerOnIndications{
		public void onSuccess(List<Indication> indicationList);
		public void onFail(String msg);
	}
	public static interface ListenerOnPublishBater{
		public void onSuccess(Bater bater);
		public void onFail(String msg);
	}
	public static interface ListenerOnBaterChanged
	{
		public void onAdd(Bater bater);
	}
	public static interface ListenerOnUploadSingleFile
	{
		public void onSuccess(UploadInfo uploadInfo);
		public void onFail(String msg);
	}
	public static interface ListenerOnBaterList
	{
		public void onSuccess(List<Bater> baters);
		public void onNoMore();
		public void onFail(String msg);
	}
	public static interface ListenerOnGiftList
	{
		public void onNoMore();
		public void onSuccess(List<Gift> gifts);
		public void onFail(String msg);
	}
	public static interface ListenerTurnTableGameGet{
		public void onSuccess(String image);
		public void onFail(String msg);
	}
	
	public static interface ListenerTurnTableGamePlay{
		public void onSuccess(TurnTableResult gameResult);
		public void onFail(String msg);
	}
	public static interface ListenerOnIndianaList
	{
		public void onNoMore();
		public void onSuccess(List<Indiana> list);
		public void onFail(String msg);
	}
	public static interface ListenerOnGamePlayList
	{
		public void onSuccess(List<DownloadFileModel> download);
		public void onNoMore();
		public void onFail(String msg);
	}
	public static interface ListenerOnWeixinPayReqEx
	{
		public void onSuccess(PayReqEx payReqEx);
		public void onFail(String msg);
	}

	
}
