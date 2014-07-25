package com.ems.lifetracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
	static private Calendar cal;
	static private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	private DateUtil(){
		
	}
	
	public static String getFormattedDate(String inDate){
		String date;
		
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		if(inDate.equals("today")){
			 cal = Calendar.getInstance();
			 date = formatter.format(cal.getTime()); 
		}else{
			date = formatter.format(inDate);
		}
	    return date;
	}
}
