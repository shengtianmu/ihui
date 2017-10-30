package com.zpstudio.datacenter.db;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zpstudio.contentprovider.IhuiProvider;

public class Wallet {
	
	String phone_number;
	double    total_credit = 0;
	double    total_donate = 0;
	int    register_credit = 0;
	int    unlock_credit = 0;
	int    attention_credit = 0;
	int    referral_credit = 0;
	int    exchange_credit = 0;
	int    reward_credit = 0;
	int    punish_credit = 0;
	int    notify_credit = 0;
	int    version = 0;
	String forceMsg;
	double today_credit;
	double total_incoming;
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public double getTotal_credit() {
		return total_credit;
	}
	public void setTotal_credit(double total_credit) {
		this.total_credit = total_credit;
	}
	public double getTotal_donate() {
		return total_donate;
	}
	public void setTotal_donate(double total_donate) {
		this.total_donate = total_donate;
	}
	public int getRegister_credit() {
		return register_credit;
	}
	public void setRegister_credit(int register_credit) {
		this.register_credit = register_credit;
	}
	public int getUnlock_credit() {
		return unlock_credit;
	}
	public void setUnlock_credit(int unlock_credit) {
		this.unlock_credit = unlock_credit;
	}
	public int getAttention_credit() {
		return attention_credit;
	}
	public void setAttention_credit(int attention_credit) {
		this.attention_credit = attention_credit;
	}
	public int getReferral_credit() {
		return referral_credit;
	}
	public void setReferral_credit(int referral_credit) {
		this.referral_credit = referral_credit;
	}
	public int getExchange_credit() {
		return exchange_credit;
	}
	public void setExchange_credit(int exchange_credit) {
		this.exchange_credit = exchange_credit;
	}
	public int getReward_credit() {
		return reward_credit;
	}
	public void setReward_credit(int reward_credit) {
		this.reward_credit = reward_credit;
	}
	public int getPunish_credit() {
		return punish_credit;
	}
	public void setPunish_credit(int punish_credit) {
		this.punish_credit = punish_credit;
	}
	
