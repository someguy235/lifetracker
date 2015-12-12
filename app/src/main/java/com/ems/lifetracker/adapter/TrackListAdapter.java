package com.ems.lifetracker.adapter;

import java.util.ArrayList;
import java.util.List;

//import com.ems.lifetracker.fragment.TrackMainFragment.MyGestureDetector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ems.lifetracker.R;
import com.ems.lifetracker.fragment.TrackMainFragment;
import com.ems.lifetracker.domain.MetricEntry;

public class TrackListAdapter extends ArrayAdapter<MetricEntry>{
	private TrackMainFragment parentFragment;
	private ArrayList<MetricEntry> entries;
	private ArrayList<Integer> updated = new ArrayList<Integer>();
	private int pos;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    
    public TrackListAdapter(Context context, ArrayList<MetricEntry> entries, TrackMainFragment fragment) {
        super(context, R.layout.track_list_item, entries);

        this.parentFragment = fragment;
        this.entries = entries;

        //Gesture detection
        //gestureDetector = new GestureDetector(this, new MyGestureDetector());
        //gestureDetector = new GestureDetector(new MyGestureDetector());
        //gestureListener = new View.OnTouchListener() {
        //    public boolean onTouch(View v, MotionEvent event) {
        //        return gestureDetector.onTouchEvent(event);
        //    }
        //};
       
    }
    
    public ArrayList<MetricEntry> getEntries(){
    	return this.entries;
    }
    
    public void animateSavedUpdated(){
    	for(int i : this.updated){
    		Log.d("updated", ""+i);
    	}
    }
    
    public List<Integer> getUpdated(){
    	return this.updated;
    }
    
    public void resetUpdated(){
    	this.updated.clear();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	// Get the data item for this position
    	MetricEntry metric = getItem(position);    
    	pos = position;
    	
   		TextView mName, mCount, mUnit, mDetails = null;
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
		   mDone.setTag(convertView);
		   // Set click listeners for interface components
		   mDone.setOnClickListener(new OnClickListener() {
			   private final TrackMainFragment eParent = parentFragment;
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
		   
		   mDetails = (TextView)convertView.findViewById(R.id.track_list_binary_item_details);
		   
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
					   //input.setText(""+ e.getCount());
                       input.setText((e.getCount() % 1.0 == 0.0) ? ""+ (int)e.getCount() : ""+ e.getCount());
					   input.setSelection(input.getText().length());
				   }
				   
				   dialog.setView(input);
				   
				   dialog.show();
			   }
	       });
		   mEdit.setTag(convertView);
		   
		   mDetails = (TextView)convertView.findViewById(R.id.track_list_count_item_details);
		   
		   if(updated.contains(pos)){
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_changed));
		   }else{
			   convertView.setBackgroundColor(getContext().getResources().getColor(R.color.tile_default));
		   }
	   }
	   
	   convertView.setOnTouchListener(gestureListener);
	   
	   /*
	    * Every metric has a details field
	    */
	   mDetails.setTag(convertView);
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

	   // Return the completed view to render on screen
       return convertView;
   }
    
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    //Toast.makeText(SelectFilterActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
                	parentFragment.dateDown();
//                	activeDate = DateUtil.getOffsetDate(activeDate, -1);
//            		activeDay = DateUtil.getFormattedDay(activeDate);
            		Log.d("swiped", "left");
//            		updateGrid();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    //Toast.makeText(SelectFilterActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                	parentFragment.dateUp();
//                	activeDate = DateUtil.getOffsetDate(activeDate, 1);
//            		activeDay = DateUtil.getFormattedDay(activeDate);
            		Log.d("swiped", "right");
//            		updateGrid();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

            @Override
        public boolean onDown(MotionEvent e) {
              return true;
        }
    }
}