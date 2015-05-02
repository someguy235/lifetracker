package com.ems.lifetracker.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ems.lifetracker.domain.*;

public class DataManager extends SQLiteOpenHelper{
		
    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "lifetracker";
    private static final String TABLE_METRICS = "metrics";
    private static final String TABLE_INSTANCES = "instances";
 
    // Common Table Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_TYPE = "type";
    
    //Metrics Table Column Names
    private static final String KEY_DESC = "desc";
    private static final String KEY_DFLT = "dflt";
    private static final String KEY_ARCH = "arch";
    
    //Instances Table Column Names
    private static final String KEY_DATE = "date";
    private static final String KEY_COUNT = "count";
    private static final String KEY_DETAILS = "details";
	
    public DataManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_METRICS_TABLE = "CREATE TABLE " + TABLE_METRICS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
                + KEY_NAME + " TEXT,"
                + KEY_DESC + " TEXT,"
                + KEY_UNIT + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_DFLT + " REAL,"
                + KEY_ARCH + " TEXT" + ")";
        db.execSQL(CREATE_METRICS_TABLE);
        
        String CREATE_INSTANCES_TABLE = "CREATE TABLE "	+ TABLE_INSTANCES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
                + KEY_NAME + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_UNIT + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_COUNT + " REAL,"
                + KEY_DETAILS + " TEXT" + ")";
        db.execSQL(CREATE_INSTANCES_TABLE);
    }
    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_METRICS);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTANCES);
        // Create tables again
        //this.onCreate(db);

        db.execSQL("ALTER TABLE "+ TABLE_METRICS +" ADD COLUMN "+ KEY_ARCH +" TEXT");
    }
    
    public Metric getMetricByName(String name){
    	Metric metric = null;
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(
                TABLE_METRICS,
                new String[] { KEY_ID, KEY_NAME, KEY_DESC, KEY_UNIT, KEY_TYPE, KEY_DFLT, KEY_ARCH },
                KEY_NAME + "=?",
                new String[] { name }, null, null, null, null);
    	
    	if (cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
			metric = new Metric(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getDouble(5),
                cursor.getString(6)
			);
    	}
    	cursor.close();
    	db.close();
    	return metric;
    }
    
    private List<Metric> getMetrics(String type){
    	List<Metric> metrics = new ArrayList<Metric>();
    	SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

    	if(type.equals("all")){
            cursor = db.query(
                    TABLE_METRICS,
                    new String[] { KEY_ID, KEY_NAME, KEY_DESC, KEY_UNIT, KEY_TYPE, KEY_DFLT, KEY_ARCH },
                    null, null, null, null, null, null
            );
    	}else if(type.equals("active")){
            cursor = db.query(
                    TABLE_METRICS,
                    new String[] { KEY_ID, KEY_NAME, KEY_DESC, KEY_UNIT, KEY_TYPE, KEY_DFLT, KEY_ARCH },
                    KEY_NAME + " IN (SELECT DISTINCT("+ KEY_NAME +") FROM "+ TABLE_INSTANCES +")",
                    null, null, null, null, null
            );
    	}else if(type.equals("nonarchived")){
            cursor = db.query(
                    TABLE_METRICS,
                    new String[] { KEY_ID, KEY_NAME, KEY_DESC, KEY_UNIT, KEY_TYPE, KEY_DFLT, KEY_ARCH },
                    KEY_ARCH + " =='' OR "+ KEY_ARCH +" IS NULL",
                    null, null, null, null, null
            );
        }

        if(cursor != null) {
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                metrics.add(
                    new Metric(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getString(6)
                    )
                );
            }
            cursor.close();
        }

    	db.close();
    	
    	return metrics;
    }
    
    public List<Metric> getAllMetrics(){
    	return getMetrics("all");
    }
    
    public List<Metric> getAllNonEmptyMetrics(){
    	return getMetrics("active");
    }

    public List<Metric> getAllNonArchivedMetrics(){
        return getMetrics("nonarchived");
    }
    
    public boolean addMetric(Metric metric){
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
	    values.put(KEY_NAME, metric.getName()); 
	    values.put(KEY_DESC, metric.getDesc()); 
	    values.put(KEY_UNIT, metric.getUnit()); 
	    values.put(KEY_TYPE, metric.getType());
	    values.put(KEY_DFLT, metric.getDflt());
	     
	    //TODO: check for duplicates?
	    long success = db.insert(TABLE_METRICS, null, values);
	    db.close(); 
	    
	    return success >= -1;
    }
    
    public boolean updateMetric(Metric metric){
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
	    
	    values.put(KEY_DESC, metric.getDesc()); 
	    values.put(KEY_UNIT, metric.getUnit()); 
	    values.put(KEY_TYPE, metric.getType());
	    values.put(KEY_DFLT, metric.getDflt());
        values.put(KEY_ARCH, metric.getArch());
    	
    	long success = db.update(
    			TABLE_METRICS, 
    			values, 
    			KEY_NAME +"=?", 
    			new String[]{metric.getName()}
    			);
    	
    	return success >= -1;
    }
    
    public boolean deleteMetricByName(String name){
    	SQLiteDatabase db = this.getWritableDatabase();
    	
		long success = db.delete(TABLE_METRICS, KEY_NAME + "='" + name +"'", null);
		if(success >= -1){
			success = db.delete(TABLE_INSTANCES, KEY_NAME + "='" + name +"'", null);
		}
		db.close();
        		
		return success >= -1;
    }

    public boolean archiveMetricByName(String name, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        if(date.equals(""))
            date = null;
        args.put(KEY_ARCH, date);

        long success = db.update(TABLE_METRICS, args, KEY_NAME +"=?", new String[]{name});

        //System.out.println("name: "+ name +", date: "+ date +", success: "+ success);
        return success >= -1;
    }
    
    public boolean saveEntries(List<MetricEntry> entries, String date){
    	SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args;
        for(MetricEntry e : entries){
        	args = new ContentValues();
        	args.put(KEY_NAME, e.getName());
        	args.put(KEY_DATE, date);
        	args.put(KEY_UNIT, e.getUnit());
        	args.put(KEY_TYPE, e.getType());
        	args.put(KEY_COUNT, e.getCount());
        	args.put(KEY_DETAILS, e.getDetails());
        	
        	int updatedRows = db.update(
        			TABLE_INSTANCES, 
        			args, 
        			KEY_NAME +"=? AND "+ KEY_DATE +"=?", 
        			new String[]{e.getName(), date}
        			);
        	
        	if(updatedRows == 0){
        		db.insert(TABLE_INSTANCES, null, args);
        	}
        }

        db.close();
    	
    	return true;
    }
    
    public List<MetricEntry> getEntriesByDate(String date, boolean includeArchived){
    	SQLiteDatabase db = this.getReadableDatabase();
    	List<MetricEntry> entries = new ArrayList<MetricEntry>();
    	HashMap<String, MetricEntry> entriesHash = new HashMap<String, MetricEntry>();
    	MetricEntry entry;
        String queryString;

        if(includeArchived) {
            queryString = KEY_DATE + "=?";
        }else{
            queryString = KEY_DATE + "=? AND "+ KEY_NAME +" in (SELECT "+ KEY_NAME +" FROM "+ TABLE_METRICS +" WHERE "+ KEY_ARCH +" IS null)";
        }

    	Cursor cursor = db.query(
                TABLE_INSTANCES,
                new String[] { KEY_NAME, KEY_UNIT, KEY_TYPE, KEY_COUNT, KEY_DETAILS },
                queryString,
                new String[] { date },
                null, null, null, null);

        if(cursor != null) {
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                entry = new MetricEntry(
                        cursor.getString(0),
                        date,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                );
                entries.add(entry);
                entriesHash.put(entry.getName(), entry);
            }
            cursor.close();
            db.close();

            // add metric with default values if no entry exists
            List<Metric> metrics = getAllMetrics();
            for (Metric m : metrics) {
                if (!entriesHash.containsKey(m.getName()) && (m.getArch() == null || m.getArch().compareTo(date) > 0)) {
                    entries.add(new MetricEntry(
                                    m.getName(),
                                    date,
                                    m.getUnit(),
                                    m.getType(),
                                    m.getDflt(),
                                    null
                            )
                    );
                }
            }
        }
    	
    	return entries;
    }
    
    public List<MetricEntry> getEntriesByName(String name){
    	return getEntriesByNameAndTimeframe(name, "all");
    }
    
    public List<MetricEntry> getEntriesByNameAndTimeframe(String name, String timeframe){
    	List<MetricEntry> entries = new ArrayList<MetricEntry>();
    	List<String> dates = new ArrayList<String>();
    	MetricEntry entry;

    	String minDate = "0000-00-00";
    	if(timeframe.equals("week")){
    		minDate = DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), -7);
    	}else if(timeframe.equals("month")){
    		minDate = DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), -30);
    	}

        Metric metric = getMetricByName(name);
        String maxDate = DateUtil.getFormattedDate(null);
        if(metric.getArch() != null && !metric.getArch().equals("")){
            maxDate = metric.getArch();
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_INSTANCES,
                new String[] { KEY_DATE, KEY_UNIT, KEY_TYPE, KEY_COUNT, KEY_DETAILS },
                KEY_NAME + "=? AND " + KEY_DATE + ">? AND "+ KEY_DATE +"<?",
                new String[] { name, minDate, maxDate }, null, null, KEY_DATE, null);

    	cursor.moveToPosition(-1);
    	while(cursor.moveToNext()){
			entry = new MetricEntry(
					name,
					cursor.getString(0), 
					cursor.getString(1), 
					cursor.getString(2), 
					cursor.getDouble(3),
					cursor.getString(4)
					);
			entries.add(entry);
			dates.add(entry.getDate());
    	}
    	cursor.close();
    	db.close();
    	
    	// Fill in gaps in entries with the Metric default value
    	if(entries.size() > 0){
	    	Collections.sort(dates);
	    	String date = dates.get(0);
            while(date.compareTo(maxDate) <= 0){
	    		if(!dates.contains(date)){
	    			entry = new MetricEntry(
	    					name,
	    					date,
	    					metric.getUnit(), 
	    					metric.getType(), 
	    					metric.getDflt(),
	    					null
	    					);
	    			entries.add(entry);
	    		}
	    		date = DateUtil.getOffsetDate(date, 1);
	    	}
    	
	    	Collections.sort(entries, new Comparator<MetricEntry>() {
	            @Override
	            public int compare(MetricEntry e1, MetricEntry e2){
	                return  e1.getDate().compareTo(e2.getDate());
	            }
	        });
    	}
    	
    	return entries;
    }
}
