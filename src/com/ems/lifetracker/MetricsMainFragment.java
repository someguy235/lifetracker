package com.ems.lifetracker;

import java.util.ArrayList;
import java.util.List;

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
 
public class MetricsMainFragment extends Fragment implements OnClickListener{
	private Context ctx;
	
    public MetricsMainFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_metrics_main, container, false);
        ctx = getActivity();
        
        DataManager dm = new DataManager(ctx);
        ArrayList<Metric> metrics = (ArrayList<Metric>)dm.getAllMetrics();
        Log.d("# metrics", String.valueOf(metrics.size()));
        
        final ListView listView = (ListView) rootView.findViewById(R.id.metrics_main_list);
		MetricsListAdapter adapter = new MetricsListAdapter(ctx, metrics);
		listView.setAdapter(adapter);
		
        Button b = (Button) rootView.findViewById(R.id.metrics_main_button_new);
        b.setOnClickListener(this);
        
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
