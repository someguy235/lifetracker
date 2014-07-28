package com.ems.lifetracker;

import java.util.ArrayList;

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
import android.widget.GridView;
 
public class TrackMainFragment extends Fragment {
	private Context ctx;
	private DataManager dm;
	//private ArrayList<MetricEntry> entries;
	private TrackListAdapter adapter;
	
    public TrackMainFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_track_main, container, false);
        ctx = getActivity();
        
        dm = new DataManager(ctx);
        //ArrayList<Metric> metrics = (ArrayList<Metric>)dm.getAllMetrics();
        ArrayList<MetricEntry> entries = (ArrayList<MetricEntry>)dm.getEntriesByDate(DateUtil.getFormattedDate("today"));
        Log.d("ENTRIES", entries.toString());
        GridView gridview = (GridView) rootView.findViewById(R.id.track_main_gridview);

        adapter = new TrackListAdapter(ctx, entries);
        gridview.setAdapter(adapter);

        Button saveButton = (Button) rootView.findViewById(R.id.track_list_button_save);
        saveButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		ArrayList<MetricEntry> entries = adapter.getEntries();
        		//TODO: get real date
        		dm.saveEntries(entries, DateUtil.getFormattedDate("today"));
        	}
        });
    
        return rootView;
    }
}
