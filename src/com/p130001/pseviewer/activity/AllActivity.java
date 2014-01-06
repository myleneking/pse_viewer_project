package com.p130001.pseviewer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.data.StockPreference;
import com.p130001.pseviewer.db.StockDB;

public class AllActivity extends MainActivity {

	public static void show(Context context) {
		Intent i = new Intent(context, AllActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StockPreference.saveActivityMode(Util.ALL);
		StockPreference.setSortBy(StockDB.COLUMN_SYMBOL);
		StockPreference.setSortMode("asc");
		
		if (isNetworkConnected()) {
			new GetApiData().execute();
		} else {
			if (StockPreference.loadDatabaseUpdateStatus()) {
				new LoadStockFromDatabase().execute();
				showAsOfDate();
			}
		}
	}
	
}
