package com.ems.lifetracker;

import java.util.ArrayList;

import android.content.Context;
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
       
       //TODO: do this dynamically based on metric type?
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_item, parent, false);
       }else{
       }
       
       // Lookup view for data population
       TextView mName = (TextView) convertView.findViewById(R.id.track_list_item_name);
       TextView mDesc = (TextView) convertView.findViewById(R.id.track_list_item_desc);
       TextView mUnit = (TextView) convertView.findViewById(R.id.track_list_item_unit);
       TextView mType = (TextView) convertView.findViewById(R.id.track_list_item_type);
       
       // Populate the data into the template view using the data object
       mName.setText(metric.getName());
       mDesc.setText(metric.getDesc());
       mUnit.setText(metric.getUnit());
       mType.setText(metric.getType());
       
       // Return the completed view to render on screen
       return convertView;
   }
}