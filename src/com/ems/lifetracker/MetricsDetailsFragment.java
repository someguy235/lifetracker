package com.ems.lifetracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
 
public class MetricsDetailsFragment extends Fragment implements OnClickListener{
	private Context ctx;
	private Bundle bundle;
	private FragmentManager fragmentManager;
	private GraphicalView mChartView;
	    
    public MetricsDetailsFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		
        ArrayList<MetricEntry> entries = (ArrayList<MetricEntry>)dm.getEntriesByName(metricName);
        
        EntriesListAdapter eAdapter = new EntriesListAdapter(ctx, entries);
        final ListView eventsListView = (ListView)rootView.findViewById(R.id.metrics_details_events);
        eventsListView.setAdapter(eAdapter);
        
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(32);
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsTextSize(30);
        renderer.setShowLegend(false);
        renderer.setPointSize(8f);
        renderer.setMargins(new int[] {40, 20, 40, 20});
        renderer.setAxesColor(Color.DKGRAY);
        renderer.setLabelsColor(Color.LTGRAY);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(1);
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        //renderer.setLegendTextSize(30);
        //renderer.setChartTitle(metric.getName());
        
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLUE);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setLineWidth(4f);
        r.setFillPoints(true);
        //r.setFillBelowLine(true);
        //r.setFillBelowLineColor(Color.WHITE);
        
        renderer.addSeriesRenderer(r);
        
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        TimeSeries series = new TimeSeries(metric.getUnit());
        for(MetricEntry e : entries){
        	series.add(DateUtil.dateFromString(e.getDate()), e.getCount());
        	if(e.getCount() >= renderer.getYAxisMax()){
        		renderer.setYAxisMax(e.getCount() + 1);
        	}
        }
        dataset.addSeries(series);
        
        mChartView = ChartFactory.getTimeChartView(ctx, dataset, renderer, "M/d");
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.metrics_details_chart);
        layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
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
