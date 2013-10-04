package com.p130001.pseviewer.datasource;

import java.util.ArrayList;
import com.p130001.pseviewer.MySQLiteHelper;
import com.p130001.pseviewer.list.StockList;

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
			MySQLiteHelper.COLUMN_PRICE,
			MySQLiteHelper.COLUMN_AS_OF
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
	
	public StockList addStock(StockList stock) {
		ContentValues values = new ContentValues();
		
		values.put(MySQLiteHelper.COLUMN_NAME, stock.getName());
		values.put(MySQLiteHelper.COLUMN_SYMBOL, stock.getCode());
		values.put(MySQLiteHelper.COLUMN_PERCENT_CHANGE, stock.getPercentChange());
		values.put(MySQLiteHelper.COLUMN_PRICE, stock.getPrice());
		values.put(MySQLiteHelper.COLUMN_VOLUME, stock.getVolume());
		values.put(MySQLiteHelper.COLUMN_AS_OF, stock.getDate());
		
		long insertId = database.insert(MySQLiteHelper.TABLE_STOCKS, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		StockList newStock = cursorToStock(cursor);
		cursor.close();
		
		return newStock;
	}
	
	public ArrayList<StockList> getAll() {
		ArrayList<StockList> stocks = new ArrayList<StockList>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StockList stock = cursorToStock(cursor);
			stocks.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return stocks;
	}

	public ArrayList<StockList> getGainers() {
		ArrayList<StockList> gainers = new ArrayList<StockList>();

		String selectQuery = MySQLiteHelper.COLUMN_PERCENT_CHANGE + "> 0";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , selectQuery , null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StockList stock = cursorToStock(cursor);
			gainers.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return gainers;
	}
	
	public ArrayList<StockList> getLosers() {
		ArrayList<StockList> losers = new ArrayList<StockList>();

		String selectQuery = MySQLiteHelper.COLUMN_PERCENT_CHANGE + "< 0";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STOCKS, mColumns , selectQuery , null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StockList stock = cursorToStock(cursor);
			losers.add(stock);
			cursor.moveToNext();
		}
		cursor.close();
		
		return losers;
	}
	
	private StockList cursorToStock(Cursor cursor) {
		StockList stock = new StockList();
		stock.setId(cursor.getLong(0));
		stock.setName(cursor.getString(1));
		stock.setCode(cursor.getString(2));
		stock.setPercentChange(cursor.getString(3));
		stock.setPrice(cursor.getString(4));
		stock.setVolume(cursor.getString(5));
		stock.setDate(cursor.getString(6));
		
		return stock;
	}

}
