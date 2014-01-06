package com.p130001.pseviewer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.p130001.pseviewer.StockPreference;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.db.StockDB;

public class LoserActivity extends MainActivity {

	public static void show(Context context) {
		StockPreference.saveActivityMode(Util.LOSER);
		Intent i = new Intent(context, LoserActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StockPreference.setSortBy(StockDB.COLUMN_SYMBOL);
		StockPreference.setSortMode("asc");
		
		if (StockPreference.loadDatabaseUpdateStatus()) {
			new LoadStockFromDatabase().execute();
		}
	}

}
