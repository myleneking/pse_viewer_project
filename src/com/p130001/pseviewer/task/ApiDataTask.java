package com.p130001.pseviewer.task;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.p130001.pseviewer.JSONParser;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.data.PSEPreferences;
import com.p130001.pseviewer.db.StockDB;
import com.p130001.pseviewer.model.Stock;

public class ApiDataTask extends AsyncTask<String, Integer, String> {

	ProgressDialog mDialog;
	Context mContext;
	String mMode;

	public ApiDataTask(Context context) {
		this.mContext = context;
		this.mMode = PSEPreferences.getActivityMode();
	}

	@Override
	protected void onPreExecute() {
		mDialog = ProgressDialog.show(mContext, "", "Loading...");
	}

	@Override
	protected String doInBackground(String... params) {
		JSONArray stockArr = null;
		String date = null;
		StockDB datasource = new StockDB(mContext);
		String json = JSONParser.getJSONFromUrl(Util.API_PSE_ALL);

		try {
			JSONObject jObject = new JSONObject(json);

			String asOf = jObject.getString("as_of");
			String tempDate = asOf.substring(0, 10);
			String tempTime = asOf.substring(11, 19);
			date = tempDate + " " + tempTime;

			stockArr = jObject.getJSONArray("stock");

			if (!PSEPreferences.getDBUpdateStatus()) {
				PSEPreferences.setDBUpdateStatus(true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < stockArr.length(); i++) {
			try {
				JSONObject stock = stockArr.getJSONObject(i);
				
				String name = stock.getString("name");
				String code = stock.getString("symbol");
				double percentChange = stock.getDouble("percent_change");
				int volume = stock.getInt("volume");

				JSONObject price = stock.getJSONObject("price");
				String currency = price.getString("currency");
				double amount = price.getDouble("amount");
				
				Stock stockRow = new Stock(name, code, percentChange,
						volume, currency, amount, date);

				// Save to database
				datasource.open();
				datasource.updateStock(stockRow, code);
				datasource.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(String result) {
		mDialog.dismiss();
		new LoadStockFromDatabaseTask(mContext).execute();
	}
}
