package com.zpstudio.datacenter.db;

import android.content.Context;
import android.net.Uri;

public class Config {
	
	private static final String REAL_IP ="02727.com.cn";
	private static final String PORT = "8080";
	private static final String REAL_URL = "http://" + REAL_IP + ":" + PORT;
	private static final String UPLOAD_FILES_PATH = REAL_URL + "/adv_uploads/";
	public static final int DATABASE_VERSION_IM = 12;
	public static final int DATABASE_VERSION_BBS = 13;
	
	public static final String INTENT_PUZZLE_GAME_WIN = "com.zpstudio.puzzlegamewin";
	public static final String EXTRA_PUZZLE_GAME_ID   = "id";
	
	public static String getFullUrl(Context context , String relativePath)
	{
		
    	Uri content_url = Uri.parse(relativePath);
    	
		if((null == content_url) || (null == content_url.getScheme()))
		{
			return REAL_URL + relativePath;
		}
		else
		{
			return relativePath;
		}
	}
	public static String getFullAdvImageUrl(Context context , String relativePath)
	{
		
		Uri content_url = Uri.parse(relativePath);
    	
		if((null == content_url) || (null == content_url.getScheme()))
		{
			return UPLOAD_FILES_PATH + relativePath;
		}
		else
		{
			return relativePath;
		}
	}
	
	public static String  getPort(Context context )
	{
		return PORT;
	}
	public static String getServerName(Context context )
	{
		
		return REAL_IP ;
	}
	
}