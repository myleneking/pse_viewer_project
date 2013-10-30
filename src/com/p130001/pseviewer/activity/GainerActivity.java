package com.p130001.pseviewer.activity;

import com.p130001.pseviewer.StockPreference;
import com.p130001.pseviewer.Util;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GainerActivity extends MainActivity {

	public static void show(Context context) {
		StockPreference.saveActivityMode(Util.GAINER);
		Intent i = new Intent(context, GainerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (StockPreference.loadDatabaseUpdateStatus()) {
			new LoadStockFromDatabase().execute();
		}
	}

}
