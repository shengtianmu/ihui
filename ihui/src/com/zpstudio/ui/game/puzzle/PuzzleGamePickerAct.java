package com.zpstudio.ui.game.puzzle;



import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zpstudio.R;
import com.zpstudio.datacenter.db.Config;
import com.zpstudio.util.SystemUtil;

public class PuzzleGamePickerAct extends Activity {

	public static final String ACTION_PICK = "puzzlegame.ACTION_PICK";
	public static final String EXTRA_URL = "url";
	private static final String DEFAULT_URL = "http://m.02727.cn:8080/webim/do_puzzlegamelistget.jsp?phone_number=13482750440";
	private static final String SD_TEMP_DIR =  Environment.getExternalStorageDirectory().toString()+File.separator + "ihui" + File.separator + "temp";
	
	GridView gridGallery;
	Handler handler;
	PuzzleGameGalleryAdapter adapter;

	ImageView imgNoMedia;
	Button btnGalleryOk;

	private ImageLoader imageLoader;

	Button   btnTitleBack;
	TextView tvTitleText;
	
	int mQuality = 50;
	int mCropwidth = 0;
	int mCropheight = 0;
	String mUrl = DEFAULT_URL;
	AsyncHttpClient mClient = new AsyncHttpClient();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.puzzlegame_picker_act);
		/* title */
        btnTitleBack = (Button)findViewById(R.id.title_reback_btn);
    	btnTitleBack.setVisibility(View.VISIBLE);
    	btnTitleBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	onGoBack();
            }  
        });
        
    	String title = SystemUtil.getResourceString(this, R.string.puzzlegamepicker);
        tvTitleText  = (TextView)findViewById(R.id.title_text);
        tvTitleText.setText(title);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mCropwidth = dm.widthPixels;//���
        mCropheight = dm.heightPixels ;//�߶�
        
        Intent intent = getIntent();
		
		if (intent != null) 
		{
			String url =  intent.getStringExtra(EXTRA_URL);
			if(null != url)
			{
				mUrl = url;
			}
			
		}
		createDir(SD_TEMP_DIR);
		initImageLoader();
		init();
	}

	private void initImageLoader() {
		
		imageLoader = ImageLoader.getInstance();
	}

	private void init() {

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setNumColumns(2);
		gridGallery.setFastScrollEnabled(true);
		adapter = new PuzzleGameGalleryAdapter(getApplicationContext(), imageLoader );
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
		gridGallery.setOnScrollListener(listener);


		findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
		gridGallery.setOnItemClickListener(mItemSingleClickListener);
		adapter.setMultiplePick(false);


		gridGallery.setAdapter(adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		
		mClient.get(Config.getFullUrl(this, mUrl), new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				try {
					String json = new String(arg2 , "UTF-8");
					Result result = new Gson().fromJson(json ,Result.class);
					if(0 == result.errorCode)
					{
						adapter.addAll(formGalleryPhotos(result.games, result.records));
						checkImageStatus();
						
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});

	}

	private void checkImageStatus() {
		if (adapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}

	

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

		@SuppressLint("ParserError")
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			PuzzleGameCustomGallery item = adapter.getItem(position);
			if(item.isDownloaded && !item.isTimelimited)
			{
				playGame(item);
			}
				
		}
	};

	

	private ArrayList<PuzzleGameCustomGallery> formGalleryPhotos(
			List<PuzzleGame> games,
			List<PuzzleGameRecord> records) {
		ArrayList<PuzzleGameCustomGallery> galleryList = new ArrayList<PuzzleGameCustomGallery>();

		
		for(PuzzleGame game:games)
		{
			PuzzleGameCustomGallery item = new PuzzleGameCustomGallery();
			item.data = game;
			item.url = game.getPicture();

			for(PuzzleGameRecord record:records)
			{
				if(game.getId() != record.getPuzzlegame_id())
				{
					continue;
				}
				Log.i("puzzlegame" , record.getEndtime() + ":" +Long.parseLong(record.getEndtime()) + ":" +game.getTimelimit()+":" + (new Date().getTime()/1000));
				if(Long.parseLong(record.getEndtime()) + game.getTimelimit() >  (new Date().getTime()/1000))
				{
					item.isTimelimited = true;
					break;
				}
				
			}
			galleryList.add(item);
				
		}
		

		return galleryList;
	}
	
	
	void playGame(PuzzleGameCustomGallery item)
	{
		Intent intent = new Intent(this,PuzzleGameAct.class);
		intent.putExtra("pic", item.url);
		intent.putExtra("picTitle", item.url);
		intent.putExtra(Config.EXTRA_PUZZLE_GAME_ID, Long.toString(((PuzzleGame)item.data).getId()));
		this.startActivity(intent);
		finish();
	}
	
	void createDir(String file)
	{
		
		File pageElementFileDir = new File(file);
		if(!pageElementFileDir.exists())
		{
			pageElementFileDir.mkdirs();
		}
		
	}
	private void onGoBack()
	{
		finish();
	}

	public class Result{
		int errorCode;
		String description;
		List<PuzzleGame> games;
		List<PuzzleGameRecord> records;
	}
	
}
