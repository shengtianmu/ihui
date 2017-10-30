package com.zpstudio.contentprovider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.zpstudio.datacenter.db.Config;
public class IhuiProvider extends ContentProvider {
	
	public static final String AUTHORITY 				= "com.zpstudio.ihuiProvider";
	public static final String USER_CONTENT_URIS 		= "content://" + AUTHORITY + "/user";
	public static final String WALLET_CONTENT_URIS 		= "content://" + AUTHORITY + "/wallet";
	public static final String ADV_CONTENT_URIS 		= "content://" + AUTHORITY + "/adv";
	public static final String SETTING_CONTENT_URIS 	= "content://" + AUTHORITY + "/setting";
	public static final String LOCATION_CONTENT_URIS 	= "content://" + AUTHORITY + "/location";
	private static final Uri USER_CONTENT_URI      = Uri
			.parse(USER_CONTENT_URIS);
	private static final Uri WALLET_CONTENT_URI    = Uri
			.parse(WALLET_CONTENT_URIS);
	private static final Uri ADV_CONTENT_URI       = Uri
			.parse(ADV_CONTENT_URIS);
	private static final Uri SETTING_CONTENT_URI   = Uri
			.parse(SETTING_CONTENT_URIS);
	private static final Uri LOCATION_CONTENT_URI  = Uri
			.parse(LOCATION_CONTENT_URIS);
	
	private final static int VERSION = Config.DATABASE_VERSION_BBS + 13;
	public final static String DATABASE_NAME = "sq_mobile.db";
	
	
	/* user info */
	public final static String USER_TABLE    = "user";
	public final static String USER_TABLE_PHONE_NUMBER = "phone_number";
	public final static String USER_TABLE_PASSWORD = "password";
	public final static String USER_TABLE_ID = "id";
	public final static String USER_TABLE_AGE = "age";
	public final static String USER_TABLE_NAME = "name";
	public final static String USER_TABLE_SEX = "sex";
	public final static String USER_TABLE_URL = "url";
	public final static String USER_TABLE_COUNTRY = "country";
	public final static String USER_TABLE_PROVINCE = "province";
	public final static String USER_TABLE_CITY = "city";
	public final static String USER_TABLE_CREDIT = "credit";
	public final static String USER_TABLE_INVITATION_CODE = "invitation_code";
	public final static String USER_TABLE_REFEREE_ID = "referee_id";
	public final static String USER_TABLE_REFEREE_INVItATION_CODE = "referee_invitation_code";
	
	/*wallet info*/
	public final static String WALLET_TABLE 					= "wallet";
	public final static String WALLET_TABLE_PHONE_NUMBER 		= "phone_number";
	public final static String WALLET_TABLE_TOTAL_CREDIT 		= "total_credit";
	public final static String WALLET_TABLE_TOTAL_DONATE 		= "total_donate";
	public final static String WALLET_TABLE_REGISTER_CREDIT 	= "register_credit";
	public final static String WALLET_TABLE_UNLOCK_CREDIT  		= "unlock_credit";
	public final static String WALLET_TABLE_ATTENTION_CREDIT  	= "attention_credit";
	public final static String WALLET_TABLE_REFERRAL_CREDIT     = "referral_credit";
	public final static String WALLET_TABLE_EXCHANGE_CREDIT     = "exchange_credit";
	public final static String WALLET_TABLE_REWARD_CREDIT       = "reward_credit";
	public final static String WALLET_TABLE_PUNISH_CREDIT       = "punish_credit";
	public final static String WALLET_TABLE_NOTIFY_CREDIT       = "notify_credit";
	public final static String WALLET_TABLE_VERSION       		= "version";
	public final static String WALLET_TABLE_FORCEMSG       		= "forceMsg";
	public final static String WALLET_TABLE_TODAY_CREDIT        = "today_credit";
	public final static String WALLET_TABLE_TOTAL_INCOMING      = "total_incoming";
	
	/*adv info*/
	public final static String ADV_TABLE 						= "adv";
	public static final String ADV_TABLE_ADV_PHONE_ID		 	= "adv_phone_id";
	public static final String ADV_TABLE_ICLASS 				= "iClass";
	public static final String ADV_TABLE_CONTENT 				= "content";
	public static final String ADV_TABLE_LINK					= "link";
	public static final String ADV_TABLE_ISFREE					= "isFree";
	public static final String ADV_TABLE_CONTENTARRAY 			= "contentArray";
	public static final String ADV_TABLE_PRICE					= "price";
	
	/*setting info*/
	public final static String SETTING_TABLE 					= "setting";
	public final static String SETTING_TABLE_ENABLE_LOCKSCREEN  = "enable_lockscreen";
	
	/*location info*/
	public final static String LOCATION_TABLE 					= "location";
	public final static String LOCATION_TABLE_LNG               = "lng";
	public final static String LOCATION_TABLE_LAT               = "lat";
	public final static String LOCATION_TABLE_ADDRESS           = "address";
	
	
	// The underlying database
	private SQLiteDatabase DB;
	
