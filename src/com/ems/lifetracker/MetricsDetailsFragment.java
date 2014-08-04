package com.ems.lifetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.LineChart;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
 
public class MetricsDetailsFragment extends Fragment implements OnClickListener{
	private Context ctx;
	private Bundle bundle;
	private FragmentManager fragmentManager;
	private GraphicalView mChartView;
	private double avg = 0.0;
	XYMultipleSeriesDataset dataset;
	XYMultipleSeriesRenderer renderer;
	XYSeriesRenderer ravg;
	XYSeries avgSeries;
	
    public MetricsDetailsFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	bundle = this.getArguments();
    	String metricName = bundle.getString("metricName");
		View rootView = inflater.inflate(R.layout.fragment_metrics_details, container, false);
        ctx = getActivity();
        DataManager dm = new DataManager(ctx);
        ArrayList<MetricEntry> entries = (ArrayList<MetricEntry>)dm.getEntriesByName(metricName);
        
        // Create header area
        Metric metric = dm.getMetricByName(metricName);
        ArrayList<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        MetricsListAdapter adapter = new MetricsListAdapter(ctx, metrics);
        final ListView listView = (ListView) rootView.findViewById(R.id.metrics_details_list);
		listView.setAdapter(adapter);
		
		if(entries.size() > 0){
			final TextView emptyMsg = (TextView) rootView.findViewById(R.id.metrics_details_empty_msg);
			emptyMsg.setVisibility(View.GONE);
			// Get chart container and add default data series
			dataset = new XYMultipleSeriesDataset();
	    	renderer = getMultipleSeriesRenderer();
	        
	    	// Set up the data renderer
	    	XYSeriesRenderer r = getSeriesRenderer();
	        renderer.addSeriesRenderer(r);
	        renderer.setXAxisMin(DateUtil.dateFromString(DateUtil.getOffsetDate(entries.get(0).getDate(), -1)).getTime() + 43200000);
	        renderer.setXAxisMax(DateUtil.dateFromString(DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), 1)).getTime() - 43200000);
	        
	        // Set up average renderer
	    	ravg = getSeriesRenderer();;
	    	ravg.setColor(ctx.getResources().getColor(R.color.lt_green));
	    	ravg.setFillPoints(false);
	    	renderer.addSeriesRenderer(ravg);
	    	
	        XYSeries series = new XYSeries(metric.getUnit());
	        avgSeries = new XYSeries("average");
	    	
	    	for(MetricEntry e : entries){
	    		avg += e.getCount();
	    	}
	    	avg = avg / entries.size();
	
	    	double ymin = entries.get(0).getCount();
	        double ymax = entries.get(0).getCount();
	    	for(int i=0; i<entries.size(); i++){
	    		MetricEntry e = entries.get(i);
	    		
	    		long t = DateUtil.dateFromString(e.getDate()).getTime();
	        	
	    		series.add(t, e.getCount());
	        	avgSeries.add(DateUtil.dateFromString(e.getDate()).getTime(), avg);
	        	
	        	if((entries.size() <= 4) || (i % (entries.size() / 4) == 1)){
	        		renderer.addXTextLabel(t, DateUtil.getFormattedDay(e.getDate()));
	        	}
	        	if(e.getCount() >= ymax) ymax = e.getCount();
	        	if(e.getCount() <= ymin) ymin = e.getCount();
	    	}
	        
	        dataset.addSeries(series);
	        dataset.addSeries(avgSeries);
	        
	        if(metric.getType().equals("count")){
		        renderer.setYAxisMin(ymin * 0.9);
		        renderer.setYAxisMax(ymax * 1.1);
		        mChartView = ChartFactory.getTimeChartView(ctx, dataset, renderer, "M/d");
	        }else if(metric.getType().equals("increment")){
		        renderer.setYAxisMin(ymin * 0.9);
		        renderer.setYAxisMax(ymax * 1.1);
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE } );
	        }else if(metric.getType().equals("binary")){
		        renderer.setYLabels(1);
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE } );
	        }
	
	        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.metrics_details_chart);
	        layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        
	        Collections.reverse(entries);
	        EntriesListAdapter eAdapter = new EntriesListAdapter(ctx, entries);
	        final ListView eventsListView = (ListView)rootView.findViewById(R.id.metrics_details_events);
	        eventsListView.setAdapter(eAdapter);
	        
	        ToggleButton tb = (ToggleButton) rootView.findViewById(R.id.metrics_details_button_average);
	        tb.setOnClickListener(this);
	        tb.setText("Avg: "+ new DecimalFormat("#.##").format(avg));
	        tb.setTextOn("Avg: "+ new DecimalFormat("#.##").format(avg));
	        tb.setTextOff("Avg Off");
		}else{
			final LinearLayout l = (LinearLayout) rootView.findViewById(R.id.metrics_details_content);
			l.setVisibility(View.GONE);
		}
		
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
    	case R.id.metrics_details_button_average:
    		ToggleButton tb = (ToggleButton) v;
    		if(tb.isChecked()){
    	    	renderer.addSeriesRenderer(ravg);
    	    	dataset.addSeries(avgSeries);
    	    	mChartView.repaint();
    		}else{
    	    	renderer.removeSeriesRenderer(ravg);
    	    	dataset.removeSeries(avgSeries);
    	    	mChartView.repaint();
    		}
    		break;
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
    
    private XYMultipleSeriesRenderer getMultipleSeriesRenderer(){
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
        renderer.setYLabelsPadding(10);
        renderer.setBarSpacing(0.25);
        renderer.setXLabels(0);
        
        //renderer.setLegendTextSize(30);
        //renderer.setChartTitle(metric.getName());
        return renderer;
    }
    private XYSeriesRenderer getSeriesRenderer(){
    	XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(ctx.getResources().getColor(R.color.lt_blue));
        r.setPointStyle(PointStyle.CIRCLE);
        r.setLineWidth(4f);
        r.setFillPoints(true);
        //r.setFillBelowLine(true);
        //r.setFillBelowLineColor(Color.WHITE);
        return r;
    }
}
