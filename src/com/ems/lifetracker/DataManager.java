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
		
	    private static final int DATABASE_VERSION = 1;
	    private static final String DATABASE_NAME = "lifetracker";
	    private static final String TABLE_METRICS = "metrics";
	 
	    // Common Table Column Names
	    private static final String KEY_ID = "id";
	    
	    //Metrics Table Column Names
	    private static final String KEY_NAME = "name";
	    private static final String KEY_DESC = "desc";
	    private static final String KEY_UNIT = "unit";
	    private static final String KEY_TYPE = "type";
	    
		
	    public DataManager(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	 
	    // Creating Tables
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        String CREATE_CUSTOMERS_TABLE = "CREATE TABLE "	+ TABLE_METRICS + "("
	                + KEY_ID + " INTEGER PRIMARY KEY," 
	                + KEY_NAME + " TEXT,"
	                + KEY_DESC + " TEXT,"
	                + KEY_UNIT + " TEXT,"
	                + KEY_TYPE + " TEXT" + ")";
	        db.execSQL(CREATE_CUSTOMERS_TABLE);
	    }
	    
	    // Upgrading database
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // Drop older table if existed
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METRICS);
	    
	        // Create tables again
	        this.onCreate(db);
	    }
	    
	    public List<Metric> getAllMetrics(){
	    	List<Metric> metrics = new ArrayList<Metric>();
	    	
	    	String query = "SELECT * FROM " + TABLE_METRICS;
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	Cursor cursor = db.rawQuery(query, null);
			cursor.moveToPosition(-1);
	    	
	    	while(cursor.moveToNext()){
	    		metrics.add(
    				new Metric(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4))
				);
	    	}
	    	
	    	return metrics;
	    }
	    
	    public boolean addMetric(Metric metric){
	    	SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
		    values.put(KEY_NAME, metric.getName()); 
		    values.put(KEY_DESC, metric.getDesc()); 
		    values.put(KEY_UNIT, metric.getUnit()); 
		    values.put(KEY_TYPE, metric.getType());
		     
		    long success = db.insert(TABLE_METRICS, null, values);
		    db.close(); 
		    
		    Log.d("values: ", values.toString());
		    
		    return success >= -1;
	    }
}
