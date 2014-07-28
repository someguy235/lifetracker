package com.ems.lifetracker;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DateBarAdapter extends ArrayAdapter<Date> {
    public DateBarAdapter(Context context, ArrayList<Date> dates) {
       super(context, R.layout.date_bar_item, dates);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Date date = getItem(position);    
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.date_bar_item, parent, false);
       }
       
       // Lookup view for data population
       TextView mDay = (TextView) convertView.findViewById(R.id.date_bar_item_day);
       TextView mDate = (TextView) convertView.findViewById(R.id.date_bar_item_date);
       
       // Populate the data into the template view using the data object
       mDay.setText(""+ date.getDay());
       mDate.setText(""+ date.getDate());
       
       // Return the completed view to render on screen
       return convertView;
   }
}