package com.ems.lifetracker.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ems.lifetracker.domain.*;
import com.ems.lifetracker.R;

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
        ImageView mImg = (ImageView) convertView.findViewById(R.id.metrics_list_item_image);
        ImageView mArchImg = (ImageView) convertView.findViewById(R.id.metrics_list_archive_image);
        
        // Populate the data into the template view using the data object
        mName.setText(metric.getName());
        if(metric.getDesc().equals("") || metric.getUnit().equals("")){
   		    mDesc.setText(metric.getDesc());
   	    }else{
   		    mDesc.setText(metric.getDesc() + " - ");
   	    }
        mUnit.setText(metric.getUnit());
       
        if(metric.getType().equals("binary")){
    	    mImg.setImageResource(R.drawable.check);
        }else if(metric.getType().equals("increment")){
    	    mImg.setImageResource(R.drawable.plus);
        }else if(metric.getType().equals("count")){
    	    mImg.setImageResource(R.drawable.edit);
        }

        if(metric.getArch() == null) {
            mArchImg.setVisibility(View.GONE);
        }else{
            mArchImg.setVisibility(View.VISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
   }
}