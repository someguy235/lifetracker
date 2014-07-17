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
	public MetricsNewFragment(){}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.fragment_metrics_new, container, false);
        
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
        	Log.d("FRAGMENT: ", "metrics_save");
        	
        	EditText metricNameText = (EditText) rootView.findViewById(R.id.metrics_new_name);
        	String metricName = metricNameText.getText().toString();
        	
        	EditText metricDescText = (EditText) rootView.findViewById(R.id.metrics_new_desc);
        	String metricDesc = metricDescText.getText().toString();
        	
        	EditText metricUnitText = (EditText) rootView.findViewById(R.id.metrics_new_unit);
        	String metricUnit = metricUnitText.getText().toString();
        	
        	RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.metrics_new_radio_group);
        	int selectedType = radioGroup.getCheckedRadioButtonId();
	        RadioButton radioTypeButton = (RadioButton) rootView.findViewById(selectedType);
	        String metricType = radioTypeButton.getText().toString();

	        Metric metric = new Metric(metricName, metricDesc, metricUnit, metricType);
	        Log.d("METRIC: ", metric.toString());
	        //DataManager dm = new DataManager();
	         
//	        if(dm.addMetric(metric)){
//	        	fragmentManager.beginTransaction()
//		    		.replace(R.id.main_container, new MetricsMainFragment())
//		    		.commit();
//	        }else{
//	        	Toast.makeText(getApplicationContext(), "Something went wrong!", 
//	        			Toast.LENGTH_LONG).show();
//	        }
        	break;
	    case R.id.metrics_new_button_cancel:
	    	Log.d("FRAGMENT: ", "metrics_cancel");
			fragmentManager.beginTransaction()
	    		.replace(R.id.main_container, new MetricsMainFragment())
	    		.commit();
			break;
    	}
    }

}
