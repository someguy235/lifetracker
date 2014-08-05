package com.ems.lifetracker;

import java.util.ArrayList;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class TrackMainFragment extends Fragment {
	private Context ctx;
	private DataManager dm;
	private TrackListAdapter entriesAdapter;
	private String activeDate, activeDay;
	private View rootView;
	private TextView datePicker;

    public TrackMainFragment(){}
     
    private void updateGrid(){
    	// Load today's entries into Adapter
        ArrayList<MetricEntry> entries = (ArrayList<MetricEntry>)dm.getEntriesByDate(activeDate);
        GridView gridview = (GridView) rootView.findViewById(R.id.track_main_gridview);
        entriesAdapter = new TrackListAdapter(ctx, entries);
        gridview.setAdapter(entriesAdapter);
        if(activeDate.equals(DateUtil.getFormattedDate(null))){
        	datePicker.setText("Today");
        }else if(activeDate.equals(DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), -1))){
        	datePicker.setText("Yesterday");
        }else{
        	datePicker.setText(activeDay);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.fragment_track_main, container, false);
        ctx = getActivity();
        activeDate = DateUtil.getFormattedDate(null);
        activeDay = DateUtil.getFormattedDay(activeDate);

        dm = new DataManager(ctx);

        // Set up the date picker and buttons
        datePicker = (TextView) rootView.findViewById(R.id.track_list_date);
        datePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        ImageView dateDown = (ImageView) rootView.findViewById(R.id.track_list_date_down);
        dateDown.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		activeDate = DateUtil.getOffsetDate(activeDate, -1);
        		activeDay = DateUtil.getFormattedDay(activeDate);
        		updateGrid();
        	}
        });
        
        ImageView dateUp = (ImageView) rootView.findViewById(R.id.track_list_date_up);
        dateUp.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		activeDate = DateUtil.getOffsetDate(activeDate, 1);
        		activeDay = DateUtil.getFormattedDay(activeDate);
        		updateGrid();
        	}
        });
        
        // The save button action
        Button saveButton = (Button) rootView.findViewById(R.id.track_list_button_save);
        saveButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		ArrayList<MetricEntry> entries = entriesAdapter.getEntries();
        		dm.saveEntries(entries, activeDate);
        		GridView gv = (GridView)rootView.findViewById(R.id.track_main_gridview);
        		for(int i=0; i<gv.getChildCount(); i++){
        			gv.getChildAt(i).setBackgroundColor(ctx.getResources().getColor(R.color.tile_default));
        			entriesAdapter.resetUpdated();
        		}
        		Toast.makeText(ctx, "Saved", Toast.LENGTH_LONG).show();
        	}
        });
        
        updateGrid();

        return rootView;
    }
}
