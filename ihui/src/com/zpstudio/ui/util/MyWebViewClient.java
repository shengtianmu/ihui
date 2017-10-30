package com.zpstudio.ui.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zpstudio.R;

public class MyWebViewClient extends WebViewClient
{
	static final String TAG = "MyWebViewClient";
	static final String ERROR_PAGE = "file:///android_asset/error.htm";
	View vHost = null;
	WebView mWebView  = null;
	View vProgressBar = null ;
	View vProgressImage = null;
	View vProgressText = null;
	
	Context context = null;
	boolean isError = false;
	boolean bShowError = false;
	
	StringBuffer sLog = null;
	Handler handler = new Handler();
	public MyWebViewClient(Context context , View vHost)
	{
		this(context , vHost , null , null);
	}
	public MyWebViewClient(Context context , View vHost , ArrayList<String> blackList )
	{
		this(context , vHost , blackList , null);
	}
	
	public MyWebViewClient(Context context , View vHost , ArrayList<String> blackList , StringBuffer sLog)
	{
		this.context = context;
		this.sLog = sLog;
		setvHost(vHost);
	}
	
	void println(String format, Object... args)
	{
		onLog(String.format(format+"\n\r", args));
	}
	void onLog(String msg)
	{
		if(null != sLog)
		{
			sLog.append(msg);
		}
	}
	public boolean getIsError()
	{
		return isError;
	}
	
	/**
	 * @param vHost the vHost to set
	 */
	private void setvHost(View vHost) {
		this.vHost = vHost;
		this.mWebView = (WebView)vHost.findViewById(R.id.content);
		configProgress(true ,false ,false);
	}
	public void enableShowError(boolean enable)
	{
		bShowError = enable;
	}
	public void configProgress(boolean bar ,boolean image , boolean text)
	{
		if(bar ) 
		{
			vProgressBar = vHost.findViewById(R.id.progressBar);
		}
		else
		{
			vProgressBar = null;
		}
		
		if(image ) 
		{
			vProgressImage = vHost.findViewById(R.id.progressImage);
		}
		else
		{
			vProgressImage = null;
		}
		
		if(text ) 
		{
			vProgressText = vHost.findViewById(R.id.progressText);
		}
		else
		{
			vProgressText = null;
		}
	}
	public void delayPageFinished(WebView view, String url)
	{
		if(isError && (!bShowError))
		{
			view.setVisibility(View.GONE);
		}
		else
		{
			view.setVisibility(View.VISIBLE);
		}
		
		if(null != vProgressBar)
		{
			vProgressBar.setVisibility(View.GONE);
		}
		if(null != vProgressImage)
		{
			vProgressImage.setVisibility(View.GONE);
		}
		if(null != vProgressText)
		{
			vProgressText.setVisibility(View.GONE);
		}
		view.clearCache(false);
	}
	public boolean onGoback()
	{
		
		if(mWebView.canGoBack())
		{
			mWebView.goBack();
			return true;
		}
		else
		{
			return false;
		}
		
	}
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		
		isError = false;
		view.setVisibility(View.INVISIBLE);
		if(null != vProgressBar)
		{
			vProgressBar.setVisibility(View.VISIBLE);
		}
		if(null != vProgressImage)
		{
			vProgressImage.setVisibility(View.VISIBLE);
		}
		if(null != vProgressText)
		{
			vProgressText.setVisibility(View.VISIBLE);
		}
    }
    
	public void onPageFinished(final WebView view, final String url) 
	{
		super.onPageFinished(view, url);
		delayPageFinished(view ,url);
		
		
    }
	
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		
		if (url == null) 
		{
			return false;  
		}
		println("[INFO]shouldOverrideUrlLoading(%s)", url);
		
		String trimedUrl = url.trim();
	    if(trimedUrl.startsWith("http:") || trimedUrl.startsWith("https:"))
	    {
	    	
	    	//Log.i(TAG , "shouldOverrideUrlLoading add " + url);
	        //mWebView.loadUrl(url);
	    	view.loadUrl(url);
	        return true;
	    }
	    else
	    {
	    	Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)); 
            if(null != context)
            {
            	context.startActivity(intent); 
            }
            return true;
	    }
	    
    }
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) 
	{
		//super.onReceivedSslError(view, handler, error);
		handler.proceed();
	}
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	{
		isError = true;
		super.onReceivedError(view, errorCode, description, failingUrl);
	}
	
}