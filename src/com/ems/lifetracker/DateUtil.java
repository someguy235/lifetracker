package com.ems.lifetracker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class DateUtil {
	static private Calendar cal;
	static private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	private DateUtil(){
	}
	
	
	public static Date dateFromString(String inDate){
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		cal = Calendar.getInstance();
		try{
			cal.setTime(formatter.parse(inDate));
		}catch(ParseException p){
			//calendar still set to today
		}
		cal.set(Calendar.DATE, cal.get(Calendar.DATE));
		return cal.getTime();
	}
	
	// Get date in format used by the db
	public static String getFormattedDate(Date inDate){
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String date;
		
		if(inDate == null){
			cal = Calendar.getInstance();
			date = formatter.format(cal.getTime()); 
		}else{
			date = formatter.format(inDate);
		}
	    return date;
	}
	
	// Get date in format for display on track screen
	public static String getFormattedDay(String inDate){
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		cal = Calendar.getInstance();
		try{
			cal.setTime(formatter.parse(inDate));
		}catch(ParseException p){
			//calendar still set to today
		}
		return new SimpleDateFormat("EE M/d", Locale.US).format(cal.getTime());
	}
	
	public static String getOffsetDate(String inDate, int offset){
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		cal = Calendar.getInstance();
		try{
			cal.setTime(formatter.parse(inDate));
		}catch(ParseException p){
			//calendar still set to today
		}
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + offset);
		return formatter.format(cal.getTime());
	}
	
	/*
	public static List<Date> getLastTenDays(){
		//formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		List<Date> dates = new ArrayList<Date>();
		
		cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		for(int i=0; i<10; i++){
			dates.add(cal.getTime());
			cal.set(Calendar.DATE, cal.get(Calendar.DATE)-1);
		}
		return dates;
	}
	*/
}
