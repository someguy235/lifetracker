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
import android.widget.Button;
import android.widget.ListView;
 
public class MetricsDetailsFragment extends Fragment implements OnClickListener{
	private Context ctx;
	
    public MetricsDetailsFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Bundle bundle = this.getArguments();
    	String metricName = bundle.getString("metricName");
		View rootView = inflater.inflate(R.layout.fragment_metrics_details, container, false);
        ctx = getActivity();
        
        DataManager dm = new DataManager(ctx);
        Metric metric = dm.getMetricByName(metricName);
        ArrayList<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        MetricsListAdapter adapter = new MetricsListAdapter(ctx, metrics);
		//Log.d("# metrics", String.valueOf(metrics.size()));
        
        final ListView listView = (ListView) rootView.findViewById(R.id.metrics_details_list);
		listView.setAdapter(adapter);
		
		/*
        Button b = (Button) rootView.findViewById(R.id.metrics_main_button_new);
        b.setOnClickListener(this);
        Button b = (Button) rootView.findViewById(R.id.metrics_main_button_new);
        b.setOnClickListener(this);
        Button b = (Button) rootView.findViewById(R.id.metrics_main_button_new);
        b.setOnClickListener(this);
        */
        return rootView;
    }
    
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();

    	switch (v.getId()) {
        case R.id.metrics_main_button_new:
        	Log.d("FRAGMENT: ", "metrics_new");
    		fragmentManager.beginTransaction()
        		.replace(R.id.main_container, new MetricsNewFragment())
        		.commit();
    		break;
        }
    }
    
}
