package com.zpstudio.ui.adv.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class MyView extends View {
	//画笔
	private static Paint paint;
	private int XLength;
	private int Ylength;
	private int number=5;//点的个数
	private int y=3;//点的个数
	
	public void setMyView(int number,int y){
		this.number=number;
		this.y=y;
		postInvalidate();
		
	}
	public MyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		 // TODO Auto-generated constructor stub
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	@SuppressLint("ResourceAsColor")
	@Override
	 protected void onDraw(Canvas canvas) {
		// X轴
		XLength = getWidth();
		// Y轴
		Ylength = getHeight();
		//画笔
		paint = new Paint();
		//抗锯齿
		paint.setAntiAlias(true); 
		//颜色
		paint.setColor(Color.WHITE);
		
		for (int i = 1; i < number+1; i++) {
			if (i==y) {
				paint.setColor(Color.rgb(94, 1, 0)); 
				canvas.drawRect((XLength-number*30-20)/2+30*i-14, Ylength/2-4, 
						(XLength-number*30-20)/2+30*i+14, Ylength/2+4, paint);  
			}else {
				paint.setColor(Color.rgb(1, 1, 1)); 
				canvas.drawRect((XLength-number*30-20)/2+30*i-14, Ylength/2-4, 
						(XLength-number*30-20)/2+30*i+14, Ylength/2+4, paint);  
			}
			
			
		}
	    
	 }

}
