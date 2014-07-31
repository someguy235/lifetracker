package com.ems.lifetracker;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MetricsListAdapter extends ArrayAdapter<Metric> {
    public MetricsListAdapter(Context context, ArrayList<Metric> metrics) {
       super(context, R.layout.metrics_list_item, metrics);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Metric metric = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.metrics_list_item, parent, false);
       }
       
       // Lookup view for data population
       TextView mName = (TextView) convertView.findViewById(R.id.metrics_list_item_name);
       TextView mDesc = (TextView) convertView.findViewById(R.id.metrics_list_item_desc);
       TextView mUnit = (TextView) convertView.findViewById(R.id.metrics_list_item_unit);
       //TextView mType = (TextView) convertView.findViewById(R.id.metrics_list_item_type);
        
       // Populate the data into the template view using the data object
       mName.setText(metric.getName());
       if(metric.getDesc().equals("") || metric.getUnit().equals("")){
   		   mDesc.setText(metric.getDesc());
   	   }else{
   		   mDesc.setText(metric.getDesc() + " - ");
   	   }
       mUnit.setText(metric.getUnit());
	   //mType.setText(metric.getType());
       
       // Return the completed view to render on screen
       return convertView;
   }
}