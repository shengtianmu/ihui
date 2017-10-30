package com.zpstudio.datacenter.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class Transaction {
	public static final int ERRORCODE_SUCCESS = 0;
	public static final int ERRORCODE_FAILURE = 1;
	int errorCode ;
	String cause;
	
	public Transaction()
	{
		
	}
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}
	
	public String toXml()
	{
		String xml;
		xml = "<xml>";
		xml += "<transaction>";
		xml +="<errorCode>";
		xml += errorCode;
		xml +="</errorCode>";
		xml +="<cause>";
		xml += cause;
		xml +="</cause>";
		xml += "</transaction>";
		xml += "</xml>";
		return xml;
	}
	public interface OnResultListener
	{
		void onSuccess(String cause);
		void onFail(String cause);
	}
	
	private static final String KEY_CONTAINER     		= "transaction";
	private static final String KEY_ERRORCODE     		= "errorcode";
	private static final String KEY_CAUSE        		= "cause";
	
	private static Transaction parse(String filePath)
	{
		Transaction transaction = null;
		if( null != filePath)
		{
			try {
				InputStream inStream = new FileInputStream(new File(filePath));
				InputSource is = new InputSource(inStream);
				is.setEncoding("GBK");
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	//取得DocumentBuilderFactory实例
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);//解析输入流 得到Document实例
				
				Element rootElement = doc.getDocumentElement();
				NodeList items = rootElement.getElementsByTagName(KEY_CONTAINER);
				for (int i = 0; i < items.getLength(); i++) {
					transaction = new Transaction();
					Node item = items.item(i);
					NodeList properties = item.getChildNodes();
					for (int j = 0; j < properties.getLength(); j++) 
					{
						Node property = properties.item(j);
						String nodeName = property.getNodeName();
						if (nodeName.equals(KEY_ERRORCODE)) 
						{
							if(null == property.getFirstChild())
							{
								transaction.setErrorCode(ERRORCODE_FAILURE);							}
							else
							{
								transaction.setErrorCode(Integer.parseInt(property.getFirstChild().getNodeValue()));
							}
						} 
						else if (nodeName.equals(KEY_CAUSE)) 
						{
							if(null == property.getFirstChild())
							{
								transaction.setCause("超时");						}
							else
							{
								transaction.setCause(property.getFirstChild().getNodeValue());
							}
						}
						
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	//解析输入流 得到Document实例
		}
		return transaction;
	}
	private static class HttpTask extends AsyncTask<String, String, String>
	{
		Context context;
		OnResultListener onResultListener;
		public HttpTask(Context context , OnResultListener onResultListener)
		{
			super();
			this.context = context;
			this.onResultListener = onResultListener;
		}
		@Override
		protected String doInBackground(String... params) {
        	String sFileDir = null;
        	String sFilePath = null;
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					sFileDir = sdpath + "download";
					URL url = new URL(params[0]);
					// 创建连接
					this.publishProgress(url.toString());
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(6*1000);  //设置链接超时时间6s
					conn.connect();
					// 获取文件大小
					InputStream is = conn.getInputStream();

					File file = new File(sFileDir);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File tmpFile = new File(sFileDir, params[1]);
					FileOutputStream fos = new FileOutputStream(tmpFile);
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						if (numread <= 0)
						{
							// 下载完成
							sFilePath = tmpFile.getAbsolutePath();
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (true);
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
				sFilePath = null;
			} catch (IOException e)
			{
				e.printStackTrace();
				sFilePath = null;
			}
		
        	return sFilePath;
		}
		protected void onPostExecute(String result) {
			Transaction trans = parse(result);
			if(null == trans ) 
			{
				trans = new Transaction();
				trans.setErrorCode(ERRORCODE_FAILURE);
				trans.setCause("超时");
			}
			
			switch(trans.getErrorCode())
			{
			case ERRORCODE_SUCCESS:
				if(null != onResultListener)
				{
					onResultListener.onSuccess(trans.getCause());
				}
				break;
			case ERRORCODE_FAILURE:
				if(null != onResultListener)
				{
					onResultListener.onFail(trans.getCause());
				}
				break;
			default:
				if(null != onResultListener)
				{
					onResultListener.onFail("超时");
				}
				break;
				
			}
		}
	}
	
	public static final String URL_PRESENT_MONEY    = "/v2_0_client_present_money.jsp";
	public static final String KEY_GIVER     		= "giver";
	public static final String KEY_GETTER     		= "getter";
	public static final String KEY_MONEY     		= "money";
	public static final String KEY_PASSWORD    		= "password";
	public static void presentMoney(Context context , 
			String sGiver , 
			String sGetter , 
			String sMoney,
			String sPassword, 
			OnResultListener onResultListener)
	{
		
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(KEY_GIVER, sGiver);
		map.put(KEY_GETTER, sGetter);
		map.put(KEY_MONEY, sMoney);
		map.put(KEY_PASSWORD, sPassword);
		
		StringBuffer buffer = new StringBuffer();
        if (map != null && !map.isEmpty()) {
           for (Map.Entry<String, String> entry : map.entrySet()){
	            try {
					buffer.append(entry.getKey())
					          .append("=")
					          .append(URLEncoder.encode(entry.getValue(),"utf-8"))
					          .append("&");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           }
           buffer.deleteCharAt(buffer.length() - 1); 
        }
        
        String url = Config.getFullUrl(context, URL_PRESENT_MONEY) + "?" + buffer.toString();
        Log.i("HttpTask" , url);
        new HttpTask(context , onResultListener).execute(url,"present_money.xml");
	}
	
}
