package com.ems.lifetracker;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EntriesListAdapter extends ArrayAdapter<MetricEntry> {
    public EntriesListAdapter(Context context, ArrayList<MetricEntry> entries) {
       super(context, R.layout.entries_list_item, entries); 
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       MetricEntry entry = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.entries_list_item, parent, false);
       }
       
       // Lookup view for data population
       TextView eDate = (TextView) convertView.findViewById(R.id.metrics_entry_date);
       TextView eCount = (TextView) convertView.findViewById(R.id.metrics_entry_count);
       TextView eDetails = (TextView) convertView.findViewById(R.id.metrics_entry_details);
        
       // Populate the data into the template view using the data object
       eDate.setText(DateUtil.getFormattedDay(entry.getDate()));
       eCount.setText((entry.getCount() % 1.0 == 0.0) ? ": "+ (int)entry.getCount() : ": "+ entry.getCount());
       if(entry.getDetails() == null || entry.getDetails().equals("")){
    	   eDetails.setVisibility(View.GONE);
       }else{
    	   eDetails.setText(entry.getDetails());
       }
       
       // Return the completed view to render on screen
       return convertView;
   }
}