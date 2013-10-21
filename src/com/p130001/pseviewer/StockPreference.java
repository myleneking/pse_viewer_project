package com.p130001.pseviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StockPreference {
	
	private static Editor mEditor;
	private static SharedPreferences mPrefs;

	public static void setupPrefs(Context context) {
        mPrefs = context.getSharedPreferences("stock", 0);
        mEditor = mPrefs.edit();
    }
    public static void saveDatabaseUpdateStatus(boolean status) {
        mEditor.putBoolean("databaseUpdate", status);
        mEditor.commit();
    }
    public static boolean loadDatabaseUpdateStatus() {
        return mPrefs.getBoolean("databaseUpdate", false);
    }
}
