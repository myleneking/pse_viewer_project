package com.p130001.pseviewer.activity;

import com.p130001.pseviewer.StockPreference;
import com.p130001.pseviewer.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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