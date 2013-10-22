package com.p130001.pseviewer.activity;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import com.p130001.pseviewer.JSONParser;
import com.p130001.pseviewer.R;
import com.p130001.pseviewer.StockPreference;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String mJStringNew, mName, mCode, mPrice, mPercentChange, mVolume, mDate;
	private TextView mAsOfTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		StockPreference.setupPrefs(this);
			
		if (isNetworkConnected()) {
			new GetApiData().execute();
		} else {
			Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
			if (StockPreference.loadDatabaseUpdateStatus()) new LoadStockFromDatabase(Util.ALL).execute();
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
			if (isNetworkConnected()) {
				new GetApiData().execute();
			} else {
				Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
				new LoadStockFromDatabase(Util.ALL).execute();
			}
			return true;
		
		case R.id.action_gainers:
			new LoadStockFromDatabase(Util.GAINER).execute();
			return true;
		
		case R.id.action_losers:
			new LoadStockFromDatabase(Util.LOSER).execute();
			return true;
		
		case R.id.action_search:
			openSearchInputDialog();
			return true;
			
		case R.id.action_watchlist:
			new LoadStockFromDatabase(Util.WATCHLIST).execute();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	private void openSearchInputDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Search Code");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String code = input.getText().toString().toUpperCase();
				new LoadStockFromDatabase(Util.SEARCH, code).execute();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	public class GetApiData extends AsyncTask<String, Integer, String>{

		ProgressDialog mDialog;
		
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MainActivity.this, "Fetching Data", "Please wait...", true, false);
		}
		
		@Override
		protected String doInBackground(String... params) {
			StockDataSource datasource = new StockDataSource(MainActivity.this);
			mJStringNew = JSONParser.getJSONFromUrl(Util.API_PSE_ALL);

			try {
				JSONObject jObject = new JSONObject(mJStringNew);

				String asOf = jObject.getString("as_of");
				String date = asOf.substring(0, 10);
				String time = asOf.substring(11, 19);
				mDate = date + " " + time;

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
					
					//Save to database
					if (mName != null && mCode != null && mPercentChange != null && mPrice != null && mVolume != null && mDate != null) {
						datasource.open();
						datasource.updateStock(stockRow, mCode);
						datasource.close();
					}
				}
				if (!StockPreference.loadDatabaseUpdateStatus()) StockPreference.saveDatabaseUpdateStatus(true);;
				
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
			mDialog.dismiss();
			new LoadStockFromDatabase(Util.ALL).execute();
		}
	}
	
	public class LoadStockFromDatabase extends AsyncTask<String, Integer, ArrayList<StockList>> {

		ProgressDialog mDialog;
		String mMode;
		String mInputCode;
		
		public LoadStockFromDatabase(String mode) {
			this.mMode = mode;
		}
		
		public LoadStockFromDatabase(String mode, String code) {
			this.mMode = mode;
			this.mInputCode = code;
		}
		
		@Override
		protected void onPreExecute() {
			if (mMode.equals(Util.SEARCH)) mDialog = ProgressDialog.show(MainActivity.this, "Searching Code", "Please wait...", true, false);
		};
		
		@Override
		protected ArrayList<StockList> doInBackground(String... params) {
			
			StockDataSource datasource = new StockDataSource(MainActivity.this);
			ArrayList<StockList> result = null;

			datasource.open();
				if (mMode.equals(Util.GAINER)) {
					result = datasource.getGainers();
				} else if (mMode.equals(Util.LOSER)) {
					result = datasource.getLosers();
				} else if (mMode.equals(Util.SEARCH)){
					result = datasource.getByCode(mInputCode);
				} else if (mMode.equals(Util.WATCHLIST)) {
					result = datasource.getWatchList();
				} else {
					result = datasource.getAll();
				} 
				mDate = datasource.getDate();
			datasource.close();
			
			return result;
		}
		
		@Override
		protected void onPostExecute(ArrayList<StockList> result) {
			if (result.size() == 0 && mMode.equals(Util.SEARCH)) Toast.makeText(MainActivity.this, "Code not found!", Toast.LENGTH_SHORT).show();
			
			mAsOfTextView = (TextView) findViewById(R.id.tvAsOf);
			mAsOfTextView.setText(mDate);

			final ListView listview = (ListView) findViewById(R.id.listView);
			listview.setAdapter(new StockAdapter(MainActivity.this, result));
			
			if (mMode.equals(Util.SEARCH)) mDialog.dismiss();
		}
		
	}
	
	}
