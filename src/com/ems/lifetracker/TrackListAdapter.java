package com.ems.lifetracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackListAdapter extends ArrayAdapter<MetricEntry> {
	//private Metric metric; //TODO: need this?
	//private MetricEntry entry;
	private ArrayList<MetricEntry> entries;
	private int pos;
	
    public TrackListAdapter(Context context, ArrayList<MetricEntry> entries) {
       super(context, R.layout.track_list_item, entries);
       //TODO: get these from the database
       //entries = new ArrayList<MetricEntry>();
       this.entries = entries;
       //TODO: get the date being edited
       /*
       MetricEntry e;
       for(Metric m : metrics){
    	   e = new MetricEntry(m.getName(), DateUtil.getFormattedDate("today"), m.getDflt(), "");
    	   entries.add(e);
       }
       */
    }
    
    public ArrayList<MetricEntry> getEntries(){
    	return this.entries;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       MetricEntry metric = getItem(position);    
       pos = position;
       
       TextView mName, mCount, mUnit, mDetails;
       ImageView mDone;
       
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   if(metric.getType().equals("binary")){
			   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_binary_item, parent, false);
			   mName = (TextView) convertView.findViewById(R.id.track_list_binary_item_name);
			   mName.setText(metric.getName());
			   
			   mCount = (TextView) convertView.findViewById(R.id.track_list_binary_item_count);
			   mCount.setText(metric.getCount() == 0 ? "No" : "Yes");
			   
			   mDone = (ImageView) convertView.findViewById(R.id.track_list_binary_item_done);
			   mDone.setImageResource(metric.getCount() == 0 ? R.drawable.check : R.drawable.cross);
			   
			   // Set click listeners for interface components
			   //View mDone = convertView.findViewById(R.id.track_list_binary_item_done);
			   mDone.setOnClickListener(new OnClickListener() {
				   int ePos = pos;
				   @Override
				   public void onClick(View v) {
					   MetricEntry e = entries.get(ePos);
					   e.setCount(e.getCount() == 0 ? 1 : 0);
					   TextView mCount = (TextView)v.getTag();
					   mCount.setText(e.getCount() == 0 ? "No" : "Yes");
					   ImageView mDone = (ImageView)v;
					   mDone.setImageResource(e.getCount() == 0 ? R.drawable.check : R.drawable.cross);
				   }
		       });
			   mDone.setTag(convertView.findViewById(R.id.track_list_binary_item_count));
			   
			   mDetails = (TextView)convertView.findViewById(R.id.track_list_binary_item_details);
			   if(metric.getDetails() != null){
				   mDetails.setText(metric.getDetails());
			   }
			   mDetails.setOnClickListener(new OnClickListener() {
				   int ePos = pos;
				   @Override
				   public void onClick(final View v) {
					   final MetricEntry e = entries.get(ePos);
					   AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					   builder.setTitle("Details");

					   final EditText input = new EditText(getContext());
					   input.setInputType(InputType.TYPE_CLASS_TEXT);
					   builder.setView(input);

					   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					    	   e.setDetails(input.getText().toString());
							   TextView mDetails = (TextView)v;
							   mDetails.setText("" + e.getDetails());
					       }
					   });
					   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					           dialog.cancel();
					       }
					   });
					   builder.show();
				   }
		       });
			   
			   // Populate the data into the template view using the data object
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));
    	   }else if(metric.getType().equals("increment")){
    		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_increment_item, parent, false);
			   
    		   mName = (TextView) convertView.findViewById(R.id.track_list_increment_item_name);
			   mName.setText(metric.getName());
		       
			   mCount = (TextView) convertView.findViewById(R.id.track_list_increment_item_count);
			   //TODO
			   String mText;
			   if(metric.getCount() % 1.0 == 0.0){
				   mText = ""+ (int)metric.getCount();
			   }else{
				   mText = ""+ metric.getCount();
			   }
			   mCount.setText(mText);
			   
			   mUnit = (TextView) convertView.findViewById(R.id.track_list_increment_item_unit);
			   mUnit.setText(" "+ metric.getUnit());

			   // Set click listeners for interface components
			   View mMore = convertView.findViewById(R.id.track_list_increment_item_more);
			   mMore.setOnClickListener(new OnClickListener() {
				   int ePos = pos;
				   @Override
				   public void onClick(View v) {
					   MetricEntry e = entries.get(ePos);
					   e.setCount(e.getCount() + 1);
					   TextView mCount = (TextView)v.getTag();
					   mCount.setText("" + e.getCount());
				   }
		       });
			   mMore.setTag(convertView.findViewById(R.id.track_list_increment_item_count));
			   
			   View mLess = convertView.findViewById(R.id.track_list_increment_item_less);
			   mLess.setOnClickListener(new OnClickListener() {
				   int ePos = pos;
				   @Override
				   public void onClick(View v) {
					   MetricEntry e = entries.get(ePos);
					   if(e.getCount() > 0){
						   e.setCount(e.getCount() - 1);
					   }
					   TextView mCount = (TextView)v.getTag();
					   mCount.setText("" + e.getCount());
				   }
		       });
			   mLess.setTag(convertView.findViewById(R.id.track_list_increment_item_count));
			   
			   mDetails = (TextView)convertView.findViewById(R.id.track_list_increment_item_details);
			   if(metric.getDetails() != null){
				   mDetails.setText(metric.getDetails());
			   }
			   mDetails.setOnClickListener(new OnClickListener() {
				   int ePos = pos;
				   @Override
				   public void onClick(final View v) {
					   final MetricEntry e = entries.get(ePos);
					   AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					   builder.setTitle("Details");

					   final EditText input = new EditText(getContext());
					   input.setInputType(InputType.TYPE_CLASS_TEXT);
					   builder.setView(input);

					   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					    	   e.setDetails(input.getText().toString());
					    	   TextView mDetails = (TextView)v;
							   mDetails.setText("" + e.getDetails());
					       }
					   });
					   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					           dialog.cancel();
					       }
					   });
					   builder.show();
				   }
		       });
			   
			   // Populate the data into the template view using the data object
		       convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tileblue));
    	   }else if(metric.getType().equals("count")){
			   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_count_item, parent, false);

			   mName = (TextView) convertView.findViewById(R.id.track_list_count_item_name);
			   mName.setText(metric.getName());
		       
			   mCount = (TextView) convertView.findViewById(R.id.track_list_count_item_count);
			   //TODO: figure this out
			   String mText;
			   if(metric.getCount() % 1.0 == 0.0){
				   mText = ""+ (int)metric.getCount();
			   }else{
				   mText = ""+ metric.getCount();
			   }
			   mCount.setText(mText);
			   mUnit = (TextView) convertView.findViewById(R.id.track_list_count_item_unit);
			   mUnit.setText(" "+ metric.getUnit());

			   // Set click listeners for interface components
			   View mEdit = convertView.findViewById(R.id.track_list_count_item_edit);
			   mEdit.setOnClickListener(new OnClickListener() {
				   int ePos = pos;
				   @Override
				   public void onClick(final View v) {
					   final MetricEntry e = entries.get(ePos);
					   AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					   builder.setTitle("Count");

					   final EditText input = new EditText(getContext());
					   input.setInputType(InputType.TYPE_CLASS_TEXT);
					   builder.setView(input);

					   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					    	   e.setCount(Double.parseDouble(input.getText().toString()));
							   TextView mCount = (TextView)v.getTag();
							   mCount.setText("" + (e.getCount() % 1.0 == 0.0 ? (int)e.getCount() : e.getCount()));
					       }
					   });
					   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					           dialog.cancel();
					       }
					   });
					   builder.show();
				   }
		       }); 
			   mEdit.setTag(convertView.findViewById(R.id.track_list_count_item_count));
			   
			   mDetails = (TextView)convertView.findViewById(R.id.track_list_count_item_details);
			   if(metric.getDetails() != null){
				   mDetails.setText(metric.getDetails());
			   }
			   mDetails.setOnClickListener(new OnClickListener() {
				   int ePos = pos;
				   @Override
				   public void onClick(final View v) {
					   final MetricEntry e = entries.get(ePos);
					   AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					   builder.setTitle("Details");

					   final EditText input = new EditText(getContext());
					   input.setInputType(InputType.TYPE_CLASS_TEXT);
					   builder.setView(input);

					   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					    	   e.setDetails(input.getText().toString());
							   TextView mDetails = (TextView)v;
							   mDetails.setText("" + e.getDetails());
					       }
					   });
					   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					       @Override
					       public void onClick(DialogInterface dialog, int which) {
					           dialog.cancel();
					       }
					   });
					   builder.show();
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