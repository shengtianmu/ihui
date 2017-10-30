package ethan.custom.verticalscrolllayout;

import java.util.Vector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 */
public class VerticalScrollLayout extends ViewGroup {

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mLastScreen = -1;
	private int mCurScreen;
	private int mDefaultScreen = 0;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;

	private static final int SNAP_VELOCITY = 600;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionY;

	private Vector<ScrollLayoutChangeListener> listeners;
    
	public VerticalScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		listeners = new Vector<ScrollLayoutChangeListener>();
	}

	public void addChangeListener(ScrollLayoutChangeListener listener) {
		listeners.add(listener);
	}

	public VerticalScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);

		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childTop = 0;
			final int childCount = getChildCount();

			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childHeight = childView.getMeasuredHeight();
					childView.layout(0, childTop, childView.getMeasuredHeight(), childTop + childHeight);
					childTop += childHeight;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int HeightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (HeightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(0, mCurScreen * height);
	}

	/**
	 * According to the position of current layout scroll to the destination
	 * page.
	 */
	public void snapToDestination() {
		final int screenHeight = getHeight();
		final int destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		mLastScreen = mCurScreen;
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollY() != (whichScreen * getHeight())) {

			final int delta = whichScreen * getHeight() - getScrollY();
			mScroller.startScroll(0, getScrollY(), 0, delta, Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout
		}
		for (ScrollLayoutChangeListener listener : listeners)
			listener.doChange(mLastScreen, whichScreen);
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(0, whichScreen * getHeight());
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
		else
		{
			for (ScrollLayoutChangeListener listener : listeners)
				listener.onScrollFinished(mLastScreen, mCurScreen);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if(getChildCount() == 1)
		{
			return true;
		}
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
		
		final int action = event.getAction();
		final float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaY = (int) (mLastMotionY - y);
			mLastMotionY = y;

			scrollBy(0, deltaY);
			break;

		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityY = (int) velocityTracker.getYVelocity();

			if (velocityY > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move up
				snapToScreen(mCurScreen - 1);
			} else if (velocityY < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
				// Fling enough to move down
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if(getChildCount() == 1)
		{
			return true;
		}
		
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int yDiff = (int) Math.abs(mLastMotionY - y);
			if (yDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;

			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return mTouchState != TOUCH_STATE_REST;
	}


}
