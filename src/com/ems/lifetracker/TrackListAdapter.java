package com.ems.lifetracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TrackListAdapter extends ArrayAdapter<Metric> {
	private Metric metric;
	
    public TrackListAdapter(Context context, ArrayList<Metric> metrics) {
       super(context, R.layout.track_list_item, metrics);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       metric = getItem(position);    
       Log.d("METRIC", metric.getName());
       TextView mName, mCount, mDesc, mUnit, mType;
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   if(metric.getType().equals("binary")){
			   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_binary_item, parent, false);
			   mName = (TextView) convertView.findViewById(R.id.track_list_binary_item_name);
			   mName.setText(metric.getName());
			   
			   mCount = (TextView) convertView.findViewById(R.id.track_list_binary_item_count);
			   mCount.setText("Done? "+ (metric.getDflt() == 0 ? "No" : "Yes"));
			   
			   // Set click listeners for interface components
			   View mDone = convertView.findViewById(R.id.track_list_binary_item_done);
			   mDone.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   Log.d("DONE", metric.getName());
				   }
		       });
			   
			   View mDetails = convertView.findViewById(R.id.track_list_binary_item_details);
			   mDetails.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   Log.d("DETAILS", metric.getName());
				   }
		       });
			   
			   // Populate the data into the template view using the data object
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));
    	   }else if(metric.getType().equals("increment")){
    		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_increment_item, parent, false);
			   
    		   mName = (TextView) convertView.findViewById(R.id.track_list_increment_item_name);
			   mName.setText(metric.getName());
		       
			   mCount = (TextView) convertView.findViewById(R.id.track_list_increment_item_count);
			   mCount.setText(metric.getDflt() +" "+ metric.getUnit());

			   // Set click listeners for interface components
			   View mMore = convertView.findViewById(R.id.track_list_increment_item_more);
			   mMore.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   Log.d("MORE", metric.getName());
				   }
		       });
			   View mLess = convertView.findViewById(R.id.track_list_increment_item_less);
			   mLess.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   Log.d("LESS", metric.getName());
				   }
		       });
			   View mDetails = convertView.findViewById(R.id.track_list_increment_item_details);
			   mDetails.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   Log.d("DETAILS", metric.getName());
				   }
		       });
			   
			   // Populate the data into the template view using the data object
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));
    	   }else if(metric.getType().equals("count")){
			   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_count_item, parent, false);

			   mName = (TextView) convertView.findViewById(R.id.track_list_count_item_name);
			   mName.setText(metric.getName());
		       
			   mCount = (TextView) convertView.findViewById(R.id.track_list_count_item_count);
			   mCount.setText(metric.getDflt() +" "+ metric.getUnit());

			   // Set click listeners for interface components
			   View mEdit = convertView.findViewById(R.id.track_list_count_item_edit);
			   mEdit.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   Log.d("EDIT", metric.getName());
				   }
		       });
			   View mDetails = convertView.findViewById(R.id.track_list_count_item_details);
			   mDetails.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
					   Log.d("DETAILS", metric.getName());
				   }
		       });

			   //convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tilegreen));
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));
    	   }
       }
       // Return the completed view to render on screen
       return convertView;
   }
}