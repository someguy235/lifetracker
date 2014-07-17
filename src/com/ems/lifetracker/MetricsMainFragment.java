package com.ems.lifetracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
 
public class MetricsMainFragment extends Fragment implements OnClickListener{
     
    public MetricsMainFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_metrics_main, container, false);
        
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
