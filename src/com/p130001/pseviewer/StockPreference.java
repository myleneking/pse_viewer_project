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
    
    public static void saveActivityMode(String mode) {
    	mEditor.putString("activityMode", mode);
    	mEditor.commit();
    }
    
    public static String loadActivityMode() {
    	return mPrefs.getString("activityMode", Util.ALL);
    }
    
    public static void setSortBy(String by) {
    	mEditor.putString("sortBy", by);
    	mEditor.commit();
    }
    
    public static String getSortBy() {
    	return mPrefs.getString("sortBy", StockTag.SYMBOL);
    }
    
    public static void setSortMode(String mode) {
    	mEditor.putString("sortMode", mode);
    	mEditor.commit();
    }
    
    public static String getSortMode() {
    	return mPrefs.getString("sortMode", "asc");
    }
    
}
