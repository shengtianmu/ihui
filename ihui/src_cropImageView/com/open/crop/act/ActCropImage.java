package com.open.crop.act;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.open.crop.CropImageView;
import com.open.crop.FileUtil;
import com.zpstudio.R;

public class ActCropImage extends Activity{
	private static final String EXTRA_INPUT_PATH = "inputPath";
	public static final String EXTRA_OUTPUT_PATH = "outputPath";
	private static final String EXTRA_CROP_WIDTH = "cropWidth";
	private static final String EXTRA_CROP_HEIGHT = "cropHeight";
	private static final String EXTRA_QUALTITY = "quality";
	String inputPath = null;
	String outputPath = null;
	int cropWidth = 300;
	int cropHeight = 300;
	int quality = 100;
	
	
	public static void cropImage(Activity activty , 
			int requestCode,
			String inputPath,
			String outputPath,
			int cropWidth,
			int cropHeight,
			int quality)
	{
		Intent i = new Intent(activty , ActCropImage.class);
		i.putExtra(EXTRA_INPUT_PATH, inputPath);
		i.putExtra(EXTRA_OUTPUT_PATH, outputPath);
		i.putExtra(EXTRA_CROP_WIDTH, cropWidth);
		i.putExtra(EXTRA_CROP_HEIGHT, cropHeight);
		i.putExtra(EXTRA_QUALTITY, quality);
		activty.startActivityForResult(i, requestCode);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cropimageview_act01);
		final CropImageView mCropImage=(CropImageView)findViewById(R.id.cropImg);
		
		Intent intent = this.getIntent();
		if(null == intent)
		{
			cancell();
			return;
		}
		inputPath  = intent.getStringExtra(EXTRA_INPUT_PATH);
		outputPath = intent.getStringExtra(EXTRA_OUTPUT_PATH);
		cropWidth  = intent.getIntExtra(EXTRA_CROP_WIDTH, cropWidth);
		cropHeight = intent.getIntExtra(EXTRA_CROP_HEIGHT, cropHeight);
		quality    = intent.getIntExtra(EXTRA_QUALTITY, quality);
		
		Bitmap bitmap = FileUtil.readImg(inputPath);
		if(0 == cropWidth || 0 == cropHeight)
		{
			cropWidth = bitmap.getWidth();
			cropHeight = bitmap.getHeight();
		}
		
		if(null == inputPath || null == outputPath)
		{
			cancell();
			return;
		}
		
		mCropImage.setDrawable(new BitmapDrawable(getResources(), bitmap),cropWidth,cropHeight);
		
		findViewById(R.id.save).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Thread(new Runnable(){

					@Override
					public void run() {
						//得到裁剪好的图片
						Bitmap bitmap = mCropImage.getCropImage();
						
						FileUtil.writeImage(bitmap, outputPath, quality);
						
						Intent mIntent=new Intent();
						mIntent.putExtra(EXTRA_OUTPUT_PATH, outputPath);
						setResult(RESULT_OK, mIntent);
						finish();
					}
				}).start();
			}
		});
	}
	
	void cancell()
	{
		Intent mIntent=new Intent();
		setResult(Activity.RESULT_CANCELED, mIntent);
		finish();
	}
}
