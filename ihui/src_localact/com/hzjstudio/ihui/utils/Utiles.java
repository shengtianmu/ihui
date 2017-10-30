package com.hzjstudio.ihui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

public class Utiles {
	public static final int LOGIN = 1;
	public static final int AGENT_ADDRESS_LISTGET = 2;
	public static final int PASSWORD_CLAIM = 3;
	private static final String BASE_URL = "http://m.02727.cn:8080/webim";
	private static final String URL_LOGIN = "/do_login.jsp";
	private static final String URL_AGENTADDRESS_LISTGET = "/do_agentaddresslistget.jsp";
	private static final String URL_PASSWORD_CLAIM = "/do_passwordclaim.jsp";
	public static final String URL_PHONE_VERIFY = "/do_smsvalidation.jsp";

	/**
	 * 这个接口用来将异步下载好的数据返回给调用数据的activity
	 * 
	 * @author Administrator
	 * 
	 */
	public static interface getDataListener {
		void dataDownloadedSuccessfully(Object data);

		void dataDownloadFailed();
	}

	public static class MyAsyncTask extends AsyncTask<Integer, String, String> {
		private HttpPost httpPost;
		private HttpClient client;
		private HttpResponse response;
		private List<NameValuePair> myParams;
		private getDataListener dataListener;
		private String phoneNumber;
		private String password;

		public MyAsyncTask(String phoneNumber, String password) {
			super();
			this.phoneNumber = phoneNumber;
			this.password = password;
		}

		/**
		 * 对外提供的公共方法，设置监听器
		 * 
		 * @param dataListener
		 */
		public void setDataListener(getDataListener dataListener) {
			this.dataListener = dataListener;
		}

		@Override
		protected String doInBackground(Integer... params) {
			String uri = BASE_URL;
			switch (params[0]) {
			case LOGIN:
				uri += URL_LOGIN;
				break;
			case AGENT_ADDRESS_LISTGET:
				uri += URL_AGENTADDRESS_LISTGET;
				break;
			case PASSWORD_CLAIM:
				uri += URL_PASSWORD_CLAIM;
				break;
			default:
				break;
			}
			// uri = "http://m.02727.cn:8080/webim/do_login.jsp";
			httpPost = new HttpPost(uri);
			client = new DefaultHttpClient();
			// 设置HTTP POST请求参数必须用NameValuePair对象
			myParams = new ArrayList<NameValuePair>();
			myParams.add(new BasicNameValuePair("action", "login"));
			myParams.add(new BasicNameValuePair("phone_number", phoneNumber));
			myParams.add(new BasicNameValuePair("password", password));
			StringBuffer sb = new StringBuffer();
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(myParams,
						HTTP.UTF_8));
				response = client.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == 200) {
					byte[] buffer = new byte[1024];
					InputStream is = response.getEntity().getContent();
					int num = -1;
					while ((num = is.read(buffer)) != -1) {
						sb.append(new String(buffer, 0, num));
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			if (!"".equals(result)) {
				dataListener.dataDownloadedSuccessfully(result);
			} else {
				dataListener.dataDownloadFailed();
			}
			super.onPostExecute(result);
		}
	}

}
