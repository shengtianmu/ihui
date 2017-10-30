package com.zpstudio.ui.util.progressWeb;


import com.zpstudio.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {
	private ProgressBar progressbar;

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressbar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				5, 0, 0));
		progressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progressbar));
		addView(progressbar);
		setWebChromeClient(new ProgressWebChromeClient(progressbar));
	}

	public ProgressBar getProgressBar()
	{
		return progressbar;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	
}