package com.zpstudio.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.hzjstudio.ihui.ActivityTabFragment;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zpstudio.datacenter.db.Adv;
import com.zpstudio.datacenter.db.im.IMCellphoneContact;
import com.zpstudio.datacenter.db.op.IhuiClientApi;
import com.zpstudio.datacenter.db.op.IhuiClientApi.ListenerOnGetFixedAdvList;
import com.zpstudio.util.CrashHandler;

import de.mindpipe.android.logging.log4j.LogConfigurator;

import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloadConfiguration.Builder;
import org.wlf.filedownloader.FileDownloader;

public class MainApplication extends Application{
	private static final String TAG = "MainApplication";
    public static final int NOTIFICATION_ID_CREDIT = 0 ;
	
	public static final int NOTIFICATION_ID_UPDATE = 1 ;
		
    public static final int NOTIFICATION_ID_STATUS = 2;
    
    public static final String LOG_FILE = Environment.getExternalStorageDirectory().toString() + "/ihui/debug/log/myapp.log";
    public static final String CRASH_FILE_FOLDER = Environment.getExternalStorageDirectory().toString() + "/ihui";
    
    private static MainApplication instance;
	public static LocationClient gLocationClient;
	//public GeofenceClient mGeofenceClient;
	
	public static List<IMCellphoneContact> mPhoneContactList = new ArrayList<IMCellphoneContact>();
	
	public static boolean debugMode = true;
	
	final public LogConfigurator logConfig = new LogConfigurator();
	
	public static final Class<?> LINKED_CLASS = ActivityTabFragment.class;
	
	public static int is=0;
	
	public static int getIs() {
		return is;
	}


	public static void setIs(int is) {
		MainApplication.is = is;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		gLocationClient = new LocationClient(this.getApplicationContext());
		//mGeofenceClient = new GeofenceClient(getApplicationContext());
//		CrashHandler.getInstance().init(getApplicationContext(),CRASH_FILE_FOLDER);
		initImageLoader(this);
		initFileDownloader();
		logConfig.setFileName(LOG_FILE);
		logConfig.setRootLevel(Level.ALL);
		logConfig.configure();
		
		initAdv();
	}
	
	


	private void initAdv() {
		// TODO Auto-generated method stub
		
		IhuiClientApi.getInstance(this).refreshHangOnAdvAsync();
	}
	
	
	
	@Override
    public void onTerminate() {
        super.onTerminate();
        // release FileDownloader
        releaseFileDownloader();
        
    }

    // init FileDownloader
    private void initFileDownloader() {
        // 1.create FileDownloadConfiguration.Builder
        Builder builder = new FileDownloadConfiguration.Builder(this);
        // 2.config FileDownloadConfiguration.Builder
        builder.configFileDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "FileDownloader2");// config the download path
        builder.configDownloadTaskSize(3);// allow 3 download task at the same time
        FileDownloadConfiguration configuration = builder.build();// build FileDownloadConfiguration with the builder
        // 3.init FileDownloader with the configuration
        FileDownloader.init(configuration);
    }

    // release FileDownloader
    private void releaseFileDownloader() {
        FileDownloader.release();
    }
    
    // init ImageLoader
    private void initImageLoader(){
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }
	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		/*
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		*/
		
		if(ImageLoader.getInstance().isInited())
		{
			ImageLoader.getInstance().destroy();
		}
		
		String CACHE_DIR = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/.temp_tmp";
		new File(CACHE_DIR).mkdirs();

		File cacheDir = StorageUtils.getOwnCacheDirectory(context,CACHE_DIR);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
				.defaultDisplayImageOptions(defaultOptions)
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.memoryCache(new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		ImageLoader.getInstance().init(config);
	
	}
	
	public static MainApplication getInstance()
	{
		return instance;
	}
}
