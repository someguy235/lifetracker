package com.ems.lifetracker;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class HistoryListAdapter extends ArrayAdapter<Metric> {
	private ArrayList<Metric> activeMetrics;
	private HashMap<String, Metric> allMetrics;
	private HistoryMainFragment parentFragment;
	
    public HistoryListAdapter(Context context, ArrayList<Metric> metrics, HistoryMainFragment fragment) {
       super(context, R.layout.history_list_item, metrics);
       
       this.parentFragment = (HistoryMainFragment)fragment;
       this.activeMetrics = new ArrayList<Metric>();
       this.allMetrics = new HashMap<String, Metric>();

       for(Metric m : metrics){
    	   activeMetrics.add(m);
    	   allMetrics.put(m.getName(), m);
       }
    }

    public ArrayList<Metric> getActiveMetrics(){
    	return this.activeMetrics;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	final Metric metric = getItem(position);    
//    	final HistoryMainFragment eParent = parent;
    	
    	convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_item, parent, false);

    	TextView mName = (TextView) convertView.findViewById(R.id.history_main_entry_name);
    	mName.setText(metric.getName());

    	ToggleButton toggleButton = (ToggleButton) convertView.findViewById(R.id.history_main_button_toggle);
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
        		eParent.updateChart();
        	}
        });
        
//        ToggleButton avgButton = (ToggleButton) convertView.findViewById(R.id.history_main_button_average);
//        avgButton.setOnClickListener(new OnClickListener(){
//        	@Override
//        	public void onClick(View v) {
//        	
//        	}
//        });
       
       
    	
    	// Return the completed view to render on screen
    	return convertView;
   }
}