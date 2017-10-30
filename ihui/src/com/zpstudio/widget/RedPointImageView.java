package com.zpstudio.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;
	/**
	 * 带红点的ImageView
	 * @author 元
	 *
	 */
public class RedPointImageView extends ImageView {
	public String rightText;
	public String redBgColor = "#ff0000";

	public RedPointImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	public RedPointImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (rightText != null) {
			Paint paint = new Paint();
			paint.setColor(Color.parseColor(redBgColor));
			int maxr = getWidth()> getHeight() ? getWidth() : getHeight();
			paint.setAntiAlias(true);// 抗锯齿
			paint.setStyle(Style.FILL);// 描边
			paint.setStrokeWidth(1);
			int r = maxr / 7; // 半径

			int cx = getWidth();
			// 画圆
			canvas.drawCircle(cx-r-2, r+4, r, paint);

			// 字
			// 中间的文字
			Paint cPaint = new Paint();
			cPaint.setColor(Color.WHITE);

			// 当前的
			if (rightText.length() > 1) {
				cPaint.setTextSize(r);
			} else {
				cPaint.setTextSize((float) (r * 1.3));
			}

			cPaint.setTextAlign(Align.CENTER);
			canvas.drawText(rightText, cx-r, (int) (r * 1.4), cPaint);
		}
	}

	public String getRightText() {
		return rightText;
	}

	public void setRightText(String rightText) {
		this.rightText = rightText;
		invalidate();
	}
	
	public String getRedBgColor() {
		return redBgColor;
	}

	public void setRedBgColor(String redBgColor) {
		this.redBgColor = redBgColor;
		invalidate();
	}

	
}
