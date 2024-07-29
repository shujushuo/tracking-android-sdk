package com.zyxcba.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class DatabaseUtil {
	static final String TABLE_NAME = "abcdefgpapapa";
	
	final String TAG = "AnalysisAAABBB_DB";
	final String DB_NAME = "analysisaaabbbpapapa.db";
	final int VERSION = 1;
	
	private static volatile DatabaseUtil instance;
	
	private AtomicInteger mOpenCounter;
	private DBHelper mDBHelper;
	private SQLiteDatabase mSQLiteDatabase;
	
	private Context mContext;

	private DatabaseUtil(Context context) {
		mContext = context;
		mDBHelper = new DBHelper(context);
		mOpenCounter = new AtomicInteger();
	}

	public static DatabaseUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (DatabaseUtil.class) {
				if (instance == null) {
					instance = new DatabaseUtil(context);
				}
			}
		}
		return instance;
	}
	
	private void open () {
		if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
			Log.w(TAG, "Database was opened!" + mOpenCounter.get());
			return;
		}
		try {
			if (mOpenCounter.incrementAndGet() == 1) {
				mSQLiteDatabase = mDBHelper.getWritableDatabase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void close (){
		try {
			if (mSQLiteDatabase != null && !mSQLiteDatabase.isOpen()) {
				Log.w(TAG, "Database was closed!" + mOpenCounter.get());
				return;
			}
			if (mOpenCounter.decrementAndGet() == 0) {
				mSQLiteDatabase.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long insert(String table, ContentValues values) {
		if (mContext == null) {
			Log.w(TAG, "function insert context is null" + mOpenCounter.get());
			return -1;
		}
		try {
			open();
			if (mSQLiteDatabase == null)
				return -1;
			return mSQLiteDatabase.insert(table, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close();
		}
	}

	public void delete(String table, String whereClause, String[] whereArgs) {
		if (mContext == null) {
			Log.w(TAG, "function delete context is null" + mOpenCounter.get());
			return;
		}
		try {
			open();
			if (mSQLiteDatabase == null)
				return;
			mSQLiteDatabase.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public ArrayList<DBDataItem> queryWithLimit(String table, int count) {
		if (mContext == null) {
			Log.w(TAG, "function queryWithLimit context is null" + mOpenCounter.get());
			return null;
		}
		if (mSQLiteDatabase == null)
		{
			return null;
		}
		
		Cursor mycursor = null;
		ArrayList<DBDataItem> data= new ArrayList<DBDataItem>();
		String jsonStr = null;
		try {
			open();
			mycursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + table + " limit ?",
					new String[] { String.valueOf(count) });
			if (mycursor != null) {
				while (mycursor.moveToNext()) {
					int coIndex = mycursor.getColumnIndex("_id");
					int coIndexValue = mycursor.getColumnIndex("value");

					if (coIndexValue <0 || coIndex <0) {
						throw new Exception("colume index exception");
					}
					final int id = mycursor.getInt(coIndex);
					byte[] value = mycursor.getBlob(coIndexValue);

					JSONObject object = byteArray2JsonObj(value);
					LogUtil.debug(TAG, "query cache data row id is " + id);
					data.add(new DBDataItem(id,object));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mycursor != null)
				mycursor.close();
			close();
		}
		return data;
	}
	
	public long insert (ContentValues values) {
		synchronized (DatabaseUtil.class) {
			return insert(TABLE_NAME, values);
		}
	}
	
	public void deleteFromById (String id) {
		synchronized (DatabaseUtil.class) {
			delete(TABLE_NAME, "_id=?", new String[]{id});
		}
	}
	
	public ArrayList<DBDataItem> queryWithLimit(int count) {
		synchronized (DatabaseUtil.class) {
			return queryWithLimit(TABLE_NAME, count);
		}
	}
	
	private JSONObject byteArray2JsonObj(byte[] bytes) {

		JSONObject jsonObject = null;
		if (bytes != null) {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream), 8 * 1024);
			StringBuffer json_buffer = new StringBuffer();
			String line = "";
			try {
				while ((line = in.readLine()) != null) {
					json_buffer.append(line);
				}
				jsonObject = new JSONObject(json_buffer.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}
	
	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}
		
		@Override
		public SQLiteDatabase getReadableDatabase() {
			SQLiteDatabase db = null;
			try {
				db = super.getReadableDatabase();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return db;
		}

		@Override
		public SQLiteDatabase getWritableDatabase()
		{
			SQLiteDatabase db = null;
			try {
				db = super.getWritableDatabase();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return db;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}
		
		@Override
		public void onOpen(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ TABLE_NAME
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, what char, value BLOB);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO
		}
	}

	public class DBDataItem {
		public int id;
		public JSONObject json;
		public DBDataItem(int _id, JSONObject jsonStr) {
			this.id = _id;
			this.json = jsonStr;
		}
	}
}
