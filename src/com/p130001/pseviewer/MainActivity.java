package com.p130001.pseviewer;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.p130001.pseviewer.JSONParser;
import com.p130001.pseviewer.R;
import com.p130001.pseviewer.Util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String mName, mCode, mPrice, mPercentChange, mVolume, mAsOf;
	private ListAdapter mAdapter;
	private TextView mAsOfTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setupList();
		initialize();
	}

	private void setupList() {

		if (isNetworkConnected()) {
			ArrayList<HashMap<String, String>> stockList = null;
			stockList = getStockList();
			
			mAdapter = new SimpleAdapter(this, stockList, R.layout.row_item,
					new String[] { "name", "code", "percentChange", "price",
							"volume" }, new int[] { R.id.tvName, R.id.tvCode,
							R.id.tvPercentChange, R.id.tvPrice, R.id.tvVolume });
		} else {
			Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG)
					.show();
		}

	}

	private ArrayList<HashMap<String, String>> getStockList() {
		ArrayList<HashMap<String, String>> stockList = new ArrayList<HashMap<String, String>>();
		String jString = JSONParser.getJSONFromUrl(Util.API_PSE_TEL);

		try {
			JSONObject jObject = new JSONObject(jString);

			String asOf = jObject.getString("as_of");
			String date = asOf.substring(0, 10);
			String time = asOf.substring(11, 19);
			mAsOf = date + " " + time;

			JSONArray stockArr = jObject.getJSONArray("stock");

			for (int i = 0; i < stockArr.length(); i++) {
				JSONObject stock = stockArr.getJSONObject(i);
				mName = stock.getString("name");
				mCode = stock.getString("symbol");
				mPercentChange = stock.getString("percent_change");
				mVolume = stock.getString("volume");

				JSONObject price = stock.getJSONObject("price");
				String currency = price.getString("currency");
				String amount = price.getString("amount");
				mPrice = currency + " " + amount;

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", mName);
				map.put("code", mCode);
				map.put("percentChange", mPercentChange);
				map.put("price", mPrice);
				map.put("volume", mVolume);
				stockList.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockList;

	}

	private void initialize() {
		mAsOfTextView = (TextView) findViewById(R.id.tvAsOf);
		mAsOfTextView.setText(mAsOf);

		final ListView listview = (ListView) findViewById(R.id.listView);
		listview.setAdapter(mAdapter);
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
			Toast.makeText(getApplicationContext(), "Reloading",
					Toast.LENGTH_LONG).show();
			reloadStockList();
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}
	}

	private void reloadStockList() {
		setupList();
		initialize();
	}
}