	/**
	 * @return the notify_credit
	 */
	public int getNotify_credit() {
		return notify_credit;
	}
	/**
	 * @param notify_credit the notify_credit to set
	 */
	public void setNotify_credit(int notify_credit) {
		this.notify_credit = notify_credit;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public String getForceMsg() {
		return forceMsg;
	}
	public void setForceMsg(String forceMsg) {
		this.forceMsg = forceMsg;
	}

	public double getToday_credit() {
		return today_credit;
	}
	public void setToday_credit(double today_credit) {
		this.today_credit = today_credit;
	}
	
	public double getTotal_incoming() {
		return total_incoming;
	}
	public void setTotal_incoming(double total_incoming) {
		this.total_incoming = total_incoming;
	}

	class Result
	{
		public int errorCode;
		public String description;
		public Wallet wallet;
	}
	
	public static String REQUEST_TYPE_NONE = "0";
	public static String REQUEST_TYPE_UNLOCK = "1";
	public static String REQUEST_TYPE_ATTENTION = "2";
	private static final String URL_UPDATE_WALLET 		= "/webim/do_updatewallet.jsp";
	
	public static void saveToLocal(Context context , Wallet wallet )
	{
		//Delete all first
		if(null != wallet)
		{
		
			ContentValues values = new ContentValues();
			values.put(IhuiProvider.WALLET_TABLE_PHONE_NUMBER , wallet.getPhone_number());
			values.put(IhuiProvider.WALLET_TABLE_TOTAL_CREDIT , wallet.getTotal_credit());
			values.put(IhuiProvider.WALLET_TABLE_REGISTER_CREDIT , wallet.getRegister_credit());
			values.put(IhuiProvider.WALLET_TABLE_UNLOCK_CREDIT , wallet.getUnlock_credit());
			values.put(IhuiProvider.WALLET_TABLE_ATTENTION_CREDIT , wallet.getAttention_credit());
			values.put(IhuiProvider.WALLET_TABLE_REFERRAL_CREDIT , wallet.getReferral_credit());
			values.put(IhuiProvider.WALLET_TABLE_EXCHANGE_CREDIT , wallet.getExchange_credit());
			values.put(IhuiProvider.WALLET_TABLE_REWARD_CREDIT , wallet.getReward_credit());
			values.put(IhuiProvider.WALLET_TABLE_PUNISH_CREDIT , wallet.getPunish_credit());
			values.put(IhuiProvider.WALLET_TABLE_NOTIFY_CREDIT , wallet.getNotify_credit());
			values.put(IhuiProvider.WALLET_TABLE_VERSION , wallet.getVersion());
			values.put(IhuiProvider.WALLET_TABLE_FORCEMSG , wallet.getForceMsg());
			values.put(IhuiProvider.WALLET_TABLE_TODAY_CREDIT , wallet.getToday_credit());
			values.put(IhuiProvider.WALLET_TABLE_TOTAL_INCOMING , wallet.getTotal_incoming());
			
			context.getContentResolver().delete(Uri.parse(IhuiProvider.WALLET_CONTENT_URIS), 
					null, null);
			context.getContentResolver().insert(Uri.parse(IhuiProvider.WALLET_CONTENT_URIS), values);
			context.getContentResolver().notifyChange(Uri.parse(IhuiProvider.WALLET_CONTENT_URIS), null);
		}
		
	}
	public static void updateFromWeb(final Context context , 
			String phone_number , 
			String requestType,
			String requestAdvPhone,
			final StringBuffer sbLog)
	{
		Location location = Location.load(context);
		Map<String, String> map = new HashMap<String, String>();
		map.put("phone_number", phone_number);
		if(null != requestType)
		{
			map.put("action_type", requestType);
		}
		if(null != requestAdvPhone)
		{
			map.put("adv_phone_id", requestAdvPhone);
		}
		if(null != location)
		{
			map.put("lng", Double.toString(location.getLng()));
			map.put("lat", Double.toString(location.getLat()));
		}
		StringBuffer buffer = new StringBuffer();
        if (map != null && !map.isEmpty()) {
           for (Map.Entry<String, String> entry : map.entrySet()){
        	   buffer.append(entry.getKey())
		          .append("=")
		          .append(entry.getValue())
		          .append("&");
           }
           buffer.deleteCharAt(buffer.length() - 1); 
        }
        
        String url = Config.getFullUrl(context, URL_UPDATE_WALLET + "?" + buffer.toString());
        println(sbLog, "updateFromWeb:"+ url);
        AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				println(sbLog , "Wallet:updateFromWeb:onFailure");
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				
				try {
					String json = new String(arg2 , "UTF-8");
					//println(sbLog , "Wallet:updateFromWeb:onSuccess:"+ json);
					println(sbLog , "---------------:");
					Result result = new Gson().fromJson(json ,Result.class);
					println(sbLog , "++++++++++++++++:");
					if(0 == result.errorCode)
					{
						println(sbLog , "Wallet:updateFromWeb:onSuccess:errorCode:"+ result.errorCode);
						Wallet wallet = result.wallet;
						Wallet.saveToLocal(context, wallet);
						
					}
					else
					{
						println(sbLog , "Wallet:updateFromWeb:onSuccess:errorCode is not 0:"+ result.description);
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					println(sbLog , "Wallet:updateFromWeb:UnsupportedEncodingException:"+e.getMessage());
				} catch (JsonSyntaxException e){
					e.printStackTrace();
					println(sbLog , "Wallet:updateFromWeb:JsonSyntaxException:"+e.getMessage());
				} catch (Exception e)
				{
					println(sbLog , "Wallet:updateFromWeb:Exception:"+e.getMessage());
				}
				
			}
			
		});
	}
	
	public static boolean isExist(Context context , String phone_number)
	{
		boolean bExist = false;
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse(IhuiProvider.WALLET_CONTENT_URIS),
						null,
						IhuiProvider.WALLET_TABLE_PHONE_NUMBER + "='" + phone_number + "'",
						null, 
						IhuiProvider.WALLET_TABLE_PHONE_NUMBER);
		
		if(cursor != null){   
			bExist = true;
        }   
		
		if(null != cursor)
		{
			cursor.close();
		}
		return bExist;
	}
	
	public static Wallet loadFromLocal(Context context)
	{
		Wallet wallet = null;
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse(IhuiProvider.WALLET_CONTENT_URIS),
						null,
						null,
						null, 
						null);
		
		if(cursor != null){   
            if(cursor.moveToNext()){   
            	wallet = new Wallet();
            	wallet.setPhone_number(cursor.getString(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_PHONE_NUMBER)));
            	wallet.setTotal_credit(cursor.getDouble(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_TOTAL_CREDIT)));
            	wallet.setTotal_donate(cursor.getDouble(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_TOTAL_DONATE)));
            	wallet.setRegister_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_REGISTER_CREDIT)));
            	wallet.setUnlock_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_UNLOCK_CREDIT)));
            	wallet.setAttention_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_ATTENTION_CREDIT)));
            	wallet.setReferral_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_REFERRAL_CREDIT)));
            	wallet.setExchange_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_EXCHANGE_CREDIT)));
            	wallet.setReward_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_REWARD_CREDIT)));
            	wallet.setPunish_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_PUNISH_CREDIT)));
            	wallet.setNotify_credit(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_NOTIFY_CREDIT)));
            	wallet.setVersion(cursor.getInt(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_VERSION)));
            	wallet.setForceMsg((cursor.getString(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_FORCEMSG))));
            	wallet.setToday_credit((cursor.getDouble(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_TODAY_CREDIT))));
            	wallet.setTotal_incoming((cursor.getDouble(cursor.getColumnIndex(IhuiProvider.WALLET_TABLE_TOTAL_INCOMING))));
            	
            }  
        }   
		
		if(null != cursor)
		{
			cursor.close();
		}
		return wallet;
	}
	
	private static void println(StringBuffer sb , String msg)
	{
		if(null != sb)
		{
			sb.append(msg + "\n\r");
		}
	}
	
	
}
