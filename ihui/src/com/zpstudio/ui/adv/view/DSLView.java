package com.zpstudio.ui.adv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zpstudio.R;

public class DSLView extends View{
	private Unlock unlock;
	
	public void setUnlock(Unlock unlock) {
		this.unlock = unlock;
	}

	private boolean isCache = false;
	private Paint mPaint;
	private float w = 0;
	private float h = 0;
	private Bitmap locus_round_original;//圆点初始状态时的图片
	private Bitmap locus_round_click;//圆点点击时的图片
	private Bitmap locus_round_leff;//
	private Bitmap locus_round_leff_original;//
	private Bitmap locus_round_leff_click;//
	private Bitmap locus_round_right;//
	private Bitmap locus_round_right_original;//
	private Bitmap locus_round_right_click;//
	private int margin=10;
	
	private boolean isTouch = true; // 是否可操作
	private float ex;
	private float ey;
	private int leff_money=10;
	private boolean is_download = true;
	
	public void Replace(int leff_money,boolean is_download) {
		// TODO Auto-generated method stub
		this.leff_money=leff_money;
		this.is_download=is_download;
		this.postInvalidate();
	}
	
	private Bitmap leff1;
	private Bitmap right1;
	private Bitmap locus_round_right1;
	private Bitmap locus_round_right_click1;
	private Bitmap locus_round_right_original1;

	public DSLView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DSLView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DSLView(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (!isCache) {
			initCache();
		}
		drawToCanvas(canvas);
//		super.onDraw(canvas);
	}
	
