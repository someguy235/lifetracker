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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackListAdapter extends ArrayAdapter<MetricEntry> {
	private ArrayList<MetricEntry> entries;
	private ArrayList<Integer> updated = new ArrayList<Integer>();
	private int pos;
	
    public TrackListAdapter(Context context, ArrayList<MetricEntry> entries) {
       super(context, R.layout.track_list_item, entries);
       this.entries = entries;
    }
    
    public ArrayList<MetricEntry> getEntries(){
    	return this.entries;
    }
    
    public void resetUpdated(){
    	this.updated.clear();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	// Get the data item for this position
    	
    	MetricEntry metric = getItem(position);    
    	pos = position;
    	
   		TextView mName, mCount, mUnit, mDetails;
   		ImageView mDone;
       
       /*
        *  Binary-based metrics
        */
	   if(metric.getType().equals("binary")){
		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_binary_item, parent, false);
		   mName = (TextView) convertView.findViewById(R.id.track_list_binary_item_name);
		   mName.setText(metric.getName());
		   
		   mCount = (TextView) convertView.findViewById(R.id.track_list_binary_item_count);
		   mCount.setText(metric.getCount() == 0 ? "No" : "Yes");
		   
		   mDone = (ImageView) convertView.findViewById(R.id.track_list_binary_item_done);
		   mDone.setImageResource(metric.getCount() == 0 ? R.drawable.check : R.drawable.cross);
		   
		   // Set click listeners for interface components
		   mDone.setOnClickListener(new OnClickListener() {
			   int ePos = pos;
			   @Override
			   public void onClick(View v) {
				   MetricEntry e = entries.get(ePos);
				   e.setCount(e.getCount() == 0 ? 1 : 0);
				   TextView mCount = (TextView)(((View)v.getTag()).findViewById(R.id.track_list_binary_item_count));
				   mCount.setText(e.getCount() == 0 ? "No" : "Yes");
				   ImageView mDone = (ImageView)v;
				   mDone.setImageResource(e.getCount() == 0 ? R.drawable.check : R.drawable.cross);
				   ((View)v.getTag()).setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
				   updated.add(ePos);
			   }
	       });
		   mDone.setTag(convertView);
		   
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
				   final EditText input = new EditText(getContext());
				   input.setInputType(InputType.TYPE_CLASS_TEXT);
				   
				   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				    	   e.setDetails(input.getText().toString());
						   TextView mDetails = (TextView)v;
						   mDetails.setText("" + e.getDetails());
						   ((View)v.getTag()).setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
						   updated.add(ePos);
				       }
				   });
				   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				           dialog.cancel();
				       }
				   });
	
				   final AlertDialog dialog = builder.create();
				   dialog.setTitle("Details");

				   input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					   @Override
					   public void onFocusChange(View v, boolean hasFocus) {
						   if (hasFocus) {
							   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						   }
					   }
				   });
				   input.setText(e.getDetails());
				   input.setSelection(input.getText().length());
				   dialog.setView(input);
				   
				   dialog.show();
			   }
	       });
		   mDetails.setTag(convertView);
		   
		   // Populate the data into the template view using the data object
		   if(updated.contains(pos)){
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
		   }else{
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_default));
		   }
	       
       /*
        *  Increment-based metrics 
        */
	   }else if(metric.getType().equals("increment")){
		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_increment_item, parent, false);
		   
		   mName = (TextView) convertView.findViewById(R.id.track_list_increment_item_name);
		   mName.setText(metric.getName());
	       
		   mCount = (TextView) convertView.findViewById(R.id.track_list_increment_item_count);
		   mCount.setText((metric.getCount() % 1.0 == 0.0) ? ""+ (int)metric.getCount() : ""+ metric.getCount());
		   
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
				   TextView mCount = (TextView)((View)v.getTag()).findViewById(R.id.track_list_increment_item_count);
				   mCount.setText((e.getCount() % 1.0 == 0.0) ? ""+ (int)e.getCount() : ""+ e.getCount());
				   ((View)v.getTag()).setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
				   updated.add(ePos);
			   }
	       });
		   mMore.setTag(convertView);
		   
		   View mLess = convertView.findViewById(R.id.track_list_increment_item_less);
		   mLess.setOnClickListener(new OnClickListener() {
			   int ePos = pos;
			   @Override
			   public void onClick(View v) {
				   MetricEntry e = entries.get(ePos);
				   if(e.getCount() > 0){
					   e.setCount(e.getCount() - 1);
					   TextView mCount = (TextView)((View)v.getTag()).findViewById(R.id.track_list_increment_item_count);
					   mCount.setText((e.getCount() % 1.0 == 0.0) ? ""+ (int)e.getCount() : ""+ e.getCount());
					   ((View)v.getTag()).setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
					   updated.add(ePos);
				   }
			   }
	       });
		   mLess.setTag(convertView);
		   
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
				   final EditText input = new EditText(getContext());
				   input.setInputType(InputType.TYPE_CLASS_TEXT);
				   
				   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				    	   e.setDetails(input.getText().toString());
						   TextView mDetails = (TextView)v;
						   mDetails.setText("" + e.getDetails());
						   ((View)v.getTag()).setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
						   updated.add(ePos);
				       }
				   });
				   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				           dialog.cancel();
				       }
				   });
	
				   final AlertDialog dialog = builder.create();
				   dialog.setTitle("Details");

				   input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					   @Override
					   public void onFocusChange(View v, boolean hasFocus) {
						   if (hasFocus) {
							   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						   }
					   }
				   });
				   input.setText(e.getDetails());
				   input.setSelection(input.getText().length());
				   dialog.setView(input);
				   
				   dialog.show();
			   }
	       });
		   mDetails.setTag(convertView);
		   
		   // Populate the data into the template view using the data object
		   if(updated.contains(pos)){
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
		   }else{
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_default));
		   }
	       
       /*
        *  Count-based metrics
        */
	   }else if(metric.getType().equals("count")){
		   convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list_count_item, parent, false);

		   mName = (TextView) convertView.findViewById(R.id.track_list_count_item_name);
		   mName.setText(metric.getName());
	       
		   mCount = (TextView) convertView.findViewById(R.id.track_list_count_item_count);
		   mCount.setText((metric.getCount() % 1.0 == 0.0) ? ""+ (int)metric.getCount() : ""+ metric.getCount());
		   
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
				   final EditText input = new EditText(getContext());
				   input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

				   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				    	   e.setCount(Double.parseDouble(input.getText().toString()));
						   TextView mCount = (TextView)((View)v.getTag()).findViewById(R.id.track_list_count_item_count);
						   mCount.setText((e.getCount() % 1.0 == 0.0) ? ""+ (int)e.getCount() : ""+ e.getCount());
						   ((View)v.getTag()).setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
						   updated.add(ePos);
				       }
				   });
				   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				           dialog.cancel();
				       }
				   });
				   
				   final AlertDialog dialog = builder.create();
				   dialog.setTitle(e.getUnit());

				   input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					   @Override
					   public void onFocusChange(View v, boolean hasFocus) {
						   if (hasFocus) {
							   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						   }
					   }
				   });
				   if(e.getCount() != 0.0){
					   input.setText(""+ e.getCount());
					   input.setSelection(input.getText().length());
				   }
				   
				   dialog.setView(input);
				   
				   dialog.show();
			   }
	       });
		   mEdit.setTag(convertView);
		   
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
				   final EditText input = new EditText(getContext());
				   input.setInputType(InputType.TYPE_CLASS_TEXT);
				   
				   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				    	   e.setDetails(input.getText().toString());
						   TextView mDetails = (TextView)v;
						   mDetails.setText("" + e.getDetails());
						   ((View)v.getTag()).setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
						   updated.add(ePos);
				       }
				   });
				   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				       @Override
				       public void onClick(DialogInterface dialog, int which) {
				           dialog.cancel();
				       }
				   });
	
				   final AlertDialog dialog = builder.create();
				   dialog.setTitle("Details");

				   input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					   @Override
					   public void onFocusChange(View v, boolean hasFocus) {
						   if (hasFocus) {
							   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						   }
					   }
				   });
				   input.setText(e.getDetails());
				   input.setSelection(input.getText().length());
				   dialog.setView(input);
				   
				   dialog.show();
			   }
	       });
		   mDetails.setTag(convertView);
		   
		   if(updated.contains(pos)){
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
		   }else{
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_default));
		   }
	   }

	   // Return the completed view to render on screen
       return convertView;
   }
}