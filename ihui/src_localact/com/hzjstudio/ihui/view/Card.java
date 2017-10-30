package com.hzjstudio.ihui.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.zpstudio.R;

public class Card extends Activity {
	private WebView webView;
	private TextView fanhui;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_item_webview);

		fanhui = (TextView) findViewById(R.id.fanhui);
		fanhui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		webView = (WebView) findViewById(R.id.webView);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if (newProgress == 100) {
					// 网页加载完成

				} else {
					// 加载中

				}

			}
		});
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// WebView加载web资源
		webView.loadUrl("https://creditcard.cmbc.com.cn/online/Mobile/QRCodePages/ELHome.aspx?recommendInfo=SPapUhcdg1tN%252bhUV2wm%252fsaWtKJ5h%252bHuMwyy%252bpfxEi1U7zQ9vajAYaTHNKt4c1SCMlxa%252f9VMfVYppcvpMiX%252fkXTqz54LfmEqD69O2H1M3%252bHWoM9HQu%252bus9qKW9Knn83Zpq7exY%252fSmWLr0B%252b8kaJ9CaqOJd%252fL5VP8dM14bQTK19p5QRBKjU0UVRcBfyJVo9REW0BYEpBrvKxCCyCVjnIl%252fuaoSKSx99LcrkzlYIZ63r6Vt%252fsD4MGfaNx1hnEH9Re%252bFGr%252bxnex1hh2IiAI2ijZLp9HC3%252fk0g3c9%252bbzrE64UjP%252fuB%252fIM6nFTd5MZrOQYXpVBzx3e2dLQg7pLA%252be4Msk8KQ%253d%253d&from=timeline&isappinstalled=0#rd");
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
	}

	// 改写物理按键——返回的逻辑
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();// 返回上一页面
				return true;
			} else {
				System.exit(0);// 退出程序
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
