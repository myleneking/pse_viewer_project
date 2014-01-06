package com.p130001.pseviewer.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.p130001.pseviewer.JSONParser;
import com.p130001.pseviewer.R;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.adapter.StockAdapter;
import com.p130001.pseviewer.adapter.StockAdapter.OnStockItemClickListener;
import com.p130001.pseviewer.data.PSEPreferences;
import com.p130001.pseviewer.db.StockDB;
import com.p130001.pseviewer.model.Stock;

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
			new LoadStockFromDatabase().execute();
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
				new LoadStockFromDatabase().execute();
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
					new LoadStockFromDatabase(code).execute();
				}
			}
		});

		alert.setNegativeButton("Cancel", null);
		alert.show();
	}

	protected void showAsOfDate() {
		final LinearLayout asOfLayout = (LinearLayout) findViewById(R.id.llAsOf);
		asOfLayout.setVisibility(View.VISIBLE);
		asOfLayout.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_left_in));
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				asOfLayout.setVisibility(View.INVISIBLE);
				asOfLayout.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_right_out));
			}
		}, 3000);
	}
	
	public class GetApiData extends AsyncTask<String, Integer, String> {

		ProgressDialog mDialog;
		String mMode;
		
		public GetApiData() {
			this.mMode = PSEPreferences.getActivityMode();;
		}
		
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MainActivity.this, "", "Loading...");
		}
		
		@Override
		protected String doInBackground(String... params) {
			StockDB datasource = new StockDB(MainActivity.this);
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
					mName = stock.getString("name");
					mCode = stock.getString("code");
					mPercentChange = stock.getDouble("percent_change");
					mVolume = stock.getInt("volume");

					JSONObject price = stock.getJSONObject("price");
					mCurrency = price.getString("currency");
					mAmount = price.getDouble("amount");

					Stock stockRow = new Stock(mName, mCode, mPercentChange, mVolume, mCurrency, mAmount, mDate);
					
					//Save to database
					if (mName != null && mCode != null && mPercentChange != Double.NaN && mVolume != null && mCurrency != null && mAmount != Double.NaN && mDate != null) {
						datasource.open();
						datasource.updateStock(stockRow, mCode);
						datasource.close();
					}
				}
				if (!PSEPreferences.getDBUpdateStatus()) {
					PSEPreferences.setDBUpdateStatus(true);;
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
			mDialog.dismiss();
			new LoadStockFromDatabase().execute();
		}
	}
	
	public class LoadStockFromDatabase extends AsyncTask<String, Integer, ArrayList<Stock>> {

		ProgressDialog mDialog;
		String mMode;
		String mSortBy, mSortMode;
		String mInputCode;
		
		public LoadStockFromDatabase() {}
		
		public LoadStockFromDatabase(String code) {
			this.mInputCode = code;
		}
		
		@Override
		protected void onPreExecute() {
			this.mMode = PSEPreferences.getActivityMode();
			this.mSortBy = PSEPreferences.getListSortBy();
			this.mSortMode = PSEPreferences.getListSortMode();
			
			if (mMode.equals(Util.SEARCH)) {
				mDialog = ProgressDialog.show(MainActivity.this, "", "Searching...");
			}
		};
		
		@Override
		protected ArrayList<Stock> doInBackground(String... params) {
			StockDB datasource = new StockDB(MainActivity.this);
			ArrayList<Stock> result = null;

			datasource.open();
				if (mMode.equals(Util.GAINER)) {
					result = datasource.getGainers(mSortBy, mSortMode);
				} else if (mMode.equals(Util.LOSER)) {
					result = datasource.getLosers(mSortBy, mSortMode);
				} else if (mMode.equals(Util.SEARCH)){
					result = datasource.getByCode(mInputCode);
				} else if (mMode.equals(Util.WATCHLIST)) {
					result = datasource.getWatchList(mSortBy, mSortMode);
				} else {
					result = datasource.getAll(mSortBy, mSortMode);
				} 
				mDate = datasource.getDate();
			datasource.close();
			
			return result;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Stock> result) {
			if (result.size() == 0 && mMode.equals(Util.SEARCH)) {
				Toast.makeText(MainActivity.this, "Code not found!", Toast.LENGTH_SHORT).show();
			}
			
			TextView mAsOfTextView = (TextView) findViewById(R.id.tvAsOf);
			mAsOfTextView.setText(mDate);
			
			final ListView listview = (ListView) findViewById(R.id.listView);
			StockAdapter adapter = new StockAdapter(MainActivity.this, result);
			adapter.setOnStockItemClickListener(new OnStockItemClickListener() {

				@Override
				public void onStockItemClick(Stock item) {
					showOptionDialog(item);
					//Toast.makeText(MainActivity.this, "item selected " + item.getName(), Toast.LENGTH_SHORT).show();
				}

			});

			listview.setAdapter(adapter);
			listview.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					RelativeLayout header = (RelativeLayout) findViewById(R.id.rlHeader);
					LinearLayout footer = (LinearLayout) findViewById(R.id.llFooter);
					switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE:
						header.setVisibility(View.VISIBLE);
						header.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up_in));
						footer.setVisibility(View.VISIBLE);
						footer.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down_in));
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
						header.setVisibility(View.INVISIBLE);
						header.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down_out));
						footer.setVisibility(View.INVISIBLE);
						footer.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up_out));
						break;
					default:
						break;
					}
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {}
			});
			
			if (mMode.equals(Util.SEARCH)) {
				mDialog.dismiss();
			}
		}

		private void showOptionDialog(final Stock item) {
			String options[] = { "Open Trends", "Remind Me" };
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, options);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
			final AlertDialog dialog = alert.create();
			
			ListView lv = new ListView(MainActivity.this);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					switch (position) {
					case 0: // Open Trends
						GraphActivity.show(MainActivity.this, item.getCode());
						break;
					case 1: // Remind Me
						break;
					default:
						break;
					}
					dialog.cancel();
				}
			});
			
			dialog.setView(lv);
			dialog.show();
		}
		
	}

	}
