package com.p130001.pseviewer.model;

public class Stock {
	private String mName, mCode, mCurrency, mDate, mWatchFlg;
	private Integer mVolume;
	private double mPercentChange, mAmount;
	private long mId;

	public Stock(String name, String code, double percentChange,
			int volume, String currency, double amount, String date) {
		this.mName = name;
		this.mCode = code;
		this.mPercentChange = percentChange;
		this.mVolume = volume;
		this.mCurrency = currency;
		this.mAmount = amount;
		this.mDate = date;
	}

	public Stock() {
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

	public double getPercentChange() {
		return this.mPercentChange;
	}

	public int getVolume() {
		return this.mVolume;
	}

	public String getCurrency() {
		return this.mCurrency;
	}

	public double getAmount() {
		return this.mAmount;
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

	public void setPercentChange(double d) {
		this.mPercentChange = d;
	}

	public void setVolume(int string) {
		this.mVolume = string;
	}

	public void setCurrency(String string) {
		this.mCurrency = string;
	}

	public void setAmount(double d) {
		this.mAmount = d;
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
