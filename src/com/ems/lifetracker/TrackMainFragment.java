package com.ems.lifetracker;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
 
public class TrackMainFragment extends Fragment implements OnClickListener {
	private Context ctx;

    public TrackMainFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_track_main, container, false);
        ctx = getActivity();
        
        DataManager dm = new DataManager(ctx);
        ArrayList<Metric> metrics = (ArrayList<Metric>)dm.getAllMetrics();
        
        //GridView gridview = (GridView) rootView.findViewById(R.id.track_main_gridview);
        ListView gridview = (ListView) rootView.findViewById(R.id.track_main_gridview);

        TrackListAdapter adapter = new TrackListAdapter(ctx, metrics);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(ctx, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        
        return rootView;
    }
    
    @Override
    public void onClick(View v) {
    	/*
        FragmentManager fragmentManager = getFragmentManager();

    	switch (v.getId()) {
        case R.id.metrics_main_button_new:
    		fragmentManager.beginTransaction()
        		.replace(R.id.main_container, new MetricsNewFragment())
        		.addToBackStack(null)
    			.commit();
    		break;
        }
        */
    }
}
