package com.ems.lifetracker.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ems.lifetracker.domain.*;
import com.ems.lifetracker.fragment.HistoryMainFragment;
import com.ems.lifetracker.R;

public class HistoryListAdapter extends ArrayAdapter<Metric> {
	private ArrayList<Metric> activeMetrics;
	private ArrayList<Metric> activeAverages;
	private HashMap<String, Metric> allMetrics;
	private HistoryMainFragment parentFragment;
	
    public HistoryListAdapter(Context context, ArrayList<Metric> metrics, HistoryMainFragment fragment) {
        super(context, R.layout.history_list_item, metrics);
       
        this.parentFragment = (HistoryMainFragment)fragment;
        this.activeMetrics = new ArrayList<Metric>();
        this.activeAverages = new ArrayList<Metric>();
        this.allMetrics = new HashMap<String, Metric>();

        for(Metric m : metrics){
            if(!m.getName().equals("All")){
                allMetrics.put(m.getName(), m);
                if(m.getArch() == null) {
                    activeMetrics.add(m);
                    //activeAverages.add(m);
                }
            }
        }
    }

    public ArrayList<Metric> getActiveMetrics(){
    	return this.activeMetrics;
    }
    
    public ArrayList<Metric> getActiveAverages(){
    	return this.activeAverages;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	final Metric metric = getItem(position);    
    	
    	convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_item, parent, false);

    	TextView mName = (TextView) convertView.findViewById(R.id.history_main_entry_name);
    	mName.setText(metric.getName());

    	ToggleButton toggleButton = (ToggleButton) convertView.findViewById(R.id.history_main_button_toggle);
    	
    	toggleButton.setTextOn("On");
    	toggleButton.setTextOff("Off");
    	if(activeMetrics.contains(metric)){
    		toggleButton.setChecked(true);
    	}else{
    		toggleButton.setChecked(false);
    	}

    	toggleButton.setOnClickListener(new OnClickListener(){
        	private final HistoryMainFragment eParent = parentFragment;
        	@Override
        	public void onClick(View v) {
        		ToggleButton tb = (ToggleButton) v;
        		if(tb.isChecked()){
        			activeMetrics.add(metric);
        		}else{
        			activeMetrics.remove(metric);
        		}
        		eParent.updateDates();
        		eParent.updateChart();
        	}
        });
        
        ToggleButton avgButton = (ToggleButton) convertView.findViewById(R.id.history_main_button_average);
        
        //avgButton.setTextOn("Avg: "+ parentFragment.getAverage(metric.getName()));
        //    	avgButton.setTextOff("Avg Off");
    	if(activeAverages.contains(metric)){
    		avgButton.setChecked(true);
    		avgButton.setText("Avg: "+ parentFragment.getAverage(metric.getName()));
    	}else{
    		avgButton.setChecked(false);
    		avgButton.setText("Avg Off");
    	}
//		avgButton.setChecked(false);
//		avgButton.setText("Avg Off");
//		activeAverages.remove(metric);
    	
        avgButton.setOnClickListener(new OnClickListener(){
    		private final HistoryMainFragment eParent = parentFragment;
        	@Override
        	public void onClick(View v) {
        		ToggleButton tb = (ToggleButton) v;
        		if(tb.isChecked()){
        			activeAverages.add(metric);
        			tb.setText("Avg: "+ parentFragment.getAverage(metric.getName()));
        		}else{
        			activeAverages.remove(metric);
        			tb.setText("Avg Off");
        		}
        		eParent.updateDates();
        		eParent.updateChart();
        	}
        });
    	 
    	// Return the completed view to render on screen
    	return convertView;
   }
}