	/**
	 * 初始化Cache信息
	 * 
	 * @param canvas
	 */
	private void initCache() {
		
		w = this.getWidth();
		h = this.getHeight();
		
		leff1 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.leff1);
		right1 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.right1);
		
		locus_round_leff = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.leff);
		locus_round_leff_click = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.leff_click);
		locus_round_leff_original = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.leff_original);
		
		margin=(locus_round_leff_original.getWidth()-locus_round_leff.getWidth())/2;
		
		locus_round_right = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.right);
		locus_round_right_click = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.right_click);
		locus_round_right_original = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.right_original);
		
		locus_round_right1 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.download);
		locus_round_right_click1 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.download_click);
		locus_round_right_original1 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.download_original);
		
		
		locus_round_original = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.round_original);
		
		locus_round_click = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.round_click);
		
		isCache = true;
	}
	
	
	
	private void drawToCanvas(Canvas canvas) {
		
		/** 
         * 获得绘制文本的宽和高 
         */  
        mPaint = new Paint();  
		
//		mPaint.setColor(Color.BLACK);  
//        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);  
  
        mPaint.setColor(Color.WHITE);  
        mPaint.setTextSize(26);
//        canvas.drawText("13456", getWidth() / 2, getHeight() / 2, mPaint);  
        
//        canvas.drawColor(Color.WHITE);
        
        if (!is_download) {
        	
//        	canvas.drawText("+"+((double)leff_money)/100, margin+locus_round_leff.getWidth()/2-26f, h/2+locus_round_right_click.getHeight()/2+24, mPaint);  
        	if (isTouch) {
            	canvas.drawBitmap(locus_round_original, (w-locus_round_original.getWidth())/2, h/2-locus_round_original.getHeight()/2,
        				mPaint);
            	canvas.drawBitmap(locus_round_right1, 0+margin, h/2-locus_round_right.getHeight()/2,
        				mPaint);
                canvas.drawBitmap(locus_round_leff, w-locus_round_leff.getWidth()-margin, h/2-locus_round_leff.getHeight()/2,
        				mPaint);
    		}else {
    			
    			if (ex<w/4) {
    				canvas.drawBitmap(locus_round_right_original1, 0, h/2-locus_round_right_original.getHeight()/2,
    	    				mPaint);
    	            canvas.drawBitmap(locus_round_leff_click, w-locus_round_right.getWidth()-margin*2, h/2-locus_round_leff_click.getHeight()/2,
    	    				mPaint);
    				
    			}else if (ex>w/4*3) {
    				canvas.drawBitmap(locus_round_right_click1, 0, h/2-locus_round_right_click.getHeight()/2,
    	    				mPaint);
    	            canvas.drawBitmap(locus_round_leff_original, w-locus_round_right.getWidth()-margin*2, h/2-locus_round_leff_original.getHeight()/2,
    	    				mPaint);
    	            
    			}else {
    				canvas.drawBitmap(locus_round_click, ex-locus_round_click.getWidth()/2, h/2-locus_round_click.getHeight()/2,
    						mPaint);
    				canvas.drawBitmap(locus_round_right_click1, 0, h/2-locus_round_right_click.getHeight()/2,
    	    				mPaint);
    	            canvas.drawBitmap(locus_round_leff_click, w-locus_round_right.getWidth()-margin*2, h/2-locus_round_leff_click.getHeight()/2,
    	    				mPaint);
    	            canvas.drawBitmap(leff1, w/4-leff1.getWidth()/2+margin, h/2-leff1.getHeight()/2,
    	    				mPaint);
    	            canvas.drawBitmap(right1, w/4*3-right1.getWidth()/2-margin, h/2-right1.getHeight()/2,
    	    				mPaint);
    	            
    			}
    			
    		}
		}else {
			if (isTouch) {
	        	canvas.drawBitmap(locus_round_original, (w-locus_round_original.getWidth())/2, h/2-locus_round_original.getHeight()/2,
	    				mPaint);
	        	canvas.drawBitmap(locus_round_right, 0+margin, h/2-locus_round_right.getHeight()/2,
	    				mPaint);
	            canvas.drawBitmap(locus_round_leff, w-locus_round_leff.getWidth()-margin, h/2-locus_round_leff.getHeight()/2,
	    				mPaint);
			}else {
				
				if (ex<w/4) {
					canvas.drawBitmap(locus_round_right_original, 0, h/2-locus_round_right_original.getHeight()/2,
		    				mPaint);
		            canvas.drawBitmap(locus_round_leff_click, w-locus_round_right.getWidth()-margin*2, h/2-locus_round_leff_click.getHeight()/2,
		    				mPaint);
					
				}else if (ex>w/4*3) {
					canvas.drawBitmap(locus_round_right_click, 0, h/2-locus_round_right_click.getHeight()/2,
		    				mPaint);
		            canvas.drawBitmap(locus_round_leff_original, w-locus_round_right.getWidth()-margin*2, h/2-locus_round_leff_original.getHeight()/2,
		    				mPaint);
		            
				}else {
					canvas.drawBitmap(locus_round_click, ex-locus_round_click.getWidth()/2, h/2-locus_round_click.getHeight()/2,
							mPaint);
					canvas.drawBitmap(locus_round_right_click, 0, h/2-locus_round_right_click.getHeight()/2,
		    				mPaint);
		            canvas.drawBitmap(locus_round_leff_click, w-locus_round_right.getWidth()-margin*2, h/2-locus_round_leff_click.getHeight()/2,
		    				mPaint);
		            canvas.drawBitmap(leff1, w/4-leff1.getWidth()/2+margin, h/2-leff1.getHeight()/2,
		    				mPaint);
		            canvas.drawBitmap(right1, w/4*3-right1.getWidth()/2-margin, h/2-right1.getHeight()/2,
		    				mPaint);
		            
				}
				
			}
		}
        
        
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 不可操作
//		if (!isTouch) {
//			return false;
//		}
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN: // 点w下
			ex = event.getX();
			ey = event.getY();
			if (ex>w/2-locus_round_original.getWidth()/2&&ex<w/2+locus_round_original.getWidth()/2
					&&ey>h/2-locus_round_original.getHeight()/2&&ey<h/2+locus_round_original.getHeight()/2) {
				
				isTouch=false;
				getParent().requestDisallowInterceptTouchEvent(true);  
			}else {
				
				isTouch=true;
			}
			break;
		case MotionEvent.ACTION_MOVE: // 移动
			if (!isTouch) {
				ex = event.getX();
				ey = event.getY();
				getParent().requestDisallowInterceptTouchEvent(true);  
			}else {
				
			}
			break;
		case MotionEvent.ACTION_UP: // 提起
			
			if (unlock!=null&&!isTouch) {
				if (ex<w/4) {
					unlock.leff();
				}else if (ex>w/4*3) {
					unlock.Right();
				}
			}
			isTouch=true;
			getParent().requestDisallowInterceptTouchEvent(false);  
			break;
		}
//		Log.e("22", ex+":"+ey);
		this.postInvalidate();
		return true;
	}

}
