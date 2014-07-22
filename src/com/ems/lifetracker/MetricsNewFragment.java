package com.ems.lifetracker;

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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
 
public class MetricsNewFragment extends Fragment implements OnClickListener {
	View rootView;
	Context ctx;
	public MetricsNewFragment(){}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.fragment_metrics_new, container, false);
        ctx = getActivity();
        
        Button saveButton = (Button) rootView.findViewById(R.id.metrics_new_button_save);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) rootView.findViewById(R.id.metrics_new_button_cancel);
        cancelButton.setOnClickListener(this);
         
        return rootView;
    }
    
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();

    	switch (v.getId()) {
        case R.id.metrics_new_button_save:
        	//TODO: add some common sense checks to entered fields
        	EditText metricNameText = (EditText) rootView.findViewById(R.id.metrics_new_name);
        	String metricName = metricNameText.getText().toString();
        	
        	EditText metricDescText = (EditText) rootView.findViewById(R.id.metrics_new_desc);
        	String metricDesc = metricDescText.getText().toString();
        	
        	EditText metricUnitText = (EditText) rootView.findViewById(R.id.metrics_new_unit);
        	String metricUnit = metricUnitText.getText().toString();
        	
        	RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.metrics_new_radio_group);
        	int selectedType = radioGroup.getCheckedRadioButtonId();
        	View radioButton = radioGroup.findViewById(selectedType);
        	int typeIdx = radioGroup.indexOfChild(radioButton);
        	
        	EditText metricDfltText = (EditText) rootView.findViewById(R.id.metrics_new_dflt);
        	String metricDflt = metricUnitText.getText().toString();
        	
        	String metricType = null;
        	switch(typeIdx){
        	case 0:
        		metricType = "binary";
        		break;
        	case 1:
        		metricType = "count";
        		break;
        	case 2:
        		metricType = "increment";
        		break;
        	}
 
	        Metric metric = new Metric(metricName, metricDesc, metricUnit, metricType, metricDflt);
	        DataManager dm = new DataManager(ctx);
	         
	        if(dm.addMetric(metric)){
	        	fragmentManager.beginTransaction()
		    		.replace(R.id.main_container, new MetricsMainFragment())
		    		.addToBackStack(null)
        			.commit();
	        }else{
	        	Toast.makeText(ctx, "Something went wrong!", 
	        			Toast.LENGTH_LONG).show();
	        }
        	break;
	    case R.id.metrics_new_button_cancel:
			fragmentManager.beginTransaction()
	    		.replace(R.id.main_container, new MetricsMainFragment())
	    		.addToBackStack(null)
    			.commit();
			break;
    	}
    }

}
