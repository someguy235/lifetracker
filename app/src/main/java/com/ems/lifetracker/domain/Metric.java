package com.ems.lifetracker.domain;

public class Metric {
	//user
	private String name;
	private String desc;
	private String unit; // name of unit (miles, hours, glasses, etc.)
	private String type; 
	private double dflt; // default value
	private String arch; // archive date

	public Metric(){
	
	}
	
	public Metric(String name, String desc, String unit, String type, double dflt){
		this.name = name;
		this.desc = desc;
		this.unit = unit;
		this.type = type;
		this.dflt = dflt;
	}

    public Metric(String name, String desc, String unit, String type, double dflt, String arch) {
        this.name = name;
        this.desc = desc;
        this.unit = unit;
        this.type = type;
        this.dflt = dflt;
        if (arch == null || arch.equals("")){
            this.arch = null;
        }else {
            this.arch = arch;
        }
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
	
	public void setDflt(double dflt) {
		this.dflt = dflt;
	}
	
	public double getDflt(){
		return dflt;
	}

    public void setArch(String arch) {
        if(arch.equals("") || arch == null)
            this.arch = null;
    }

    public String getArch(){
        return arch;
    }
}
