package com.zpstudio.ui.util.imagepicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.open.crop.act.ActCropImage;
import com.zpstudio.R;
import com.zpstudio.ui.util.imagepicker.GalleryAdapter.OnCameraSelectedListener;
import com.zpstudio.util.ImageCompress;
import com.zpstudio.util.SystemUtil;

public class ActImagePicker extends Activity implements OnCameraSelectedListener {
	public static final String ACTION_PICK = "luminous.ACTION_PICK";
	public static final String ACTION_MULTIPLE_PICK = "luminous.ACTION_MULTIPLE_PICK";
	
	public static final String EXTRA_KEY_QUANTITY = "quantity";
	public static final String EXTRA_KEY_CROP_WIDTH = "crop_width";
	public static final String EXTRA_KEY_CROP_HEIGHT = "crop_height";
	public static final String EXTRA_KEY_ID = "id";
	
	private static final String SD_TEMP_DIR =  Environment.getExternalStorageDirectory().toString()+File.separator + "ihui" + File.separator + "temp";
	
	private static final String CAPTURE_OUT_FILE_NAME = SD_TEMP_DIR + File.separator +"image.jpg";
	private static final String CROP_OUT_FILE_NAME    = SD_TEMP_DIR + File.separator +"crop.png";
	
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int CROP_REQUEST_CODE = 2;
	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgNoMedia;
	Button btnGalleryOk;

	String action;
	private ImageLoader imageLoader;

	Button   btnTitleBack;
	TextView tvTitleText;
	
	int mQuality = 50;
	int mCropwidth = 0;
	int mCropheight = 0;
	boolean bSingleMode = true;
	
