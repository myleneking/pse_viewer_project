package com.p130001.pseviewer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.data.PSEPreferences;
import com.p130001.pseviewer.db.StockDB;
import com.p130001.pseviewer.task.ApiDataTask;
import com.p130001.pseviewer.task.LoadStockFromDatabaseTask;

public class AllActivity extends MainActivity {

	public static void show(Context context) {
		Intent i = new Intent(context, AllActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PSEPreferences.setActivityMode(Util.ALL);
		PSEPreferences.setListSortBy(StockDB.COLUMN_SYMBOL);
		PSEPreferences.setListSortMode("asc");
		
		if (isNetworkConnected()) {
			new ApiDataTask(AllActivity.this).execute();
		} else {
			if (PSEPreferences.getDBUpdateStatus()) {
				new LoadStockFromDatabaseTask(AllActivity.this).execute();
			}
		}
	}
	
}
