package com.ems.lifetracker.domain;

public class MetricEntry {
	private String name;
	private String date;
	private String unit;
	private String type;
	private double count;
	private String details;

	public MetricEntry(String name, String date, String unit, String type, double count, String details) {
		this.name = name;
		this.date = date;
		this.unit = unit;
		this.type = type;
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
	public String getUnit(){
		return unit;
	}
	public void setUnit(String unit){
		this.unit = unit;
	}
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type = type;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
}
