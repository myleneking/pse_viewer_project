package com.p130001.pseviewer;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.p130001.pseviewer.JSONParser;
import com.p130001.pseviewer.R;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.adapter.StockAdapter;
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

	private String mName, mCode, mPrice, mPercentChange, mVolume, mAsOf;
	private TextView mAsOfTextView;
	ArrayList<HashMap<String, String>> stockList = null;
	private ArrayList<StockList> mItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setupList();
	}

	private void setupList() {
		if (isNetworkConnected()) {
			new loadStockList().execute();
		} else {
			Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	private ArrayList<HashMap<String, String>> getStockList() {
		ArrayList<HashMap<String, String>> stockList = new ArrayList<HashMap<String, String>>();
		String jString = JSONParser.getJSONFromUrl(Util.API_PSE_ALL);
		mItems = new ArrayList<StockList>();

		try {
			JSONObject jObject = new JSONObject(jString);

			String asOf = jObject.getString("as_of");
			String date = asOf.substring(0, 10);
			String time = asOf.substring(11, 19);
			mAsOf = date + " " + time;

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

				mItems.add(new StockList(mName, mCode, mPercentChange, mPrice, mVolume));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockList;

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
			setupList();
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}
	}

	public class loadStockList extends AsyncTask<String, Integer, String>{

		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(MainActivity.this, "Fetching Data", "Please wait...", true, false);
		}
		
		@Override
		protected String doInBackground(String... params) {
			stockList = getStockList();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
		@Override
		protected void onPostExecute(String result) {
			mAsOfTextView = (TextView) findViewById(R.id.tvAsOf);
			mAsOfTextView.setText(mAsOf);

			final ListView listview = (ListView) findViewById(R.id.listView);
			listview.setAdapter(new StockAdapter(MainActivity.this, mItems));
			
			dialog.dismiss();
		}
	}
}
