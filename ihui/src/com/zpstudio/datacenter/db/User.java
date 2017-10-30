package com.zpstudio.datacenter.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zpstudio.contentprovider.IhuiProvider;


public class User {
	
	String phone_number="";
	String password="";
	
	long id;
	String name;
	String age;
	String sex;
	String url;
	String country;
	String province;
	String city;
	double credit;
	String invitation_code;
	int referee_id;
	String referee_invitation_code;
	
	
	public String getReferee_invitation_code() {
		return referee_invitation_code;
	}
	public void setReferee_invitation_code(String referee_invitation_code) {
		this.referee_invitation_code = referee_invitation_code;
	}
	public int getReferee_id() {
		return referee_id;
	}
	public void setReferee_id(int referee_id) {
		this.referee_id = referee_id;
	}
	public String getInvitation_code() {
		return invitation_code;
	}
	public void setInvitation_code(String invitation_code) {
		this.invitation_code = invitation_code;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean equals(User user)
	{
		
		if(!phone_number.equals(user.getPhone_number()))
		{
			return false;
		}
		
		if(!password.equals(user.getPassword()))
		{
			return false;
		}
		
		
		return true;
	}
	
	
	/* Keys Used by http */ 

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public String getDisplayPhoneNumber()
	{
		if (isMobile(phone_number)) {
			return phone_number;
		}else {
			return "未绑定";
		}
		
	}
	
	/**
	 * 手机号码的判断
	 * 
	 * @param mobiles
	 * @return true 为验证正确，false 为失败
	 */
	public static boolean isMobile(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(147)|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	public interface OnResultListener
	{
		void onSuccess();
		void onFail(String cause);
	}
	
	public static void saveToLocal(Context context , User user  , OnResultListener onResult)
	{
		//Delete all first
		if(null != user)
		{
		
			ContentValues values = new ContentValues();
			values.put(IhuiProvider.USER_TABLE_PHONE_NUMBER, user.getPhone_number());
			values.put(IhuiProvider.USER_TABLE_PASSWORD, user.getPassword());
			
			values.put(IhuiProvider.USER_TABLE_ID, user.getId());
			values.put(IhuiProvider.USER_TABLE_NAME, user.getName());
			values.put(IhuiProvider.USER_TABLE_SEX, user.getSex());
			values.put(IhuiProvider.USER_TABLE_AGE, user.getAge());
			values.put(IhuiProvider.USER_TABLE_URL, user.getUrl());
			values.put(IhuiProvider.USER_TABLE_COUNTRY, user.getCountry());
			values.put(IhuiProvider.USER_TABLE_PROVINCE, user.getProvince());
			values.put(IhuiProvider.USER_TABLE_CITY, user.getCity());
			values.put(IhuiProvider.USER_TABLE_CREDIT, user.getCredit());
			values.put(IhuiProvider.USER_TABLE_INVITATION_CODE, user.getInvitation_code());
			values.put(IhuiProvider.USER_TABLE_REFEREE_ID, user.getReferee_id());
			values.put(IhuiProvider.USER_TABLE_REFEREE_INVItATION_CODE, user.getReferee_invitation_code());
			
			
			context.getContentResolver().delete(Uri.parse(IhuiProvider.USER_CONTENT_URIS), 
					null, null);
			context.getContentResolver().insert(Uri.parse(IhuiProvider.USER_CONTENT_URIS), values);
		}
		context.getContentResolver().notifyChange(Uri.parse(IhuiProvider.USER_CONTENT_URIS), null);
	}
	public static User loadFromLocal(Context context)
	{
		User user = null;
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse(IhuiProvider.USER_CONTENT_URIS),
						null,
						null,
						null, 
						IhuiProvider.USER_TABLE_PHONE_NUMBER);
		
		if(cursor != null){   
            if(cursor.moveToNext()){   
            	user = new User();
            	user.setPhone_number(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_PHONE_NUMBER)));
            	user.setPassword(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_PASSWORD)));	
            	
            	user.setId(cursor.getLong(cursor.getColumnIndex(IhuiProvider.USER_TABLE_ID)));	
            	user.setName(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_NAME)));
            	user.setSex(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_SEX)));
            	user.setAge(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_AGE)));
            	user.setUrl(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_URL)));
            	user.setCountry(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_COUNTRY)));
            	user.setProvince(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_PROVINCE)));
            	user.setInvitation_code(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_INVITATION_CODE)));
            	user.setCity(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_CITY)));
            	user.setCredit(cursor.getDouble(cursor.getColumnIndex(IhuiProvider.USER_TABLE_CREDIT)));
            	user.setReferee_id(cursor.getInt(cursor.getColumnIndex(IhuiProvider.USER_TABLE_REFEREE_ID)));
                user.setReferee_invitation_code(cursor.getString(cursor.getColumnIndex(IhuiProvider.USER_TABLE_REFEREE_INVItATION_CODE)));
            } 
        }   
		
		if(null != cursor)
		{
			cursor.close();
		}
		return user;
	}
	
	public static void deleteAll(Context context)
	{
		context.getContentResolver().delete(Uri.parse(IhuiProvider.USER_CONTENT_URIS) ,"1=1" , null );
		context.getContentResolver().notifyChange(Uri.parse(IhuiProvider.USER_CONTENT_URIS), null);
		
	}
	
	
}
