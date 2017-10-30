package com.hzjstudio.ihui.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpstudio.R;

/**
 * 自定义倒计时文本控件
 * 
 * @author Administrator
 * 
 */
public class TimeTextView extends TextView implements Runnable {
	String strTime;
	Paint mPaint; // 画笔,包含了画几何图形、文本等的样式和颜色信息
	private long tick10Millis;
	
	private boolean run = false; // 是否启动了
	private OnExpireListener mListenerOnTimeTextViewExpire = null;
	private int expiredDelay = 0;
	String expiredText = null;
	String afterExpiredText = null;
	ImageView afterExpiredImage = null;
	int    afterExpiredImageResid = 0;
	public TimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TimeTextView);
		array.recycle(); // 一定要调用，否则这次的设定会对下次的使用造成影响
	}

	public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TimeTextView);
		array.recycle(); // 一定要调用，否则这次的设定会对下次的使用造成影响
	}

	public TimeTextView(Context context) {
		super(context);
	}

	public void setTick(long tickMillis) {
		this.tick10Millis = tickMillis/10;
	}

	private void ComputeTime() {
		if(tick10Millis > 0)
		{
			tick10Millis -- ;
		}
		
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}
	public void setListenerOnTimeTextViewExpire(OnExpireListener listenerOnTimeTextViewExpire)
	{
		mListenerOnTimeTextViewExpire = listenerOnTimeTextViewExpire;
	}
	@Override
	public void run() {
		// 标示已经启动
		run = true;

		ComputeTime();
		long mmin, msecond, second10Millis;// 分钟，秒,10毫秒
		
		mmin = tick10Millis/(100*60);
		msecond = (tick10Millis%(100*60))/100;
		second10Millis = tick10Millis%100;
		strTime = String.format("%02d:%02d:%02d", mmin , msecond , second10Millis);
		
		setText(strTime);
		if (0 == tick10Millis) {
			if(expiredDelay > 0)
			{
				postDelayed(this, expiredDelay);
				expiredDelay = 0;
				if(!TextUtils.isEmpty(expiredText))
				{
					this.setText(expiredText);
				}
				
			}
			else
			{
				if(!TextUtils.isEmpty(afterExpiredText))
				{
					this.setText(afterExpiredText);
				}
				if(null != afterExpiredImage)
				{
					afterExpiredImage.setImageResource(afterExpiredImageResid);
				}
				if(null != mListenerOnTimeTextViewExpire)
				{
					mListenerOnTimeTextViewExpire.onExpire();
				}
			}
				
			
			return;
		}
		postDelayed(this, 10);
	}

	/**
	 * @return the expiredText
	 */
	public String getExpiredText() {
		return expiredText;
	}

	/**
	 * @param expiredText the expiredText to set
	 */
	public void setExpiredText(String expiredText) {
		this.expiredText = expiredText;
	}

	/**
	 * @return the afterExpiredText
	 */
	public String getAfterExpiredText() {
		return afterExpiredText;
	}

	/**
	 * @param afterExpiredText the afterExpiredText to set
	 */
	public void setAfterExpiredText(String afterExpiredText) {
		this.afterExpiredText = afterExpiredText;
	}

	/**
	 * @return the expiredDelay
	 */
	public int getExpiredDelay() {
		return expiredDelay;
	}

	/**
	 * @param expiredDelay the expiredDelay to set
	 */
	public void setExpiredDelay(int expiredDelay) {
		this.expiredDelay = expiredDelay;
	}

	public void setAfterExpiredImage(ImageView iv , int resid)
	{
		afterExpiredImage = iv;
		afterExpiredImageResid = resid;
	}
	
	
}
