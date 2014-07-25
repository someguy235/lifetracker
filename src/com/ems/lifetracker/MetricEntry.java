package com.ems.lifetracker;

public class MetricEntry {
	private String name;
	private String date;
	private int count;
	private String details;

	public MetricEntry(String name, String date, int count, String details) {
		this.name = name;
		this.date = date;
		this.count = count;
		this.details = details;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
}
