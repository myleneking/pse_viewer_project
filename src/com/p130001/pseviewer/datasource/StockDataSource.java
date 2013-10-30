package com.p130001.pseviewer.datasource;

import java.util.ArrayList;
import com.p130001.pseviewer.MySQLiteHelper;
import com.p130001.pseviewer.model.Stock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class StockDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Context mContext;
	private String[] mColumns = {
			MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME,
			MySQLiteHelper.COLUMN_SYMBOL,
			MySQLiteHelper.COLUMN_PERCENT_CHANGE,
			MySQLiteHelper.COLUMN_VOLUME,
			MySQLiteHelper.COLUMN_CURRENCY,
			MySQLiteHelper.COLUMN_AMOUNT,
			MySQLiteHelper.COLUMN_AS_OF,
			MySQLiteHelper.COLUMN_WATCH_FLG
	};
	
	public StockDataSource (Context context) {
		dbHelper = new MySQLiteHelper(context);
		mContext = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void dropDatabase() {
		mContext.deleteDatabase(MySQLiteHelper.DATABASE_NAME);
	}
	
	public void updateStock(Stock stock, String code) {
		ContentValues values = new ContentValues();
		
		values.put(MySQLiteHelper.COLUMN_NAME, stock.getName());
		values.put(MySQLiteHelper.COLUMN_SYMBOL, stock.getCode());
		values.put(MySQLiteHelper.COLUMN_PERCENT_CHANGE, stock.getPercentChange());
		values.put(MySQLiteHelper.COLUMN_VOLUME, stock.getVolume());
		values.put(MySQLiteHelper.COLUMN_CURRENCY, stock.getCurrency());
		values.put(MySQLiteHelper.COLUMN_AMOUNT, stock.getAmount());
		values.put(MySQLiteHelper.COLUMN_AS_OF, stock.getDate());
		
		ArrayList<Stock> result = this.getByCode(code);
		if (result.size() != 0) {
			String whereClause = MySQLiteHelper.COLUMN_SYMBOL + "='" + code + "'";
			database.update(MySQLiteHelper.TABLE_STOCKS, values, whereClause, null);
		} else {
			database.insert(MySQLiteHelper.TABLE_STOCKS, null, values);
		}
	}
	
	private Stock cursorToStock(Cursor cursor) {
		Stock stock = new Stock();
		stock.setId(cursor.getLong(0));
		stock.setName(cursor.getString(1));
		stock.setCode(cursor.getString(2));
		stock.setPercentChange(cursor.getDouble(3));
		stock.setVolume(cursor.getInt(4));
		stock.setCurrency(cursor.getString(5));
		stock.setAmount(cursor.getDouble(6));
		stock.setDate(cursor.getString(7));
		stock.setWatchFlg(cursor.getString(8));
		
		return stock;
	}
	
	public ArrayList<Stock> getAll(String sortBy, String sortMode) {
		ArrayList<Stock> stocks = new ArrayList<Stock>();

		String orderBy = sortBy + " " + sortMode;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , null, null, null, null, orderBy);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Stock stock = cursorToStock(cursor);
			stocks.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return stocks;
	}

	public ArrayList<Stock> getGainers(String sortBy, String sortMode) {
		ArrayList<Stock> gainers = new ArrayList<Stock>();

		String selectQuery = MySQLiteHelper.COLUMN_PERCENT_CHANGE + "> 0";
		String orderBy = sortBy + " " + sortMode;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , selectQuery , null, null, null, orderBy);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Stock stock = cursorToStock(cursor);
			gainers.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return gainers;
	}
	
	public ArrayList<Stock> getLosers(String sortBy, String sortMode) {
		ArrayList<Stock> losers = new ArrayList<Stock>();

		String selectQuery = MySQLiteHelper.COLUMN_PERCENT_CHANGE + "< 0";
		String orderBy = sortBy + " " + sortMode;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , selectQuery , null, null, null, orderBy);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Stock stock = cursorToStock(cursor);
			losers.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return losers;
	}

	public ArrayList<Stock> getByCode(String code) {
		ArrayList<Stock> search = new ArrayList<Stock>();
		String selectQuery = MySQLiteHelper.COLUMN_SYMBOL + "= '" + code + "'";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , selectQuery , null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Stock stock = cursorToStock(cursor);
			search.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return search;
	}

	public void addToWatchList(String code) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_WATCH_FLG, "1");
		
		String whereClause = MySQLiteHelper.COLUMN_SYMBOL + "='" + code + "'"; 
		database.update(MySQLiteHelper.TABLE_STOCKS, values, whereClause, null);
	}

	public void removeToWatchList(String code) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_WATCH_FLG, "0");
		
		String whereClause = MySQLiteHelper.COLUMN_SYMBOL + "='" + code + "'"; 
		database.update(MySQLiteHelper.TABLE_STOCKS, values, whereClause, null);
	}

	public ArrayList<Stock> getWatchList(String sortBy, String sortMode) {
		ArrayList<Stock> watchList = new ArrayList<Stock>();

		String selectQuery = MySQLiteHelper.COLUMN_WATCH_FLG + "= 1";
		String orderBy = sortBy + " " + sortMode;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , selectQuery , null, null, null, orderBy);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Stock stock = cursorToStock(cursor);
			watchList.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return watchList;
	}

	public String getDate() {
		String selectQuery = MySQLiteHelper.COLUMN_ID + "= 1";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, new String[] { MySQLiteHelper.COLUMN_AS_OF } , selectQuery , null, null, null, null);
		cursor.moveToFirst();
		String date = cursor.getString(0);
		cursor.close();
		return date;
	}

}
