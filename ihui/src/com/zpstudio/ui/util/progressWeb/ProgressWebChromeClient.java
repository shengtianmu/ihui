package com.zpstudio.ui.util.progressWeb;


import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class ProgressWebChromeClient extends WebChromeClient {
	ProgressBar progressbar = null;
	public ProgressWebChromeClient(View progressbar)
	{
		super();
		this.progressbar = (ProgressBar)progressbar;
	}
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		
		if(progressbar != null)
		{
			
			if (newProgress == 100) {
			} else {
				progressbar.setProgress(newProgress);
			}
			
		}
		super.onProgressChanged(view, newProgress);
	}
	
	//À©³ä»º´æµÄÈÝÁ¿  
    @Override
    public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater  quotaUpdater) {
 	   	quotaUpdater.updateQuota(spaceNeeded * 2);
    } 
}
