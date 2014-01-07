package com.p130001.pseviewer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.data.PSEPreferences;
import com.p130001.pseviewer.db.StockDB;
import com.p130001.pseviewer.task.LoadStockFromDatabaseTask;

public class GainerActivity extends MainActivity {

	public static void show(Context context) {
		PSEPreferences.setActivityMode(Util.GAINER);
		Intent i = new Intent(context, GainerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PSEPreferences.setListSortBy(StockDB.COLUMN_SYMBOL);
		PSEPreferences.setListSortMode("asc");
		
		if (PSEPreferences.getDBUpdateStatus()) {
			new LoadStockFromDatabaseTask(GainerActivity.this).execute();
		}
	}

}
