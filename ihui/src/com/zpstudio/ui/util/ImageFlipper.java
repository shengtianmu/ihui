package com.zpstudio.ui.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class ImageFlipper implements ViewSwitcher.ViewFactory{
	private static int MSG_UPDATE = 1;
	
	Context   context;
	ImageSwitcher imageSwitcher;
	private int[] arrayPictures;
	private int period = 1000;
	// Ҫ��ʾ��ͼƬ��ͼƬ�����е�Index  
	private int pictureIndex = 0;
	private boolean isalive = true; //�߳�ѭ�����е����� 
	//ͨ��handler������������  mgallery.setSelection(positon),ѡ�е�position��ͼƬ��Ȼ�����OnItemSelectedListener�����ı�ͼ��
	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE) {
				if(imageSwitcher != null)imageSwitcher.setImageResource(arrayPictures[msg.arg1]);  
			}
		}
	};
	private Thread thread = new Thread(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					while (isalive) {
						
						
						//����ʱ����Ϊ 2s 
						try {
							Thread.sleep(period);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(arrayPictures != null && imageSwitcher != null)
						{
							pictureIndex ++;
							if(pictureIndex == arrayPictures.length)
							{
								pictureIndex = 0;
							}
							
							Message msg = mhandler.obtainMessage(MSG_UPDATE, pictureIndex,	0);
							mhandler.sendMessage(msg);
						}
						
						
					}
				}
			});
	
	

	public ImageSwitcher getImageSwitcher() {
		return imageSwitcher;
	}

	public void setImageSwitcher(ImageSwitcher imageSwitcher) {
		this.imageSwitcher = imageSwitcher;
	}

	public int[] getArrayPictures() {
		return arrayPictures;
	}

	public void setArrayPictures(int[] arrayPictures) {
		this.arrayPictures = arrayPictures;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public ImageFlipper(Context context ,ImageSwitcher imageSwitcher , int[] arrayPictures , int period) {
		this.context = context;
		this.imageSwitcher = imageSwitcher;
		this.arrayPictures = arrayPictures;
		this.period = period;
	}
	
	public ImageFlipper(Context context) {
		this(context , null , null , 2000);
	}

	public void start()
	{
		if(!thread.isAlive())
		{
			isalive = true;
			thread.start();
		}
	}
	
	public void stop()
	{
		isalive = false;
	}
	
	public void suspend()
	{
		
	}
	
	public View makeView() {
		ImageView i = new ImageView(context);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        //android��ImageView �����ؼ������ұ��ֿ�߱� .
        i.setAdjustViewBounds(true);
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        return i;
	}
}
