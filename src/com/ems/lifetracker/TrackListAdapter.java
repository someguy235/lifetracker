package com.ems.lifetracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TrackListAdapter extends ArrayAdapter<Metric> {
    public TrackListAdapter(Context context, ArrayList<Metric> metrics) {
       super(context, R.layout.track_list_item, metrics);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Metric metric = getItem(position);    
       TextView mName, mCount, mDesc, mUnit, mType;
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   if(metric.getType().equals("binary")){
			   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_binary_item, parent, false);
			   mName = (TextView) convertView.findViewById(R.id.track_list_binary_item_name);
			   mName.setText(metric.getName());
			   
			   mCount = (TextView) convertView.findViewById(R.id.track_list_binary_item_count);
			   mCount.setText("Done? "+ (metric.getDflt() == 0 ? "No" : "Yes"));
			   
			   // Populate the data into the template view using the data object
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));
    	   }else if(metric.getType().equals("increment")){
    		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_increment_item, parent, false);
			   
    		   mName = (TextView) convertView.findViewById(R.id.track_list_increment_item_name);
			   mName.setText(metric.getName());
		       
//			   String mCountStr = metric.getUnit();
//			   mCountStr += mCountStr == null ? "" : " : ";
//			   mCountStr += metric.getDflt();
//			   mCount.setText(mCountStr);

			   mCount = (TextView) convertView.findViewById(R.id.track_list_increment_item_count);
			   mCount.setText(metric.getDflt() +" "+ metric.getUnit());
			   // Populate the data into the template view using the data object
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));
    	   }else if(metric.getType().equals("count")){
			   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_count_item, parent, false);

			   mName = (TextView) convertView.findViewById(R.id.track_list_count_item_name);
			   mName.setText(metric.getName());
		       
			   mCount = (TextView) convertView.findViewById(R.id.track_list_count_item_count);
			   mCount.setText(metric.getDflt() +" "+ metric.getUnit());
			   //mDesc = (TextView) convertView.findViewById(R.id.track_list_item_desc);
		       //mUnit = (TextView) convertView.findViewById(R.id.track_list_item_unit);
		       //mType = (TextView) convertView.findViewById(R.id.track_list_item_type);
			   
		       // Populate the data into the template view using the data object
		       //mDesc.setText(metric.getDesc());
		       //mUnit.setText(metric.getUnit());
		       //mType.setText(metric.getType());
			   
		       //convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tilegreen));
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));

    	   }
    	   
       }
       
       // Return the completed view to render on screen
       return convertView;
   }
}