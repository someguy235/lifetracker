package com.ems.lifetracker;

public class Metric {
	//user
	private String name;
	private String desc;
	private String unit; // name of unit (miles, hours, glasses, etc.)
	private String type; //TODO: restrict this to binary/numeric/increment
	//private String isBinaryType; // yes/no metrics, eg went out to lunch
	//private String isNumericType; // enter a value metric, eg "9" for distance ran
	//private String isIncrementType; // accumulated throughout metric, eg glasses of water
	
	public Metric(){
	
	}
	
	public Metric(String name, String desc, String unit, String type){
		this.name = name;
		this.desc = desc;
		this.unit = unit;
		this.type = type;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
}
