package com.p130001.pseviewer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.p130001.pseviewer.R;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.data.PSEPreferences;
import com.p130001.pseviewer.db.StockDB;
import com.p130001.pseviewer.task.LoadStockFromDatabaseTask;

public class MainActivity extends Activity implements OnClickListener, OnLongClickListener {

	protected String mJStringNew, mName, mCode, mCurrency, mDate;
	protected Integer mVolume;
	protected Double mPercentChange, mAmount;
	private static String ASC_ORDER = "asc";
	private static String DESC_ORDER = "desc";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		PSEPreferences.setupPrefs(this);
			
		initialize();
	}

	protected boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		return activeNetworkInfo != null;
	}
	
	protected void initialize() {
		ImageView ivSearch = (ImageView) findViewById(R.id.ivSearch);
		ImageView ivSort = (ImageView) findViewById(R.id.ivSort);
		ImageView ivReload = (ImageView) findViewById(R.id.ivReload);
		ImageView ivGainers = (ImageView) findViewById(R.id.ivGainers);
		ImageView ivLosers = (ImageView) findViewById(R.id.ivLosers);
		ImageView ivWatchlist = (ImageView) findViewById(R.id.ivWatchList);
		
		ivSearch.setOnClickListener(this);
		ivSort.setOnClickListener(this);
		ivReload.setOnClickListener(this);
		ivGainers.setOnClickListener(this);
		ivLosers.setOnClickListener(this);
		ivWatchlist.setOnClickListener(this);
		
		ivSort.setOnLongClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivSearch:
			showSearchInputDialog();
			break;
		case R.id.ivSort:
			String sortMode = PSEPreferences.getListSortMode();
			if (sortMode.equals(ASC_ORDER)) {
				PSEPreferences.setListSortMode(DESC_ORDER);
			} else {
				PSEPreferences.setListSortMode(ASC_ORDER);
			}
			new LoadStockFromDatabaseTask(MainActivity.this).execute();
			break;
		case R.id.ivReload:
			AllActivity.show(this);
			break;
		case R.id.ivGainers:
			GainerActivity.show(this);
			break;
		case R.id.ivLosers:
			LoserActivity.show(this);
			break;
		case R.id.ivWatchList:
			WatchListActivity.show(this);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.ivSort:
			showSortOptions();
			break;
		default:
			break;
		}
		return false;
	}

	private void showSortOptions() {
		String options[] = { "Code", "Amount", "Volume", "Percent Change" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		final AlertDialog dialog = alert.create();
		alert.setMessage("Sort by...");
		
		ListView lv = new ListView(this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String sortBy = null;
				switch (position) {
				case 0: // Code
					sortBy = StockDB.COLUMN_SYMBOL;
					break;
				case 1: // Amount
					sortBy = StockDB.COLUMN_AMOUNT;
					break;
				case 2: // Volume
					sortBy = StockDB.COLUMN_VOLUME;
					break;
				case 3: // Percent Change
					sortBy = StockDB.COLUMN_PERCENT_CHANGE;
					break;
				default:
					break;
				}
				PSEPreferences.setListSortBy(sortBy);
				new LoadStockFromDatabaseTask(MainActivity.this).execute();
				dialog.cancel();
			}
		});
		
		dialog.setView(lv);
		dialog.show();
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
		case R.id.action_settings:
			Toast.makeText(this, "Not yet available.", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_exit:
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(0, 0);
	}
	
	protected void showSearchInputDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Search Code");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String code = input.getText().toString().toUpperCase();
				PSEPreferences.setActivityMode(Util.SEARCH);
				if (PSEPreferences.getDBUpdateStatus()) {
					new LoadStockFromDatabaseTask(MainActivity.this, code).execute();
				}
			}
		});

		alert.setNegativeButton("Cancel", null);
		alert.show();
	}

}
