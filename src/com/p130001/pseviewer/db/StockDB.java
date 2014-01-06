package com.p130001.pseviewer.db;

import java.util.ArrayList;

import com.p130001.pseviewer.model.Stock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class StockDB {

protected static final String TABLE_NAME = "stocks";
	
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_SYMBOL = "symbol";
	private static final String COLUMN_PERCENT_CHANGE = "percent_change";
	private static final String COLUMN_VOLUME = "volume";
	private static final String COLUMN_CURRENCY = "currency";
	private static final String COLUMN_AMOUNT = "amount";
	private static final String COLUMN_AS_OF = "as_of";
	private static final String COLUMN_WATCH_FLG = "watch_flg";

	public static final String CREATE_TABLE = "" 
			+ "create table " + TABLE_NAME 
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_SYMBOL + " text not null, "
			+ COLUMN_PERCENT_CHANGE + " double not null, "
			+ COLUMN_VOLUME + " integer not null, "
			+ COLUMN_CURRENCY + " text not null, "
			+ COLUMN_AMOUNT + " double not null, "
			+ COLUMN_AS_OF + " text not null,"
			+ COLUMN_WATCH_FLG + " text not null default 0"
			+ ");";
	
	private SQLiteDatabase database;
	private PSEDatabase dbHelper;
	private Context mContext;
	private String[] mColumns = {
			COLUMN_ID,
			COLUMN_NAME,
			COLUMN_SYMBOL,
			COLUMN_PERCENT_CHANGE,
			COLUMN_VOLUME,
			COLUMN_CURRENCY,
			COLUMN_AMOUNT,
			COLUMN_AS_OF,
			COLUMN_WATCH_FLG
	};
	
	public StockDB (Context context) {
		dbHelper = new PSEDatabase(context);
		mContext = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void dropDatabase() {
		mContext.deleteDatabase(PSEDatabase.DATABASE_NAME);
	}
	
	public void updateStock(Stock stock, String code) {
		ContentValues values = new ContentValues();
		
		values.put(COLUMN_NAME, stock.getName());
		values.put(COLUMN_SYMBOL, stock.getCode());
		values.put(COLUMN_PERCENT_CHANGE, stock.getPercentChange());
		values.put(COLUMN_VOLUME, stock.getVolume());
		values.put(COLUMN_CURRENCY, stock.getCurrency());
		values.put(COLUMN_AMOUNT, stock.getAmount());
		values.put(COLUMN_AS_OF, stock.getDate());
		
		ArrayList<Stock> result = this.getByCode(code);
		if (result.size() != 0) {
			String whereClause = COLUMN_SYMBOL + "='" + code + "'";
			database.update(TABLE_NAME, values, whereClause, null);
		} else {
			database.insert(TABLE_NAME, null, values);
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
		Cursor cursor = database.query(TABLE_NAME, mColumns , null, null, null, null, orderBy);
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

		String selectQuery = COLUMN_PERCENT_CHANGE + "> 0";
		String orderBy = sortBy + " " + sortMode;
		Cursor cursor = database.query(TABLE_NAME, mColumns , selectQuery , null, null, null, orderBy);
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

		String selectQuery = COLUMN_PERCENT_CHANGE + "< 0";
		String orderBy = sortBy + " " + sortMode;
		Cursor cursor = database.query(TABLE_NAME, mColumns , selectQuery , null, null, null, orderBy);
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
		String selectQuery = COLUMN_SYMBOL + "= '" + code + "'";
		Cursor cursor = database.query(TABLE_NAME, mColumns , selectQuery , null, null, null, null);
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
		values.put(COLUMN_WATCH_FLG, "1");
		
		String whereClause = COLUMN_SYMBOL + "='" + code + "'"; 
		database.update(TABLE_NAME, values, whereClause, null);
	}

	public void removeToWatchList(String code) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_WATCH_FLG, "0");
		
		String whereClause = COLUMN_SYMBOL + "='" + code + "'"; 
		database.update(TABLE_NAME, values, whereClause, null);
	}

	public ArrayList<Stock> getWatchList(String sortBy, String sortMode) {
		ArrayList<Stock> watchList = new ArrayList<Stock>();

		String selectQuery = COLUMN_WATCH_FLG + "= 1";
		String orderBy = sortBy + " " + sortMode;
		Cursor cursor = database.query(TABLE_NAME, mColumns , selectQuery , null, null, null, orderBy);
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
		String selectQuery = COLUMN_ID + "= 1";
		Cursor cursor = database.query(TABLE_NAME, new String[] { COLUMN_AS_OF } , selectQuery , null, null, null, null);
		cursor.moveToFirst();
		String date = cursor.getString(0);
		cursor.close();
		return date;
	}

}
