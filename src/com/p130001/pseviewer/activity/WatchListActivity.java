package com.p130001.pseviewer.activity;

import com.p130001.pseviewer.StockPreference;
import com.p130001.pseviewer.StockTag;
import com.p130001.pseviewer.Util;

import android.content.Context;
import android.content.Intent;

public class WatchListActivity extends MainActivity {

	public static void show(Context context) {
		StockPreference.saveActivityMode(Util.WATCHLIST);
		Intent i = new Intent(context, WatchListActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(i);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		StockPreference.setSortBy(StockTag.SYMBOL);
		StockPreference.setSortMode("asc");
		
		if (StockPreference.loadDatabaseUpdateStatus()) {
			new LoadStockFromDatabase().execute();
		}
	}
	
}
