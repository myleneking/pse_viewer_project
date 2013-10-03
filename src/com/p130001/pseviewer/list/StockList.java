package com.p130001.pseviewer.list;

public class StockList {
	private String mName, mCode, mPercentChange, mPrice, mVolume, mStatus;
	private int mId;

	public StockList(String name, String code, String percentChange, String price, String volume) {
		this.mName = name;
		this.mCode = code;
		this.mPercentChange = percentChange;
		this.mPrice = price;
		this.mVolume = volume;
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

	public int getId() {
		return this.mId;
	}
	
	public String getStatus(){
		return this.mStatus;
	}
}
