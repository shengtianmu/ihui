package com.zpstudio.ui.adv.fragment;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zpstudio.R;
import com.zpstudio.R.string;
import com.zpstudio.datacenter.db.User;

public class JournalismActivity extends Activity {
	
	private WebView article_web;
	private Advertisement journalism;
	private TextView tv_titles;
	private ImageView iv_phone;
	private String id;
	private String page;
	private String link;
	private News news;
	private Advert advert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journalism);
		id=getIntent().getExtras().getString("id");
		page=getIntent().getExtras().getString("page");

		
		
		
		initView();
		initData();
		

	}
	
	private void initData() {
		// TODO Auto-generated method stub
		
		getJournalismList();
	}
	
	
	public void getJournalismList()
	{
		
		AsyncHttpClient mClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		User user = User.loadFromLocal(this);
		params.put("id", id);
		params.put("page", page);
		params.put("phone_number", user.getPhone_number());
		mClient.get(this, "http://www.02727.com.cn/nozzle/newshow.php?", 
				params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				Log.e("111", "222"+""+statusCode);
				// TODO Auto-generated method stub
				if (statusCode == 200) {
//					JournalismResult result = mGson.fromJson(new String(responseBody), JournalismResult.class);
					JSONObject object=null;
					try {
						String json = new String(responseBody , "UTF-8");
						Log.e("111", "222"+""+json);
						object = new JSONObject(json);
						JSONArray  data=null;
						if (object.getString("status").equals("4")) {
							data=object.getJSONArray("data");
							JSONObject object2=null;
							if (data.length()>0) {
								object2=data.getJSONObject(0);
								link=object2.getString("link");
								
								WebSet(link);
							}
							
							
							
							
							data=object.getJSONArray("news");
							if (data.length()>0) {
								news=new News();
								object2=data.getJSONObject(0);
								news.setNews_id(object2.getString("news_id"));
								news.setNews_page(object2.getString("news_page"));
								news.setNews_titles(object2.getString("news_titles"));
								
								tv_titles.setText(news.getNews_titles());
							}
							
							data=object.getJSONArray("advert");
							if (data.length()>0) {
								advert=new Advert();
								object2=data.getJSONObject(0);
//								 "admin_city": "上海",
//							     "valid_end": "2016-02-27 00:00:00",
//							     "content": "http://www.02727.com.cn/upfiles/advertitlepic/12/20150829142957402.jpg",
//							     "link": "http://m.02727.cn/advertisement.php?id=95",
//							     "credit": "5000"
								advert.setAdmin_city(object2.getString("admin_city"));
								advert.setContent(object2.getString("content"));
								advert.setCredit(object2.getString("credit"));
								advert.setLink(object2.getString("link"));
								advert.setValid_end(object2.getString("valid_end"));
								advert.setAdv_phone_id(object2.getString("adv_phone_id"));
								if (advert.getContent()!=null&&!"".equals(advert.getContent())) {
									iv_phone.setVisibility(View.VISIBLE);
									ImageLoader.getInstance().displayImage(advert.getContent(), iv_phone);
								}
							}
							
							
//							listener.onSuccess(journalismList);
						}else {
//							listener.onNoMore(object.getString("describtion"));
							
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
//					listener.onFail("fail");
				}
				
			}

		});
	}
	
	

	private void initView() {
		// TODO Auto-generated method stub
		article_web=(WebView) findViewById(R.id.webView);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		iv_phone=(ImageView) findViewById(R.id.iv_phone);
		iv_phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(JournalismActivity.this, AdvertisementActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("link", advert.getLink());
				bundle.putString("id", advert.getAdv_phone_id());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		tv_titles.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(JournalismActivity.this, JournalismActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("id", news.getNews_id());
				bundle.putString("page", news.getNews_page()+"");
//				bundle.putString("link", list.get(0).getLink());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
	}
	
	

	private void WebSet(String name) {
		article_web.loadUrl(name); 
		article_web.requestFocusFromTouch();
		article_web.getSettings().setJavaScriptEnabled(true);
		article_web.setWebViewClient(new WebViewClient(){
		      @Override
		      public boolean shouldOverrideUrlLoading(WebView view, String url) {
		    	  
//		    	  if(Uri.parse(url).getHost().contains("http://121.40.154.127/main/share?main_id="))
//		            {
//		    		    Log.e("===", "ddddd");
//		                return false;
//		            }else {
//		            	Log.e("===", "cccccc");
//		            	return true;
//					}
		          view.loadUrl(url);
		          return true;
		      }
		});
		article_web.setBackgroundColor(Color.parseColor("#FFFFFF"));
		WebSettings settings = article_web.getSettings();
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);

	}
}
