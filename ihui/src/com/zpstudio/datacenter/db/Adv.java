package com.zpstudio.datacenter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.zpstudio.contentprovider.IhuiProvider;
import com.zpstudio.util.Log;
public class Adv {
	
	long adv_phone_id;
	int iClass;
	String content;
	String link;
	int isFree;
	byte[] contentArray;
	String des;
	double credit;
	int price;
	String desc1;
	
	public String getDesc1() {
		return desc1;
	}
	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public long getAdv_phone_id() {
		return adv_phone_id;
	}
	public void setAdv_phone_id(long adv_phone_id) {
		this.adv_phone_id = adv_phone_id;
	}
	public int getiClass() {
		return iClass;
	}
	public void setiClass(int iClass) {
		this.iClass = iClass;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getIsFree() {
		return isFree;
	}
	public void setIsFree(int isFree) {
		this.isFree = isFree;
	}
	public byte[] getContentArray() {
		return contentArray;
	}
	public void setContentArray(byte[] contentArray) {
		this.contentArray = contentArray;
	}
	public Bitmap getBitmap()
	{
		Bitmap bitmap = null;
		if(null != contentArray)
		{
			bitmap = BitmapFactory.decodeByteArray(contentArray, 0,contentArray.length);
		}
		return bitmap;
		
	}
	
	/**
	 * @return the des
	 */
	public String getDes() {
		return des;
	}
	/**
	 * @param des the des to set
	 */
	public void setDes(String des) {
		this.des = des;
	}
	/**
	 * @return the credit
	 */
	public double getCredit() {
		return credit;
	}
	/**
	 * @param credit the credit to set
	 */
	public void setCredit(double credit) {
		this.credit = credit;
	}

	class Result
	{
		public int errorCode;
		public String description;
		public Adv adv;
	}
	private static final String TAG                     = "Adv";
	private static final boolean DEBUG                  = true;
	
	
	public static void insert(Context context , Adv adv )
	{
		//Delete all first
		if(null != adv)
		{
		
			ContentValues values = new ContentValues();
			values.put(IhuiProvider.ADV_TABLE_ADV_PHONE_ID , adv.getAdv_phone_id());
			values.put(IhuiProvider.ADV_TABLE_CONTENT , adv.getContent());
			values.put(IhuiProvider.ADV_TABLE_ICLASS , adv.getiClass());
			values.put(IhuiProvider.ADV_TABLE_LINK , adv.getLink());
			values.put(IhuiProvider.ADV_TABLE_ISFREE , adv.getIsFree());
			values.put(IhuiProvider.ADV_TABLE_CONTENTARRAY , adv.getContentArray());
			values.put(IhuiProvider.ADV_TABLE_PRICE , adv.getPrice());

			context.getContentResolver().insert(Uri.parse(IhuiProvider.ADV_CONTENT_URIS), values);
			context.getContentResolver().notifyChange(Uri.parse(IhuiProvider.ADV_CONTENT_URIS), null);
		}
		else
		{
			if(DEBUG) Log.w(TAG, "saveToLocal: adv is null");
		}
		
	}
	public static void insert(Context context , List<Adv> list)
	{
		for(Adv adv:list)
		{
			insert(context , adv);
		}
	}
	public static void deleteAll(Context context)
	{
		context.getContentResolver().delete(Uri.parse(IhuiProvider.ADV_CONTENT_URIS), 
				null, null);
	}
	
	public static List<Adv> getAll(Context context)
	{
		List<Adv> list = new ArrayList<Adv>();
		Adv adv = null;
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse(IhuiProvider.ADV_CONTENT_URIS),
						null,
						null,
						null, 
						null);
		
		if(cursor != null){   
			if(cursor.moveToFirst()){
                do {
                	adv = new Adv();
                	adv.setAdv_phone_id(cursor.getLong(cursor.getColumnIndex(IhuiProvider.ADV_TABLE_ADV_PHONE_ID)));
                	adv.setContent(cursor.getString(cursor.getColumnIndex(IhuiProvider.ADV_TABLE_CONTENT)));
                	adv.setiClass(cursor.getInt(cursor.getColumnIndex(IhuiProvider.ADV_TABLE_ICLASS)));
                	adv.setIsFree(cursor.getInt(cursor.getColumnIndex(IhuiProvider.ADV_TABLE_ISFREE)));
                	adv.setLink(cursor.getString(cursor.getColumnIndex(IhuiProvider.ADV_TABLE_LINK)));
                	adv.setContentArray(cursor.getBlob(cursor.getColumnIndex(IhuiProvider.ADV_TABLE_CONTENTARRAY)));
                	adv.setPrice(cursor.getInt(cursor.getColumnIndex(IhuiProvider.ADV_TABLE_PRICE)));
                	list.add(adv);
                } while (cursor.moveToNext());
            }
			
			cursor.close();
        }   
		
		return list;
	}
	
    
}
