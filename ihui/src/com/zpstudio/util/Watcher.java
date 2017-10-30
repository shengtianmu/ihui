package com.zpstudio.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * �������࣬����ʱ������Native�����ӽ�������ӵ�ǰ��̣�
 * 
 * @author wangqiang
 * @date 2014-04-24
 */
public class Watcher {
	private static final int WHAT_SERVERSOCKET_CREATED = 0 ;
	private static final int WHAT_SERVERSOCKET_ACCEPTED = 1 ;
	private static final int WHAT_SERVERSOCKET_READ = 2 ;
	private static final int WHAT_SERVERSOCKET_EXCEPTION = 3 ;
	static final String TAG="Watcher";
	private Context mContext;
	private String userId;
	private String serviceOrActivity;
	
	public class SocketInfo
	{
		String ip;
		int port;
		
		public SocketInfo(String ip , int port)
		{
			this.ip = ip;
			this.port = port;
		}
		public String toString()
		{
			return ip + ":" + port;
		}
	}
	private Handler handler= new Handler()
	{
		List<String> commandArgs = new ArrayList<String>();
		Object resultO   = null;
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case WHAT_SERVERSOCKET_CREATED:
				Log.i(TAG,"WHAT_SERVERSOCKET_CREATED");
				
				resultO = msg.obj;
				handler.postDelayed(new Runnable(){
					
					@Override
					public void run() {
						String watchDogPath = copyLibs2Private("libwatchdog.so","libwatchdog.so");
						chmod(watchDogPath, "755");
						Log.e("watchDogPath", watchDogPath);
						commandArgs.add(watchDogPath);
						commandArgs.add(mContext.getApplicationContext().getPackageName());
						commandArgs.add(serviceOrActivity);
						commandArgs.add(Integer.toString(android.os.Process.myPid()));
						commandArgs.add(userId);
						commandArgs.add(((SocketInfo)resultO).toString());//listener ip:port
						runDaemon(commandArgs);
					}
					
				}, 1000);
				
				break;
			case WHAT_SERVERSOCKET_ACCEPTED:
				Log.i(TAG,"WHAT_SERVERSOCKET_ACCEPTED");
				break;
			case WHAT_SERVERSOCKET_READ:
				Log.e(TAG,"WHAT_SERVERSOCKET_READ");
				break;
			case WHAT_SERVERSOCKET_EXCEPTION:
				Log.e(TAG,"WHAT_SERVERSOCKET_EXCEPTION");
				break;
			default:
				break;
			}
		}
	};
	private Runnable thread = new Runnable() {
		
		ServerSocket buildSocket()
		{
			int port = 5000;
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				port ++;
			}
			return serverSocket;
		}
		void sendIndication(int what , Object o)
		{
			Message msg = handler.obtainMessage(what , o);
			handler.sendMessage(msg);
		}
		@Override
		public synchronized void run() {
			
			ServerSocket serverSocket;
			
			serverSocket = buildSocket();
			
			if(null != serverSocket)
			{
				sendIndication(WHAT_SERVERSOCKET_CREATED , new SocketInfo("127.0.0.1" , serverSocket.getLocalPort()));
		        
				try {
					Socket client = serverSocket.accept();
					sendIndication(WHAT_SERVERSOCKET_ACCEPTED , null);
					client.getInputStream().read();
					sendIndication(WHAT_SERVERSOCKET_READ , null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sendIndication(WHAT_SERVERSOCKET_READ , e.getMessage());
				}
			}

		}

	};
	private Thread serverThread;

	private synchronized void startServer() {
		if (serverThread == null) {
			serverThread = new Thread(thread);
			serverThread.start();
		}
	}

	private synchronized void stopServer() {
		if (serverThread != null) {
			Thread t = serverThread;
			serverThread = null;
			t.interrupt();
		}

	}

	public void createAppMonitor(String serviceOrActivity) {
		if (!createWatcher(getUserId(mContext), serviceOrActivity)) {
			Log.e("Watcher", "<<Monitor created failed>>");
		}
	}

	public Watcher(Context context) {
		mContext = context;
	}

	private String copyLibs2Private(String srcLibFileName,
			String destPrivateFileName) {
		String path = null;
		try {
			path = mContext.getApplicationContext().getFilesDir()
					.getAbsolutePath()
					+ "/" + destPrivateFileName; // data/dataĿ¼
			File file = new File(path);
			InputStream in = new FileInputStream(mContext
					.getApplicationContext().getFilesDir().getParentFile()
					.getPath()
					+ "/lib/" + srcLibFileName);
			FileOutputStream out = new FileOutputStream(file);
			int length = -1;
			byte[] buf = new byte[1024];
			while ((length = in.read(buf)) != -1) {
				out.write(buf, 0, length);
			}
			out.flush();
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			path = null;
		} // ��assetsĿ¼�¸���

		return path;
	}

	private String copyAssets2Private(String srcAssetsFileName,
			String destPrivateFileName) {
		String path = null;
		try {
			path = mContext.getApplicationContext().getFilesDir()
					.getAbsolutePath()
					+ "/" + destPrivateFileName; // data/dataĿ¼
			File file = new File(path);
			InputStream in = mContext.getAssets().open(srcAssetsFileName);
			FileOutputStream out = new FileOutputStream(file);
			int length = -1;
			byte[] buf = new byte[1024];
			while ((length = in.read(buf)) != -1) {
				out.write(buf, 0, length);
			}
			out.flush();
			in.close();
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			path = null;
		} // ��assetsĿ¼�¸���

		return path;

	}

	private boolean createWatcher(String userId, String serviceOrActivity) {
		this.userId = (null != userId) ? userId : "null";
		this.serviceOrActivity = serviceOrActivity;
		startServer();
		return true;
	}
	
	void runDaemon(List<String> args)
	{
		String argString = "";
		for (int i = 0; i < args.size(); i++) {
			String arg = args.get(i);
			if(null == arg)
			{
				Toast.makeText(mContext, "runDaemon:the " + i + " arg is null", Toast.LENGTH_LONG).show();
				return ;
			}
			argString += arg + " ";
		}
		Log.i(TAG, argString);
		
		try {
			new ProcessBuilder(args).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
	String getUserId(Context context) {
		Object userManager = context.getSystemService("user");
		if (userManager == null) {
			Log.e(TAG, "userManager not exsit !!!");
			return null;
		}

		try {
			Method myUserHandleMethod = android.os.Process.class.getMethod(
					"myUserHandle", (Class<?>[]) null);
			Object myUserHandle = myUserHandleMethod.invoke(
					android.os.Process.class, (Object[]) null);

			Method getSerialNumberForUser = userManager.getClass().getMethod(
					"getSerialNumberForUser", myUserHandle.getClass());
			long userSerial = (Long) getSerialNumberForUser.invoke(userManager,
					myUserHandle);
			return String.valueOf(userSerial);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "", e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "", e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, "", e);
		}

		return null;
	}
	public native int chmod(String file, String mode);
	public native int exec(String command);
	static {
		System.loadLibrary("watcher");
	}
}