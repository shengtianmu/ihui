package com.hzjstudio.ihui;

import java.io.File;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener.Type;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.util.Log;

public class DownloadFileModel{
	private static final String TAG = "DownloadFileModel";
	Adv adv;
	boolean bInstalled = false;
	DownloadFileInfo downloadFileInfo;
	Context mContext;
	String packageName;
	public DownloadFileModel(Context context ,Adv adv)
	{
		this.mContext = context;
		this.adv    = adv;
		packageName = extractPackageName(adv.getLink());
		bInstalled  = checkApkInstalled(context,packageName);
		
		// register DownloadFileChangeListener, you may not care about to unregister the reference,because it is a 
        // WeakReference
        //FileDownloader.registerDownloadFileChangeListener(this);

        // init DownloadFileInfo if has been downloaded
        downloadFileInfo = FileDownloader.getDownloadFile(adv.getLink());
        
        if(null == downloadFileInfo)
        {
        	Log.i(TAG, adv.getLink() +" has no downloadFileInfo");
        }
	}
	/**
	 * @return the adv
	 */
	public Adv getAdv() {
		return adv;
	}
	/**
	 * @param adv the adv to set
	 */
	public void setAdv(Adv adv) {
		this.adv = adv;
	}
	
	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	/**
	 * @return the downloadFileInfo
	 */
	public DownloadFileInfo getDownloadFileInfo() {
		return downloadFileInfo;
	}
	/**
	 * @param downloadFileInfo the downloadFileInfo to set
	 */
	public void setDownloadFileInfo(DownloadFileInfo downloadFileInfo) {
		this.downloadFileInfo = downloadFileInfo;
	}
	public boolean isNew()
	{
		return (null == downloadFileInfo);
	}
	/**
	 * @return the bDownloading
	 */
	public boolean isDownloading() {
		if(isNew()) return false;
		boolean status = false;
		switch (downloadFileInfo.getStatus()) 
		{
	
	        // download file status:unknown
	        case Status.DOWNLOAD_STATUS_UNKNOWN:
	            break;
	
	        // download file status:error & paused
	        case Status.DOWNLOAD_STATUS_ERROR:
	        case Status.DOWNLOAD_STATUS_PAUSED:
	            break;
	
	        // download file status:file not exist
	        case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:
	            break;
	
	        // download file status:waiting & preparing & prepared & downloading
	        case Status.DOWNLOAD_STATUS_WAITING:
	        case Status.DOWNLOAD_STATUS_PREPARING:
	        case Status.DOWNLOAD_STATUS_PREPARED:
	        case Status.DOWNLOAD_STATUS_DOWNLOADING:
	        	status = true;
	            break;
	
	        // download file status:completed
	        case Status.DOWNLOAD_STATUS_COMPLETED:
	        	//downlaoded
	            break;
		}
		return status;
	}
	
	public boolean isPaused()
	{
		if(isNew()) return false;
		boolean status = false;
        switch (downloadFileInfo.getStatus()) 
        {

            // download file status:unknown
            case Status.DOWNLOAD_STATUS_UNKNOWN:
                break;

            // download file status:error & paused
            case Status.DOWNLOAD_STATUS_ERROR:
            case Status.DOWNLOAD_STATUS_PAUSED:
            	status = true;
                break;

            // download file status:file not exist
            case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:
                break;

            // download file status:waiting & preparing & prepared & downloading
            case Status.DOWNLOAD_STATUS_WAITING:
            case Status.DOWNLOAD_STATUS_PREPARING:
            case Status.DOWNLOAD_STATUS_PREPARED:
            case Status.DOWNLOAD_STATUS_DOWNLOADING:
            	//pause
                break;

            // download file status:completed
            case Status.DOWNLOAD_STATUS_COMPLETED:
                break;
        }
        return status;
        
	}
	/**
	 * @return the bDownloaded
	 */
	public boolean isDownloaded() {
		if(isNew()) return false;
		boolean status = false;
		switch (downloadFileInfo.getStatus()) {

        // download file status:unknown
        case Status.DOWNLOAD_STATUS_UNKNOWN:
            break;

        // download file status:error & paused
        case Status.DOWNLOAD_STATUS_ERROR:
        case Status.DOWNLOAD_STATUS_PAUSED:
            break;

        // download file status:file not exist
        case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST:
            break;

        // download file status:waiting & preparing & prepared & downloading
        case Status.DOWNLOAD_STATUS_WAITING:
        case Status.DOWNLOAD_STATUS_PREPARING:
        case Status.DOWNLOAD_STATUS_PREPARED:
        case Status.DOWNLOAD_STATUS_DOWNLOADING:
        	//pause
            break;

        // download file status:completed
        case Status.DOWNLOAD_STATUS_COMPLETED:
        	status = true;
            break;
		}
		return status;
	}
	public double getPercent()
	{
		if(isNew()) return 0;
		double percent = ((double) downloadFileInfo.getDownloadedSizeLong()) / ((double) downloadFileInfo
            .getFileSizeLong());
		return percent;
	}
	/**
	 * @return the bInstalled
	 */
	public boolean isbInstalled() {
		return bInstalled;
	}
	public void checkInstalled()
	{
		bInstalled  = checkApkInstalled(mContext,packageName);
	}
	public void startActivity ()
	{
		try{
			Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
			mContext.startActivity(intent);
		}catch(Exception e){
			Toast.makeText(mContext, "没有安装", Toast.LENGTH_LONG).show();
		}
	}
	public void install ()
	{
		if(isNew()) return;
		if(null == mContext)
		{
			Log.e(TAG, "install mContext is null");
			return;
		}
		Uri uri = Uri.fromFile(new File(downloadFileInfo.getFilePath()));
		Log.i(TAG, "install " + uri.toString());
		
		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(uri, "application/vnd.android.package-archive");

		mContext.startActivity(intent);
	}
	String extractPackageName(String link)
	{
		Uri uri = Uri.parse(link);
		return uri.getQueryParameter("packagename");
	}
	boolean fileIsExists(String path){
        try{
            File f=new File(path);
            if(!f.exists()){
                    return false;
            }
        }
        catch (Exception e) {
                // TODO: handle exception
                return false;
        }
        return true;
	}
	boolean checkApkInstalled(Context context, String packageName) 
	{
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			if(null != info)
			{
				return true;
			}
			else
			{
				return false;
			}
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	@Override
    protected void finalize() throws Throwable {
        // unregister, optional,not require
        //FileDownloader.unregisterDownloadFileChangeListener(this);
        super.finalize();
    }
	
	public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		if (downloadFileInfo != null && downloadFileInfo.getUrl() != null && downloadFileInfo.getUrl().equals(adv.getLink())) {
            this.downloadFileInfo = downloadFileInfo;
            Log.e("wlf", "onDownloadFileCreated,downloadFileInfo:" + downloadFileInfo);
        }
	}
	public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo,
			Type type) {
		// TODO Auto-generated method stub
		if (downloadFileInfo != null && downloadFileInfo.getUrl() != null && downloadFileInfo.getUrl().equals(adv.getLink())) {
            this.downloadFileInfo = downloadFileInfo;
            Log.e("wlf", "onDownloadFileUpdated,downloadFileInfo:" + downloadFileInfo);
        }
	}
	public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
		// TODO Auto-generated method stub
		if (downloadFileInfo != null && downloadFileInfo.getUrl() != null && downloadFileInfo.getUrl().equals(adv.getLink())) {
            this.downloadFileInfo = null;
            Log.e("wlf", "onDownloadFileDeleted,downloadFileInfo:" + downloadFileInfo);
        }
	}
}
