package com.p130001.pseviewer.adapter;

import java.util.ArrayList;

import com.p130001.pseviewer.R;
import com.p130001.pseviewer.StockPreference;
import com.p130001.pseviewer.Util;
import com.p130001.pseviewer.activity.WatchListActivity;
import com.p130001.pseviewer.datasource.StockDataSource;
import com.p130001.pseviewer.model.Stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class StockAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<Stock> mItems;
	private OnStockItemClickListener mListener;
	
	// Interface
	public interface OnStockItemClickListener {
		public void onStockItemClick(Stock item);
	}
	
	// Constructor
	public StockAdapter(Context c, ArrayList<Stock> items) {
		mContext = c;
		this.mItems = items;
	}

	public int getCount() {
		return mItems.size();
	}

	public Stock getItem(int position) {
		return mItems.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void setOnStockItemClickListener(OnStockItemClickListener listener) {
		this.mListener = listener;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = inflater.inflate(R.layout.row_item, parent, false);
		}
		
		TextView tvCode = (TextView) rowView.findViewById(R.id.tvCode);
		TextView tvName = (TextView) rowView.findViewById(R.id.tvName);
		TextView tvPercentChange = (TextView) rowView.findViewById(R.id.tvPercentChange);
		TextView tvVolume = (TextView) rowView.findViewById(R.id.tvVolume);
		TextView tvCurrency = (TextView) rowView.findViewById(R.id.tvCurrency);
		TextView tvAmount = (TextView) rowView.findViewById(R.id.tvAmount);
		ImageView ivArrow = (ImageView) rowView.findViewById(R.id.ivArrow);
		ToggleButton tbWatch = (ToggleButton) rowView.findViewById(R.id.tbWatchList);

		final Stock item = this.mItems.get(position);
		double percentChange = item.getPercentChange();
		int color, arrow;
		
		if (percentChange < 0) {
			color = R.color.red;
			arrow = R.drawable.img_arrow_down_colored;
		} else if (percentChange > 0) {
			color = R.color.green;
			arrow = R.drawable.img_arrow_up_colored;
		} else {
			color = R.color.black;
			arrow = R.drawable.img_arrow;
		}
		
		if (position == 0) {
			rowView.findViewById(R.id.margin_view).setVisibility(View.VISIBLE);
		} else {
			rowView.findViewById(R.id.margin_view).setVisibility(View.GONE);
		}
		
		tvCode.setText(item.getCode());
		tvCode.setTextColor(mContext.getResources().getColor(color));
		
		tvName.setText(item.getName());
		tvName.setTextColor(mContext.getResources().getColor(color));
		
		tvPercentChange.setText(item.getPercentChange() + "%");
		tvPercentChange.setTextColor(mContext.getResources().getColor(color));
		
		tvVolume.setText(item.getVolume() + "");
		tvVolume.setTextColor(mContext.getResources().getColor(color));

		tvCurrency.setText(item.getCurrency());
		tvCurrency.setTextColor(mContext.getResources().getColor(color));
		
		tvAmount.setText(String.format("%.2f", item.getAmount()));
		tvAmount.setTextColor(mContext.getResources().getColor(color));
		
		ivArrow.setImageResource(arrow);
		
		tbWatch.setChecked(item.getWatchFlg());
		tbWatch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				StockDataSource datasource = new StockDataSource(mContext);
				datasource.open();
				
				if(isChecked) {
					datasource.addToWatchList(item.getCode());
					//Toast.makeText(mContext, "added: " + ret, Toast.LENGTH_SHORT).show();
		        }
		        else {
		        	//Toast.makeText(mContext, "removed: " + item.getCode(), Toast.LENGTH_SHORT).show();
		        	datasource.removeToWatchList(item.getCode());
		        }
				
				datasource.close();
				
				String mode = StockPreference.loadActivityMode();
				if (mode.equals(Util.WATCHLIST)) {
					WatchListActivity.show(mContext);
				}
			}
		});
		
		rowView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if (mListener != null) {
					mListener.onStockItemClick(item);
				}
				return false;
			}
		});
		
		return rowView;
	}
	
}
