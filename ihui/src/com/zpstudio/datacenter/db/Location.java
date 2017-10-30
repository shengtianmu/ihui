package com.zpstudio.datacenter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zpstudio.contentprovider.IhuiProvider;

public class Location {
	public double lng;
	public double lat;
	public String address;
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public static void save(Context context , 
			double lng,
			double lat,
			String address)
	{
		ContentValues values = new ContentValues();
		values.put(IhuiProvider.LOCATION_TABLE_LNG, lng);
		values.put(IhuiProvider.LOCATION_TABLE_LAT, lat);
		values.put(IhuiProvider.LOCATION_TABLE_ADDRESS, address);
		
		context.getContentResolver().delete(Uri.parse(IhuiProvider.LOCATION_CONTENT_URIS), 
				null, null);
		context.getContentResolver().insert(Uri.parse(IhuiProvider.LOCATION_CONTENT_URIS), values);
		
		context.getContentResolver().notifyChange(Uri.parse(IhuiProvider.LOCATION_CONTENT_URIS), null);
	}
	public static Location load(Context context)
	{
		Location location = null;
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse(IhuiProvider.LOCATION_CONTENT_URIS),
						null,
						null,
						null, 
						null);
		
		if(cursor != null){   
            if(cursor.moveToNext()){   
            	location = new Location();
            	location.setLng(cursor.getDouble(cursor.getColumnIndex(IhuiProvider.LOCATION_TABLE_LNG)));
            	location.setLat(cursor.getDouble(cursor.getColumnIndex(IhuiProvider.LOCATION_TABLE_LAT)));	
            	location.setAddress(cursor.getString(cursor.getColumnIndex(IhuiProvider.LOCATION_TABLE_ADDRESS)));	
            	
            }  
        }   
		
		if(null != cursor)
		{
			cursor.close();
		}
		return location;
	}
}