	/* Map */
	private final UriTableSort[] mDbInfo = {
			new UriTableSort(USER_CONTENT_URI,
					USER_TABLE, USER_TABLE_PHONE_NUMBER),
			new UriTableSort(WALLET_CONTENT_URI,
					WALLET_TABLE, WALLET_TABLE_PHONE_NUMBER),
			new UriTableSort(ADV_CONTENT_URI,
					ADV_TABLE,
					ADV_TABLE_ADV_PHONE_ID),
			new UriTableSort(SETTING_CONTENT_URI,
					SETTING_TABLE, null),
			new UriTableSort(LOCATION_CONTENT_URI,
					LOCATION_TABLE, null),
			
			};

	protected UriTableSort findUriTableSort(Uri uri,
			UriTableSort[] uriTableSorts) {
		UriTableSort result = null;
		for (int i = 0; i < uriTableSorts.length; i++) {
			result = uriTableSorts[i];
			if (result.getUri().equals(uri)) {
				break;
			}
		}
		return result;
	}
	@Override
	public boolean onCreate() {
		Context context = getContext();

		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DATABASE_NAME, null, VERSION);
		DB = dbHelper.getWritableDatabase();
		return (DB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		UriTableSort uriTableSort = findUriTableSort(uri, mDbInfo);
		if (null != uriTableSort) {
			qb.setTables(uriTableSort.getTable());
			// If no sort order is specified sort by date / time /id
			String orderBy;
			if (TextUtils.isEmpty(sort)) {
				orderBy = uriTableSort.getDefaultSort();
			} else {
				orderBy = sort;
			}

			// Apply the query to the underlying database.
			Cursor c = qb.query(DB, projection, selection, selectionArgs, null,
					null, orderBy);

			// Register the contexts ContentResolver to be notified if
			// the cursor result set changes.
			c.setNotificationUri(getContext().getContentResolver(), uri);

			// Return a cursor to the query result.
			return c;
		} else {
			return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri _uri, ContentValues values) {
		UriTableSort uriTableSort = findUriTableSort(_uri, mDbInfo);
		if (null != uriTableSort) {
			// Insert the new row, will return the row number if
			// successful.
			long rowID = DB.insert(uriTableSort.getTable(), null, values);

			// Return a URI to the newly inserted row on success.
			if (rowID > 0) {
				Uri uri = ContentUris.withAppendedId(uriTableSort.getUri(),
						rowID);
				return uri;
			}

			throw new SQLException("Failed to insert row into " + _uri);
		} else {
			return null;
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int affectedRow = 0;
		UriTableSort uriTableSort = findUriTableSort(uri, mDbInfo);
		if (null != uriTableSort) {
			affectedRow = DB.delete(uriTableSort.getTable(), selection,
					selectionArgs);
		}
		return affectedRow;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int affectedRow = 0;
		UriTableSort uriTableSort = findUriTableSort(uri, mDbInfo);
		if (null != uriTableSort) {
			affectedRow = DB.update(uriTableSort.getTable(), values, selection,
					selectionArgs);
		}
		return affectedRow;

	}
	
	public class DatabaseHelper extends SQLiteOpenHelper {
		Context mContext;
		public DatabaseHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			mContext = context;
		}
		/**
		 * run sqlite script
		 * 
		 * @param context
		 * @param file
		 * @param db
		 */
		private void runscript(Context context, String file, SQLiteDatabase db) {
			try {
				InputStream in = context.getAssets().open(file);
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(in,"UTF-8"));
				String line = null;
				String sqlUpdate = "";
				boolean bSqlFinish = false;
				do{
					line = bufferedReader.readLine();
					
					if (!TextUtils.isEmpty(line) && !TextUtils.isEmpty(line.trim())) {
						sqlUpdate += line;
					}
					else
					{
						bSqlFinish = true;
					}

					if (bSqlFinish) {
						if (!TextUtils.isEmpty(sqlUpdate)) {
							db.execSQL(sqlUpdate);
							sqlUpdate = "";
						}
						bSqlFinish = false;
					}
				}while(line!=null);
				
				/* final check */
				if (bSqlFinish) {
					if (!TextUtils.isEmpty(sqlUpdate)) {
						db.execSQL(sqlUpdate);
						sqlUpdate = null;
					}
					bSqlFinish = false;
				}

				bufferedReader.close();
				in.close();
			} catch (SQLException e) {
			} catch (IOException e) {
			}
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			runscript(mContext, "ihui.sql", db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(newVersion == oldVersion){
				return;
			}
			onCreate(db);
		}

	}
	
	/* Map item */
	public class UriTableSort {
		Uri uri;
		String table;
		String defaultSort;

		public UriTableSort(Uri uri, String table, String defaultSort) {
			this.uri = uri;
			this.table = table;
			this.defaultSort = defaultSort;
		}

		/**
		 * @return the uri
		 */
		public Uri getUri() {
			return uri;
		}

		/**
		 * @param uri
		 *            the uri to set
		 */
		public void setUri(Uri uri) {
			this.uri = uri;
		}

		/**
		 * @return the table
		 */
		public String getTable() {
			return table;
		}

		/**
		 * @param table
		 *            the table to set
		 */
		public void setTable(String table) {
			this.table = table;
		}

		/**
		 * @return the defaultSort
		 */
		public String getDefaultSort() {
			return defaultSort;
		}

		/**
		 * @param defaultSort
		 *            the defaultSort to set
		 */
		public void setDefaultSort(String defaultSort) {
			this.defaultSort = defaultSort;
		}

	}
}
