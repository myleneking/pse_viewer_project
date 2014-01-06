package com.p130001.pseviewer.data;

import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.db.StockDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PSEPreferences {
	
	private static final String PREFERENCES = "pse_preferences";
	private static final String DB_UPDATE_STATUS_KEY = "db_update_status";
	private static final String ACTIVITY_MODE_KEY = "activity_mode";
	private static final String LIST_SORT_BY_KEY = "list_sort_by";
	private static final String LIST_SORT_MODE_KEY = "list_sort_mode";
	
	private static Editor mEditor;
	private static SharedPreferences mPrefs;

	public static void setupPrefs(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, 0);
        mEditor = mPrefs.edit();
    }
	
    public static void setDBUpdateStatus(boolean status) {
        mEditor.putBoolean(DB_UPDATE_STATUS_KEY, status);
        mEditor.commit();
    }
    
    public static boolean getDBUpdateStatus() {
        return mPrefs.getBoolean(DB_UPDATE_STATUS_KEY, false);
    }
    
    public static void setActivityMode(String mode) {
    	mEditor.putString(ACTIVITY_MODE_KEY, mode);
    	mEditor.commit();
    }
    
    public static String getActivityMode() {
    	return mPrefs.getString(ACTIVITY_MODE_KEY, Util.ALL);
    }
    
    public static void setListSortBy(String by) {
    	mEditor.putString(LIST_SORT_BY_KEY, by);
    	mEditor.commit();
    }
    
    public static String getListSortBy() {
    	return mPrefs.getString(LIST_SORT_BY_KEY, StockDB.COLUMN_SYMBOL);
    }
    
    public static void setListSortMode(String mode) {
    	mEditor.putString(LIST_SORT_MODE_KEY, mode);
    	mEditor.commit();
    }
    
    public static String getListSortMode() {
    	return mPrefs.getString(LIST_SORT_MODE_KEY, "asc");
    }
    
}
