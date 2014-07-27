package com.ems.lifetracker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataManager extends SQLiteOpenHelper{
		
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "lifetracker";
    private static final String TABLE_METRICS = "metrics";
    private static final String TABLE_INSTANCES = "instances";
 
    // Common Table Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    
    //Metrics Table Column Names
    private static final String KEY_DESC = "desc";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DFLT = "dflt";
    
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
                + KEY_DFLT + " INT" + ")";
        db.execSQL(CREATE_METRICS_TABLE);
        
        String CREATE_INSTANCES_TABLE = "CREATE TABLE "	+ TABLE_INSTANCES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
                + KEY_NAME + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_COUNT + " INT,"
                + KEY_DETAILS + " TEXT" + ")";
        db.execSQL(CREATE_INSTANCES_TABLE);
    }
    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METRICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTANCES);
    
        // Create tables again
        this.onCreate(db);
    }
    
    public Metric getMetricByName(String name){
    	Metric metric = null;
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(TABLE_METRICS, new String[] { 
        		KEY_ID, KEY_NAME, KEY_DESC, KEY_UNIT, KEY_TYPE, KEY_DFLT }, KEY_NAME + "=?",
                new String[] { name }, null, null, null, null);
    	
    	if (cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
			metric = new Metric(
					cursor.getString(1), 
					cursor.getString(2), 
					cursor.getString(3), 
					cursor.getString(4), 
					Integer.parseInt(cursor.getString(5))
					);
    	}
    	cursor.close();
    	return metric;
    }
    
    public List<Metric> getAllMetrics(){
    	List<Metric> metrics = new ArrayList<Metric>();
    	
    	String query = "SELECT * FROM " + TABLE_METRICS;
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
		cursor.moveToPosition(-1);
    	
    	while(cursor.moveToNext()){
    		metrics.add(
				new Metric(
						cursor.getString(1), 
						cursor.getString(2), 
						cursor.getString(3), 
						cursor.getString(4), 
						Integer.parseInt(cursor.getString(5))
						)
			);
    	}
    	cursor.close();
    	
    	return metrics;
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
    
    public boolean deleteMetricByName(String name){
    	SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_METRICS, KEY_NAME + "='" + name +"'", null) > 0;
    }
    
    public boolean saveEntries(ArrayList<MetricEntry> entries, String date){
    	SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args;
        for(MetricEntry e : entries){
        	args = new ContentValues();
        	args.put(KEY_NAME, e.getName());
        	args.put(KEY_DATE, date);
        	args.put(KEY_COUNT, e.getCount());
        	args.put(KEY_DETAILS, e.getDetails());
        	
        	int updatedRows = db.update(
        			TABLE_INSTANCES, 
        			args, 
        			KEY_NAME +"=? AND "+ KEY_DATE +"=?", 
        			new String[]{e.getName(), date}
        			);
        	
        	if(updatedRows == 0){
        		Log.d("INSERTED", args.toString());
        		db.insert(TABLE_INSTANCES, null, args);
        	}else{
        		Log.d("UPDATED", args.toString());
        	}
        }
    	return true;
    }
}
