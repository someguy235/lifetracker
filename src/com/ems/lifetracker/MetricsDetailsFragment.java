package com.ems.lifetracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
 
public class MetricsDetailsFragment extends Fragment implements OnClickListener{
	private Context ctx;
	private Bundle bundle;
	private FragmentManager fragmentManager;
	
    public MetricsDetailsFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	bundle = this.getArguments();
    	String metricName = bundle.getString("metricName");
		View rootView = inflater.inflate(R.layout.fragment_metrics_details, container, false);
        ctx = getActivity();
        
        DataManager dm = new DataManager(ctx);
        Metric metric = dm.getMetricByName(metricName);
        ArrayList<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        MetricsListAdapter adapter = new MetricsListAdapter(ctx, metrics);
        
        final ListView listView = (ListView) rootView.findViewById(R.id.metrics_details_list);
		listView.setAdapter(adapter);
		
        Button b = (Button) rootView.findViewById(R.id.metrics_details_button_cancel);
        b.setOnClickListener(this);
        b = (Button) rootView.findViewById(R.id.metrics_details_button_edit);
        b.setOnClickListener(this);
        b = (Button) rootView.findViewById(R.id.metrics_details_button_delete);
        b.setOnClickListener(this);

        return rootView;
    }
    
    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();

    	switch (v.getId()) {
        case R.id.metrics_details_button_cancel:
    		fragmentManager.beginTransaction()
        		.replace(R.id.main_container, new MetricsMainFragment())
        		.addToBackStack(null)
    			.commit();
    		break;
        case R.id.metrics_details_button_edit:
        	Toast.makeText(ctx, "Edit button doesn't work yet.", 
        			Toast.LENGTH_LONG).show();
//    		fragmentManager.beginTransaction()
//        		.replace(R.id.main_container, new MetricsNewFragment())
//        		.addToBackStack(null)
//				.commit();
    		break;
        case R.id.metrics_details_button_delete:
        	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

    	    builder.setTitle("Confirm");
    	    builder.setMessage("Are you sure you want to delete this metric?");
    	    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) {
    	        	DataManager dm = new DataManager(ctx);
    	        	//Bundle bundle = this.getArguments();
    	        	String metricName = bundle.getString("metricName");
    	    		if(dm.deleteMetricByName(metricName)){
    		    		fragmentManager.beginTransaction()
    		        		.replace(R.id.main_container, new MetricsMainFragment())
    		        		.addToBackStack(null)
    		        		.commit();
    	    		}else{
    	    			Toast.makeText(ctx, "Something went wrong!", 
    		        			Toast.LENGTH_LONG).show();
    	    		}
    	        	dialog.dismiss();
    	        }

    	    });

    	    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
    	        @Override
    	        public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
    	    });

    	    AlertDialog alert = builder.create();
    	    alert.show();

        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
    		break;
        }
    }
    
}
