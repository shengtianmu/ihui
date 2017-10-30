package com.zpstudio.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class SMSValidation {
	public interface OnResultListener
	{
		void onSuccess();
		void onFail(String cause);
	}
	public static void send( 
			OnResultListener onResult , 
			String url)
	{
		new HttpTask(onResult).execute(url);
	}
	
	private static boolean isSuccess(String rsp)
	{
		String [] splits = rsp.split("&");
		for(int i=0; i < splits.length ;i++ )
		{
			String keyValue[] = splits[i].split("=");
			String key=null;
			String value=null;
			if(keyValue.length > 0)
			{
				key = keyValue[0];
			}
			
			if(keyValue.length > 1)
			{
				value = keyValue[1];
			}
			
			if( (key != null && key.equals("stat") )
			   &&value != null && key.equals("100") 	)
			{
				return true;
			}
			
		}
		
		return false;
	}
	private static class HttpTask extends AsyncTask<String, String, String>
	{
		
		
		OnResultListener onResult;
		public HttpTask(OnResultListener onResult)
		{
			super();
			this.onResult = onResult;
		}
		@Override
		protected String doInBackground(String... params) {
			//组建请求
			String straddr = params[0];
			String inputline = "";
			StringBuffer sb = new StringBuffer(straddr);
			
			//发送请求
			URL url;
			try {
				url = new URL(sb.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));
				
				//返回结果
				inputline = in.readLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return inputline;
		}
		protected void onPostExecute(String result) {
			boolean bSuccess = isSuccess(result);
			if(null != onResult)
			{
				if(bSuccess)
				{
					onResult.onSuccess();
				}
				else
				{
					onResult.onFail(result);
				}
			}
		}
	}

}
