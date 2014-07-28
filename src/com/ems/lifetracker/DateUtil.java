package com.ems.lifetracker;

import java.text.DateFormat;
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
	
	public static String getFormattedDate(String inDate){
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String date;
		
		if(inDate.equals("today")){
			cal = Calendar.getInstance();
			date = formatter.format(cal.getTime()); 
		}else{
			date = formatter.format(inDate);
		}
	    return date;
	}
	
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
}
