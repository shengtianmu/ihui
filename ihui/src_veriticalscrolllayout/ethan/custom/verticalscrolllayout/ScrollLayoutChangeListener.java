package ethan.custom.verticalscrolllayout;

public interface ScrollLayoutChangeListener {
	public void doChange(int lastIndex, int currentIndex);
	public void onScrollFinished(int lastIndex, int currentIndex);
}
