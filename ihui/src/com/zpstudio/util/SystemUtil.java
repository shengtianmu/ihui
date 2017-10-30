package com.zpstudio.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class SystemUtil {
	public static String TAG = "SystemUtil";
	// �жϵ�ǰ�豸�Ƿ���ģ����������TRUE����ǰ��ģ���������Ƿ���FALSE
	public static boolean isEmulator1(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (imei != null && imei.equals("000000000000000")) {
				return true;
			}
			return (Build.MODEL.equals("sdk"))
					|| (Build.MODEL.equals("google_sdk"));
		} catch (Exception ioe) {

		}
		return false;
	}

	/**
	 * deviceID�����Ϊ��������־+ʶ�����Դ��־+hash����ն�ʶ���
	 * 
	 * ������־Ϊ�� 1��andriod��a��
	 * 
	 * ʶ�����Դ��־�� 1�� wifi mac��ַ��wifi���� 2�� IMEI��imei���� 3�� ���кţ�sn���� 4��
	 * id������롣��ǰ��Ķ�ȡ����ʱ����������һ������룬��Ҫ���档
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {

		StringBuilder deviceId = new StringBuilder();
		// ������־
		deviceId.append("a");

		try {

			// wifi mac��ַ
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String wifiMac = info.getMacAddress();
			if (!isEmpty(wifiMac)) {
				deviceId.append("wifi");
				deviceId.append(wifiMac);
				Log.e("wifi : ", deviceId.toString());
				return deviceId.toString();
			}

			// IMEI��imei��
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (!isEmpty(imei)) {
				deviceId.append("imei");
				deviceId.append(imei);
				Log.e("getDeviceId : ", deviceId.toString());
				return deviceId.toString();
			}

			// ���кţ�sn��
			String sn = tm.getSimSerialNumber();
			if (!isEmpty(sn)) {
				deviceId.append("sn");
				deviceId.append(sn);
				Log.e("getDeviceId : ", deviceId.toString());
				return deviceId.toString();
			}

			// ������涼û�У� �����һ��id�������
			String uuid = getUUID(context);
			if (!isEmpty(uuid)) {
				deviceId.append("id");
				deviceId.append(uuid);
				Log.e("getDeviceId : ", deviceId.toString());
				return deviceId.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			deviceId.append("id").append(getUUID(context));
		}

		Log.e("getDeviceId : ", deviceId.toString());

		return deviceId.toString();

	}

	/**
	 * �õ�ȫ��ΨһUUID
	 */
	public static String getUUID(Context context) {
		String uuid = null;
		SharedPreferences mShare = getSysShare(context, "sysCacheMap");
		if (mShare != null) {
			uuid = mShare.getString("uuid", "");
		}

		if (isEmpty(uuid)) {
			uuid = UUID.randomUUID().toString();
			saveSysMap(context, "sysCacheMap", "uuid", uuid);
		}

		return uuid;
	}

	public static boolean isEmpty(String str) {
		if (null == str) {
			return true;
		}

		return str.isEmpty();
	}

	private static SharedPreferences getSysShare(Context context, String name) {
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	private static void saveSysMap(Context context, String name, String key,
			String value) {
		SharedPreferences sp = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		if (null != sp) {
			Editor editor = sp.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	/**
	 * ����Ƿ����SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static void triggerReturn() {
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				} catch (Exception e) {
					Log.e("Exception when onBack", e.toString());
				}
			}
		}.start();
	}

	public static int getStatusBarHeight(Context context) {
		/* @android:dimen/status_bar_height */
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static String getRandomPwd() {
		Random rd = new Random();
		String n = "";
		int getNum;
		do {
			getNum = Math.abs(rd.nextInt()) % 10 + 48;// ��������0-9�������
			// getNum = Math.abs(rd.nextInt())%26 + 97;//������ĸa-z�������
			char num1 = (char) getNum;
			String dn = Character.toString(num1);
			n += dn;
		} while (n.length() < 6);
		return n;
	}

	public static float getYuan(int fen) {
		float yuan = fen;
		return yuan / 100;
	}
	
	public static double getYuan(double fen) {
		return fen / 100;
	}

	public static int getFen(float yuan) {
		return (int) (yuan * 100);
	}

	public interface OnLocationCallback {
		public void onLocation(double lat, double lng);
	}

	public static void saveToSD(String filename, String content)
			throws Exception {
		saveToSD(filename, content.getBytes());
	}

	public static void saveToSD(String filename, byte[] content)
			throws Exception {
		// getExternalStorageDirectory()����ȡ��sd����·��
		File f = new File(Environment.getExternalStorageDirectory(), filename);
		FileOutputStream out2 = new FileOutputStream(f);
		out2.write(content);
		out2.close();
	}

	/**
	 * ��ӿ�ݷ�ʽ
	 * */
	public static void creatShortCut(Activity activity, Class<?> cls,
			String shortcutName, int resourceId) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		/* ����������Ϊ����ж��Ӧ�õ�ʱ��ͬʱɾ�������ݷ�ʽ */
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");

		Intent shortcutintent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// �������ظ�����
		shortcutintent.putExtra("duplicate", true);
		// ��Ҫ��ʵ�����
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
		// ���ͼƬ
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				activity.getApplicationContext(), resourceId);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// ������ͼƬ�����еĳ��������
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// ���͹㲥��OK
		activity.sendBroadcast(shortcutintent);
	}

	/**
	 * ɾ���ݷ�ʽ
	 * */
	public static void deleteShortCut(Activity activity, Class<?> cls,
			String shortcutName) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		// ��ݷ�ʽ�����
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
		// �����Ͽ������Ļ���һ�¼��䣬���Ե�ʱ���ֲ�����ɾ���ݷ�ʽ��
		// String appClass = activity.getPackageName()+"."+
		// activity.getLocalClassName();
		// ComponentName comp = new ComponentName( activity.getPackageName(),
		// appClass);
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new
		// Intent(Intent.ACTION_MAIN).setComponent(comp));
		/** �ĳ����·�ʽ�ܹ��ɹ�ɾ�������ɾ��ʹ�����Ҫ��Ӧ�����ҵ���ݷ�ʽ���ɹ�ɾ�� **/
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		activity.sendBroadcast(shortcut);
	}

	/**
	 * �ж��Ƿ���ڿ�ݷ�ʽ
	 * */
	public static boolean hasShortcut(Activity activity, String shortcutName) {
		String url = "";
		int systemversion = Integer.parseInt(android.os.Build.VERSION.SDK);
		/* ����8��ʱ����com.android.launcher2.settings ���ѯ��δ���ԣ� */
		if (systemversion < 8) {
			url = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			url = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		ContentResolver resolver = activity.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(url), null, "title=?",
				new String[] { shortcutName }, null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		return false;
	}

	/**
	 * ��pxֵת��Ϊdip��dpֵ����֤�ߴ��С����
	 * 
	 * @param pxValue
	 * @param scale
	 *            ��DisplayMetrics��������density��
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * ��dip��dpֵת��Ϊpxֵ����֤�ߴ��С����
	 * 
	 * @param dipValue
	 * @param scale
	 *            ��DisplayMetrics��������density��
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * ��pxֵת��Ϊspֵ����֤���ִ�С����
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            ��DisplayMetrics��������scaledDensity��
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * ��spֵת��Ϊpxֵ����֤���ִ�С����
	 * 
	 * @param spValue
	 * @param fontScale
	 *            ��DisplayMetrics��������scaledDensity��
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static View getContentView(Activity ac) {
		ViewGroup view = (ViewGroup) ac.getWindow().getDecorView();
		FrameLayout content = (FrameLayout) view
				.findViewById(android.R.id.content);
		return content.getChildAt(0);
	}

	
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}

	public static String im_formatDatetime(String time,boolean simple) {
		String format = "";
		Date thatDate = new Date(Long.parseLong(time) * 1000);
		Calendar thatCal=Calendar.getInstance();
		thatCal.setTime(thatDate);
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(new Date());
		
		if(nowCal.get(Calendar.YEAR) - thatCal.get(Calendar.YEAR) > 1)
		{
			if(simple)
			{
				format = "yyyy��";
			}
			else
			{
				format = "yyyy��MM��dd�� kk��mm��";
			}
		}
		else if(nowCal.get(Calendar.YEAR) - thatCal.get(Calendar.YEAR) == 1)
		{
			if(simple)
			{
				format = "ȥ��";
			}
			else
			{
				format = "ȥ��MM��dd�� kk��mm��";
			}
		}
		else if(nowCal.get(Calendar.MONTH) - thatCal.get(Calendar.MONTH) > 0  )
		{
			if(simple)
			{
				format = "MM��dd��";
			}
			else
			{
				format = "MM��dd�� kk��mm��";
			}
		}
		else if(nowCal.get(Calendar.DAY_OF_MONTH) - thatCal.get(Calendar.DAY_OF_MONTH) > 1)
		{
			if(simple)
			{
				format = "MM��dd��";
			}
			else
			{
				format = "MM��dd�� kk��mm��";
			}
		}
		else if(nowCal.get(Calendar.DAY_OF_MONTH) - thatCal.get(Calendar.DAY_OF_MONTH) == 1)
		{
			if(simple)
			{
				format = "����";
			}
			else
			{
				format = "���� kk��mm��";
			}
		}
		else
		{
			format = "kk��mm��";
		}
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(thatDate);
		
	}
	
	public static boolean isServiceRunning(Context context , String className) {  
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) { 
	    Log.i(TAG , service.service.getClassName());
	        if (className.equals(service.service.getClassName())) {  
	            return true;  
	        }  
	    }  
	    return false;  
	}
	
	public static String maskPswd(String s , int left) {
		if (null == s) {
			return s;
		}
		
		String des = "";
		for(int i = 0 ; i< s.length() ;i ++)
		{
			
			if(i < left || i < s.length() - left)
			{
				des+="*";
			}
			else
			{
				des +=s.charAt(i);
			}
			
		}
		return des;
	}
	public static String getResourceString(Context context , int id)
	{
		return context.getResources().getString(id);
	}
	
	public static int clearCacheFolder(File dir, long numDays) {      
		int deletedFiles = 0;     
		if (dir!= null && dir.isDirectory()) {         
			try {            
				for (File child:dir.listFiles()) {
					if (child.isDirectory()) {          
						deletedFiles += clearCacheFolder(child, numDays);      
					}
					if (child.lastModified() < numDays) { 
						if (child.delete()) {               
							deletedFiles++;       
						}
					}
				}         
			} catch(Exception e) {   
				e.printStackTrace();
			} 
		}   
		return deletedFiles; 
		  
	}
	
	public static String generateUnique()
	{
		Date dt = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(dt);
	}
	
	/**
     * ��鵱ǰ�����Ƿ����
     * 
     * @param context
     * @return
     */
    
	public static boolean isNetworkAvailable(Context context)
    {
        // ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ��?
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // ��ȡNetworkInfo����
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===״̬===" + networkInfo[i].getState());
                    System.out.println(i + "===����===" + networkInfo[i].getTypeName());
                    // �жϵ�ǰ����״̬�Ƿ�Ϊ����״̬
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
	
	
}
