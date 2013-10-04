package com.p130001.pseviewer.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.p130001.pseviewer.JSONParser;
import com.p130001.pseviewer.R;
import com.p130001.pseviewer.Tag;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.adapter.StockAdapter;
import com.p130001.pseviewer.datasource.StockDataSource;
import com.p130001.pseviewer.list.StockList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String mJStringNew, mName, mCode, mPrice, mPercentChange, mVolume, mDate;
	private TextView mAsOfTextView;
	ArrayList<HashMap<String, String>> stockList = null;
	private ArrayList<StockList> mItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setupList(Util.RELOAD);
	}

	private void setupList(String mode) {
		if (isNetworkConnected()) {
			if (mode.equals(Util.RELOAD)) {
				new LoadStockList().execute();
			} else if (mode.equals(Util.GAINER)) {
				new LoadStockFromDatabase(Util.GAINER).execute();
			} else if (mode.equals(Util.LOSER)){
				new LoadStockFromDatabase(Util.LOSER).execute();
			}
		} else {
			if (mode.equals(Util.RELOAD)) {
				new LoadStockFromDatabase(Util.ALL).execute();
			} else if (mode.equals(Util.GAINER)) {
				new LoadStockFromDatabase(Util.GAINER).execute();
			} else if (mode.equals(Util.LOSER)){
				new LoadStockFromDatabase(Util.LOSER).execute();
			}
			Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		return activeNetworkInfo != null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_reload:
			setupList(Util.RELOAD);
			return true;
		
		case R.id.action_gainers:
			setupList(Util.GAINER);
			return true;
		
		case R.id.action_losers:
			setupList(Util.LOSER);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	public class LoadStockList extends AsyncTask<String, Integer, String>{

		ProgressDialog mDialog;
		
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MainActivity.this, "Fetching Data", "Please wait...", true, false);
		}
		
		@Override
		protected String doInBackground(String... params) {
			StockDataSource datasource = new StockDataSource(MainActivity.this);
			mJStringNew = JSONParser.getJSONFromUrl(Util.API_PSE_ALL);
			mItems = new ArrayList<StockList>();

			try {
				JSONObject jObject = new JSONObject(mJStringNew);

				String asOf = jObject.getString("as_of");
				String date = asOf.substring(0, 10);
				String time = asOf.substring(11, 19);
				mDate = date + " " + time;

				datasource.dropDatabase();
				
				JSONArray stockArr = jObject.getJSONArray("stock");

				for (int i = 0; i < stockArr.length(); i++) {
					JSONObject stock = stockArr.getJSONObject(i);
					mName = stock.getString(Tag.NAME);
					mCode = stock.getString(Tag.SYMBOL);
					mPercentChange = stock.getString(Tag.PERCENT_CHANGE);
					mVolume = stock.getString(Tag.VOLUME);

					JSONObject price = stock.getJSONObject("price");
					String currency = price.getString("currency");
					String amount = price.getString("amount");
					mPrice = currency + " " + amount;

					StockList stockRow = new StockList(mName, mCode, mPercentChange, mPrice, mVolume, mDate);
					mItems.add(stockRow);
					
					if (mName != null && mCode != null && mPercentChange != null && mPrice != null && mVolume != null && mDate != null) {
						datasource.open();
						datasource.addStock(stockRow);
						datasource.close();
					}
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
		@Override
		protected void onPostExecute(String result) {
			mAsOfTextView = (TextView) findViewById(R.id.tvAsOf);
			mAsOfTextView.setText(mDate);

			final ListView listview = (ListView) findViewById(R.id.listView);
			listview.setAdapter(new StockAdapter(MainActivity.this, mItems));
			
			mDialog.dismiss();
		}
	}
	
	public class LoadStockFromDatabase extends AsyncTask<String, Integer, String> {

		ProgressDialog mDialog;
		String mMode;
		
		public LoadStockFromDatabase(String mode) {
			this.mMode = mode;
		}
		
		public LoadStockFromDatabase() {
		}
		
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MainActivity.this, "Loading Database Data", "Please wait...", true, false);
		};
		
		@Override
		protected String doInBackground(String... params) {
			
			StockDataSource datasource = new StockDataSource(MainActivity.this);
			datasource.open();
			
			if (mMode.equals(Util.GAINER)) {
				mItems = datasource.getGainers();
			} else if (mMode.equals(Util.LOSER)) {
				mItems = datasource.getLosers();
			} else {
				mItems = datasource.getAll();
			} 
			
			if (mItems != null) {
				mDate = mItems.get(0).getDate();
			}
			datasource.close();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mAsOfTextView = (TextView) findViewById(R.id.tvAsOf);
			mAsOfTextView.setText(mDate);

			final ListView listview = (ListView) findViewById(R.id.listView);
			listview.setAdapter(new StockAdapter(MainActivity.this, mItems));
			
			mDialog.dismiss();
		}
		
	}
	
	}
