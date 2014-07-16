package com.ems.lifetracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 
public class MetricsNewFragment extends Fragment {
     
    public MetricsNewFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_metrics_new, container, false);
          
        return rootView;
    }
}
