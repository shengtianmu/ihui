package com.zpstudio.datacenter.db.im;


import java.util.Comparator;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;

import com.zpstudio.util.PinyinUtils;

public class IMCellphoneContact {
	String phoneNumber;
	String name;
	String py;
	String fisrtSpell;
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the py
	 */
	public String getPy() {
		return py;
	}
	/**
	 * @param py the py to set
	 */
	public void setPy(String py) {
		this.py = py;
	}
	/**
	 * @return the fisrtSpell
	 */
	public String getFisrtSpell() {
		return fisrtSpell;
	}
	/**
	 * @param fisrtSpell the fisrtSpell to set
	 */
	public void setFisrtSpell(String fisrtSpell) {
		this.fisrtSpell = fisrtSpell;
	}
	
	public static void getAllAsync(final Context context , final OnCellphoneContactEvent onCellphoneContactEvent)
	{
		final int WHAT_STARTED 		= 0;
		final int WHAT_ADD  		= 1;
		final int WHAT_FINISHED  	= 2;
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				if(WHAT_STARTED == msg.what)
				{
					if(null != onCellphoneContactEvent)
					{
						onCellphoneContactEvent.onStarted();
					}
				}
				else if(WHAT_ADD == msg.what)
				{
					if(null != onCellphoneContactEvent)
					{
						onCellphoneContactEvent.onAdd((IMCellphoneContact)msg.obj);
					}
				}
				else if(WHAT_FINISHED == msg.what)
				{
					if(null != onCellphoneContactEvent)
					{
						onCellphoneContactEvent.onFinished();
					}
				}
			}
		};
		
		Thread thread = new Thread()
		{
			private void sendStartedInd()
			{
				handler.sendEmptyMessage(WHAT_STARTED);
			}
			private void sendFinishedInd()
			{
				handler.sendEmptyMessage(WHAT_FINISHED);
			}
			private void sendAddInd(Object obj)
			{
				Message msg = handler.obtainMessage(WHAT_ADD, obj);
				handler.sendMessage(msg);
			}
			public void run()
			{
				sendStartedInd();
				
				//得到contentresolver对象
				ContentResolver cr = context.getContentResolver();
				//取得电话本中开始一项的光标，必须先moveToNext()
				Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,null, null, null, null);
				// 循环遍历
	            while (cursor.moveToNext()) {
                    
                	int displayNameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int NumberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                	IMCellphoneContact contact = new IMCellphoneContact();
                	contact.setName(cursor.getString(displayNameColumn));
                	contact.setPy(PinyinUtils.getPingYin(contact.getName()));
                	contact.setFisrtSpell(PinyinUtils.getFirstSpell(contact.getName()));
                	contact.setPhoneNumber(cursor.getString(NumberColumn).replace("+86", "").replace("-", "").replace(" ", ""));
                	sendAddInd(contact);
	                    
	            }
	            cursor.close();
				
				sendFinishedInd();
			}
		};
		thread.start();

	}
	
	public interface OnCellphoneContactEvent
	{
		public void  onStarted();
		public void  onAdd(IMCellphoneContact contact);
		public void  onFinished();
	}
	
	
}