	public static void pickImage(Activity activty , 
			int requestCode,
			int cropWidth,
			int cropHeight,
			int quality )
	{
		pickImage(activty ,
				requestCode ,
				cropWidth ,
				cropHeight ,
				quality ,
				0);
	}
	public static void pickImage(Activity activty , 
			int requestCode,
			int cropWidth,
			int cropHeight,
			int quality ,
			int id)
	{
		Intent i = new Intent(activty ,ActImagePicker.class);
		i.setAction(ActImagePicker.ACTION_PICK);
		i.putExtra(EXTRA_KEY_CROP_WIDTH, cropWidth);
		i.putExtra(EXTRA_KEY_CROP_HEIGHT, cropHeight);
		i.putExtra(EXTRA_KEY_QUANTITY, quality);
		i.putExtra(EXTRA_KEY_ID, id);
		activty.startActivityForResult(i, requestCode);
	}
	public static void pickImage(Fragment fragment , 
			int requestCode,
			int cropWidth,
			int cropHeight,
			int quality)
	{
		Intent i = new Intent(fragment.getActivity() ,ActImagePicker.class);
		i.setAction(ActImagePicker.ACTION_PICK);
		i.putExtra(EXTRA_KEY_CROP_WIDTH, cropWidth);
		i.putExtra(EXTRA_KEY_CROP_HEIGHT, cropHeight);
		i.putExtra(EXTRA_KEY_QUANTITY, quality);
		fragment.startActivityForResult(i, requestCode);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.util_imagepicker_gallery);
		/* title */
        btnTitleBack = (Button)findViewById(R.id.title_reback_btn);
    	btnTitleBack.setVisibility(View.VISIBLE);
    	btnTitleBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	onGoBack();
            }  
        });
        
    	String title = getString(R.string.util_imagepicker_title);
        tvTitleText  = (TextView)findViewById(R.id.title_text);
        tvTitleText.setText(title);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mCropwidth = dm.widthPixels;
        mCropheight = dm.heightPixels ;
        
        Intent intent = getIntent();
		if(null != intent)
		{
			action = intent.getAction();
			mQuality = intent.getIntExtra(EXTRA_KEY_QUANTITY, mQuality);
			mCropwidth = intent.getIntExtra(EXTRA_KEY_CROP_WIDTH, mCropwidth);
			mCropheight = intent.getIntExtra(EXTRA_KEY_CROP_HEIGHT, mCropheight);
		}
		
		if (action == null) {
			finish();
			return;
		}
		createDir(SD_TEMP_DIR);
		initImageLoader();
		init();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_CANCELED) {

			switch (requestCode) {
			
			case CAMERA_REQUEST_CODE:
				ActCropImage.cropImage(this, CROP_REQUEST_CODE,
						CAPTURE_OUT_FILE_NAME,
						CROP_OUT_FILE_NAME,
						mCropwidth , mCropheight, mQuality);
				break;
			
			case CROP_REQUEST_CODE:
				File outFile = new File(CROP_OUT_FILE_NAME);
				String absPath = outFile.getAbsolutePath();
				result(compressImage(absPath));
				break;
			
		
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	private void initImageLoader() {
		imageLoader = ImageLoader.getInstance();
	}

	private void init() {

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader , this);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
		gridGallery.setOnScrollListener(listener);

		if (action.equalsIgnoreCase(ACTION_MULTIPLE_PICK)) {

			findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);

		} else if (action.equalsIgnoreCase(ACTION_PICK)) {

			findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
			gridGallery.setOnItemClickListener(mItemSingleClickListener);
			adapter.setMultiplePick(false);

		}

		gridGallery.setAdapter(adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		

		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.addAll(getGalleryPhotos());
						checkImageStatus();
					}
				});
				Looper.loop();
			};

		}.start();

	}

	private void checkImageStatus() {
		if (adapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}

	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<CustomGallery> selected = adapter.getSelected();

			String[] allPath = new String[selected.size()];
			for (int i = 0; i < allPath.length; i++) {
				allPath[i] = selected.get(i).sdcardPath;
			}
			
			
			Intent data = new Intent().putExtra("all_path", allPath);
			setResult(RESULT_OK, data);
			finish();

		}
	};
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			adapter.changeSelection(v, position);

		}
	};

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

		@SuppressLint("ParserError")
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			if(adapter.isCamera(position))
			{
				//trigger camera
				adapter.changeSelection(v, position);
			}
			else
			{
				CustomGallery item = adapter.getItem(position);
				//result(compressImage(Uri.fromFile(new File(item.sdcardPath))));
				ActCropImage.cropImage(ActImagePicker.this, CROP_REQUEST_CODE,
						item.sdcardPath,
						CROP_OUT_FILE_NAME,
						mCropwidth , mCropheight, mQuality);
				
			}
		}
	};

	private ArrayList<CustomGallery> getGalleryPhotos() {
		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;

			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);

			if (imagecursor != null && imagecursor.getCount() > 0) {

				while (imagecursor.moveToNext()) {
					CustomGallery item = new CustomGallery();

					int dataColumnIndex = imagecursor
							.getColumnIndex(MediaStore.Images.Media.DATA);

					item.sdcardPath = imagecursor.getString(dataColumnIndex);

					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}

	@Override
	public void onCameraSelected() {
		// TODO Auto-generated method stub
		Intent intentFromCapture = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			intentFromCapture.putExtra(
					MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(CAPTURE_OUT_FILE_NAME)));
			startActivityForResult(intentFromCapture,
					CAMERA_REQUEST_CODE);
		}
		else 
		{
			Toast.makeText(this, SystemUtil.getResourceString(this, R.string.pls_insert_sd_card),
					Toast.LENGTH_LONG).show();
		}

		
	}
	
	/**
	 * 
	 * @param uri
	 */
	private void cropImageUri(Uri input, Uri output , int outputX, int outputY, int requestCode){
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, SystemUtil.getResourceString(this, R.string.pls_insert_sd_card),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(input, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", outputX);
		intent.putExtra("aspectY", outputY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	
	private void result(String path)
	{
		if(null != path)
		{
			//Toast.makeText(this, path, Toast.LENGTH_LONG).show();
			Intent result = null;
			if (action.equalsIgnoreCase(ACTION_MULTIPLE_PICK)) {
				String[] allPath = new String[]{path};
				result = new Intent().putExtra("all_path", allPath);
			}
			else if (action.equalsIgnoreCase(ACTION_PICK)) {
				result = new Intent().putExtra("single_path", path);
			}
			setResult(RESULT_OK, result);
			finish();
		}
			
	}
	
	private String compressImage(Uri srcUri)
	{
		File desFile = new File(SD_TEMP_DIR,SystemUtil.generateUnique() + ".png");
    	
		DisplayMetrics metric = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(metric);  
		int width = metric.widthPixels;     
		int height = metric.heightPixels;
		
		ImageCompress compress = new ImageCompress();
		ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
		options.uri = srcUri;
		options.destFile = desFile;
		options.maxWidth=width;
		options.maxHeight=height;
		compress.compressFromUri(this, options);
		return options.destFile.getAbsolutePath();
	}
    private String compressImage(String srcPath) {  
        
    	Uri srcUri = Uri.fromFile(new File(srcPath));
		return compressImage(srcUri);
        
    }  
	
    /**
	 * 
	 * @param picdata
	 */
	private void saveBitmap(Bitmap bitmap, File outFile) {
		if(null == bitmap)
		{
			return;
		}
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			FileOutputStream outStream = new FileOutputStream(outFile);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			
			baos.writeTo(outStream);
			baos.flush();
			baos.close();
			
			outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
}
