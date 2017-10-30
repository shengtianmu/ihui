package com.zpstudio.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.zpstudio.R;

/**
 *@author coolszy
 *@date 2012-4-26
 *@blog http://blog.92coding.com
 */

public class UpdateManager
{
	
	/* 下载�?*/
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 有新版本 */
	private static final int NEED_UPDATE = 3;
	/* 无新版本 */
	private static final int NO_NEED_UPDATE = 4;
	
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数�?*/
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	/* 更新进度�?*/
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private boolean mIsSilence = true;
	ProgressDialog mUpdateCheckDialog = null;
	private String mUpdateCheckUrl = "";
	private String mServerIp="";
	private String mPort="";
	private static final String TAG = "UpdateManager";
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位�?
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
				//有新版本
			case NEED_UPDATE:
				// 显示提示对话�?
				showNoticeDialog(); 
				break;
				//无新版本
			case NO_NEED_UPDATE:
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context,String ip,String port)
	{
		mServerIp = ip;
		mPort = port;
		this.mContext = context;
	}

	/**
	 * 软件更新
	 */
	public void checkUpdate()
	{
		String web = "http://" + mServerIp +":" + mPort +"/client_get_latest_version.jsp";
		Log.i(TAG , "checkUpdate " + web);
		new FetchRemoteVersionInfoTask().execute(web);
		
	}

	/**
	 * 软件是否有更新版�?
	 * 
	 * @return
	 */
	private boolean isUpdate(String checkFile)
	{
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);

		try
		{
			// 把version.xml放到网络上，然后获取文件信息
			InputStream inStream = new FileInputStream(new File(checkFile));
			// 解析XML文件�?由于XML文件比较小，因此使用DOM方式进行解析
			ParseXmlService service = new ParseXmlService();
			mHashMap = service.parseXml(inStream);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (null != mHashMap)
		{
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
//			
//			Dialog alertDialog = new AlertDialog.Builder(mContext). 
//                    setTitle("Version Info"). 
//                    setMessage("Current is " +versionCode+ "; Latest is " + serviceCode).
//                    setPositiveButton(mContext.getString(R.string.ok), null).
//                    create(); 
//            alertDialog.show();
            
			// 版本判断
			if (0 == serviceCode || serviceCode > versionCode)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取软件版本�?
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog()
	{
		String msg = mContext.getString(R.string.soft_update_info);
		msg += mHashMap.get("name");
		// 
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(msg);
		// 更新
		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 显示下载对话
				showDownloadDialog();
			}
		});
		// 稍后更新
		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话�?
	 */
	private void showDownloadDialog()
	{
		// 构�?软件下载对话�?
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_updating);
		// 给下载对话框增加进度�?
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 设置取消状�?
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 下载文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软�?
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 *@date 2012-4-26
	 *@blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL("http://" + mServerIp + ":" + mPort + "/" + mHashMap.get("url"));
					Log.i(TAG , "apk url is " + url.getPath());
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(6*1000);  //设置链接超时时间6s
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入�?
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位�?
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下�?
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显�?
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
	
	/**
	 * 远程版本信息获取线程
	 */
	class FetchRemoteVersionInfoTask extends AsyncTask<String, String, String>{  
        //后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回�? 类型  
          
        @Override  
        protected void onPreExecute() {  
            //第一个执行方�? 
        	if(false == mIsSilence)
        	{
	        	mUpdateCheckDialog = ProgressDialog.show(mContext, 
	        			mContext.getString(R.string.soft_update_loading), 
	        			mContext.getString(R.string.soft_update_pls_wait),
	        	        true,false); 
        	}
            super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(String... params) {  
            //第二个执行方�?onPreExecute()执行完后执行  
        	String versionInfo = null;
        	String sUpdateCheckFileDir = null;
        	String sUpdateCheckFilePath = null;
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					sUpdateCheckFileDir = sdpath + "download";
					URL url = new URL(params[0]);
					// 创建连接
					this.publishProgress(url.toString());
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(6*1000);  //设置链接超时时间6s
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入�?
					InputStream is = conn.getInputStream();

					File file = new File(sUpdateCheckFileDir);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(sUpdateCheckFileDir, "info.xml");
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						if (numread <= 0)
						{
							// 下载完成
							sUpdateCheckFilePath = apkFile.getAbsolutePath();
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (true);// 点击取消就停止下�?
					fos.close();
					is.close();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				sUpdateCheckFilePath = null;
			} 
		
        	return sUpdateCheckFilePath;
			
        }  
  
        protected void onProgressUpdate(String... progress) {  
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {  
        	if(false == mIsSilence)
        	{
	        	mUpdateCheckDialog.dismiss();
	        	mUpdateCheckDialog = null;
        	}
        	Log.i(TAG ,"update_version info is " + result);
        	if(null != result && true == isUpdate(result))
        	{
        		// 有新版本
				mHandler.sendEmptyMessage(NEED_UPDATE);
        	}
        	else
        	{
        		// 没有新版�?
				mHandler.sendEmptyMessage(NO_NEED_UPDATE);
        	}
            super.onPostExecute(result);  
        }
   
    } 
}
