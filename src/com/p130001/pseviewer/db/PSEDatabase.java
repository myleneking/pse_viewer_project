package com.p130001.pseviewer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PSEDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "pse.db";
	private static final int DATABASE_VERSION = 1;
	
	public PSEDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(StockDB.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + StockDB.TABLE_NAME);
		onCreate(db);
	}
}

