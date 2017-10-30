package com.hzjstudio.ihui.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hzjstudio.ihui.entity.UserAndWallet;
import com.zpstudio.R;

public class ShouRu extends Activity {
	private WebView webView15;
	private TextView fanhui15;
	private UserAndWallet userAndWallet;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_hzjstudio__ihui_shouru);
		userAndWallet = UserAndWallet.getInstance();
		
		fanhui15 = (TextView) findViewById(R.id.fanhui15);
		fanhui15.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		webView15 = (WebView) findViewById(R.id.webView15);
		WebSettings settings = webView15.getSettings();
		settings.setJavaScriptEnabled(true);
		// WebView加载web资源
		webView15
				.loadUrl("http://m.02727.cn:8080/webim/web_transferdetail.jsp?phone_number="+userAndWallet.phone_number+"&password="+userAndWallet.password+"&lat=@&lng=@");
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView15.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
	}
}
