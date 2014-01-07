package com.p130001.pseviewer.task;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.p130001.pseviewer.R;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.activity.GraphActivity;
import com.p130001.pseviewer.adapter.StockAdapter;
import com.p130001.pseviewer.adapter.StockAdapter.OnStockItemClickListener;
import com.p130001.pseviewer.data.PSEPreferences;
import com.p130001.pseviewer.db.StockDB;
import com.p130001.pseviewer.model.Stock;

public class LoadStockFromDatabaseTask extends AsyncTask<String, Integer, ArrayList<Stock>> {

	ProgressDialog mDialog;
	String mMode;
	String mSortBy, mSortMode;
	String mInputCode;
	Context mContext;
	String mDate;
	
	public LoadStockFromDatabaseTask(Context context) {
		this.mContext = context;
	}
	
	public LoadStockFromDatabaseTask(Context context, String code) {
		this.mContext = context;
		this.mInputCode = code;
	}
	
	@Override
	protected void onPreExecute() {
		this.mMode = PSEPreferences.getActivityMode();
		this.mSortBy = PSEPreferences.getListSortBy();
		this.mSortMode = PSEPreferences.getListSortMode();
		
		if (mMode.equals(Util.SEARCH)) {
			mDialog = ProgressDialog.show(mContext, "", "Searching...");
		}
	};
	
	@Override
	protected ArrayList<Stock> doInBackground(String... params) {
		StockDB datasource = new StockDB(mContext);
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
//			String tempDate = datasource.getDate();
			mDate = datasource.getDate();
		datasource.close();
		
		return result;
	}
	
	@Override
	protected void onPostExecute(ArrayList<Stock> result) {
		if (result.size() == 0 && mMode.equals(Util.SEARCH)) {
			Toast.makeText(mContext, "Code not found!", Toast.LENGTH_SHORT).show();
		}
		
		TextView mAsOfTextView = (TextView) ((Activity) mContext).findViewById(R.id.tvAsOf);
		mAsOfTextView.setText(mDate);
		
		final ListView listview = (ListView) ((Activity) mContext).findViewById(R.id.listView);
		StockAdapter adapter = new StockAdapter(mContext, result);
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
				RelativeLayout header = (RelativeLayout) ((Activity) mContext).findViewById(R.id.rlHeader);
				LinearLayout footer = (LinearLayout) ((Activity) mContext).findViewById(R.id.llFooter);
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					header.setVisibility(View.VISIBLE);
					header.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_up_in));
					footer.setVisibility(View.VISIBLE);
					footer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_down_in));
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					header.setVisibility(View.INVISIBLE);
					header.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_down_out));
					footer.setVisibility(View.INVISIBLE);
					footer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_up_out));
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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, options);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		final AlertDialog dialog = alert.create();
		
		ListView lv = new ListView(mContext);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0: // Open Trends
					GraphActivity.show(mContext, item.getCode());
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
