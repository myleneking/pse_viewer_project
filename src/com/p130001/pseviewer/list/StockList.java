package com.p130001.pseviewer.list;

public class StockList {
	private String mName, mCode, mPercentChange, mPrice, mVolume, mDate,
			mWatchFlg;
	private long mId;

	public StockList(String name, String code, String percentChange,
			String price, String volume, String date) {
		this.mName = name;
		this.mCode = code;
		this.mPercentChange = percentChange;
		this.mPrice = price;
		this.mVolume = volume;
		this.mDate = date;
	}

	public StockList() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return this.mId;
	}

	public String getName() {
		return this.mName;
	}

	public String getCode() {
		return this.mCode;
	}

	public String getPercentChange() {
		return this.mPercentChange;
	}

	public String getPrice() {
		return this.mPrice;
	}

	public String getVolume() {
		return this.mVolume;
	}

	public String getDate() {
		return this.mDate;
	}

	public boolean getWatchFlg() {
		if (this.mWatchFlg.equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	public void setName(String string) {
		this.mName = string;
	}

	public void setCode(String string) {
		this.mCode = string;
	}

	public void setPercentChange(String string) {
		this.mPercentChange = string;
	}

	public void setPrice(String string) {
		this.mPrice = string;
	}

	public void setVolume(String string) {
		this.mVolume = string;
	}

	public void setDate(String string) {
		this.mDate = string;
	}

	public void setId(long id) {
		this.mId = id;
	}

	public void setWatchFlg(String string) {
		this.mWatchFlg = string;
	}

}
