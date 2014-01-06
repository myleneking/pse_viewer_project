package com.p130001.pseviewer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PSEDatabase extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "pseviewer.db";
	private static final int DATABASE_VERSION = 1;
	
	public PSEDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + StockDB.TABLE_NAME);
		db.execSQL(StockDB.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PSEDatabase.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + StockDB.TABLE_NAME);
		onCreate(db);
	}
}

