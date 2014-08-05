package com.ems.lifetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
 
public class HistoryMainFragment extends Fragment {
	private Context ctx;
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private GraphicalView mChartView;
	private ArrayList<Metric> metrics;
	
    public HistoryMainFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_history_main, container, false);
        ctx = getActivity();
        DataManager dm = new DataManager(ctx);
        metrics = (ArrayList<Metric>)dm.getAllMetrics();
        String minDate = DateUtil.getFormattedDate(null);

        if(metrics.size() == 0){
        	
        }else{
        	String[] chartTypes = new String[metrics.size()];
            double ymin = Double.MAX_VALUE;
        	double ymax = 0.0;
	    	
//	        final TextView emptyMsg = (TextView) rootView.findViewById(R.id.metrics_details_empty_msg);
//			emptyMsg.setVisibility(View.GONE);

        	// Get chart container and add default data series
        	dataset = new XYMultipleSeriesDataset();
	    	renderer = getMultipleSeriesRenderer();
    		        
	    	
	        for(int m=0; m<metrics.size(); m++){
	        	// Set up the data renderer
		    	XYSeriesRenderer r = getSeriesRenderer();
//		        r.setColor(ctx.getResources().getColor(R.color.lt_blue));
		    	int[] colors = ctx.getResources().getIntArray(R.array.chart_colors);
		    	r.setColor(colors[m%10]);

		        renderer.addSeriesRenderer(r);
		        
		        Metric metric = metrics.get(m);
		        if(metric.getType().equals("binary")){
		        	chartTypes[m] = BarChart.TYPE;
		        }else{
		        	chartTypes[m] = LineChart.TYPE;
		        }
//	        	entryMap.put( metric.getName(), (ArrayList<MetricEntry>)dm.getEntriesByName(metric.getName()) );
	            ArrayList<MetricEntry> entries = (ArrayList<MetricEntry>)dm.getEntriesByName(metric.getName());

	        	
		        // Set up average renderer
//		    	ravg = getSeriesRenderer();;
//		    	ravg.setColor(ctx.getResources().getColor(R.color.lt_green));
//		    	ravg.setFillPoints(false);
//		    	renderer.addSeriesRenderer(ravg);
		    	
//		        XYSeries series = new XYSeries(metric.getUnit());
	            XYSeries series = new XYSeries(metric.getName());
//		        avgSeries = new XYSeries("average");
		    	
//		    	for(MetricEntry e : entries){
//		    		avg += e.getCount();
//		    	}
//		    	avg = avg / entries.size();
		
		    	for(int i=0; i<entries.size(); i++){
		    		MetricEntry e = entries.get(i);
		    		
		    		long t = DateUtil.dateFromString(e.getDate()).getTime();
		        	
		    		series.add(t, e.getCount());
//		        	avgSeries.add(DateUtil.dateFromString(e.getDate()).getTime(), avg);
		        	
		        	if((entries.size() <= 4) || (i % (entries.size() / 3) == 1)){
		        		renderer.addXTextLabel(t, DateUtil.getFormattedDay(e.getDate()));
		        	}
		        	if(e.getCount() >= ymax) ymax = e.getCount();
		        	if(e.getCount() <= ymin) ymin = e.getCount();
		        	if(e.getDate().compareTo(minDate) < 0) minDate = e.getDate();
		    	}
		        
		        dataset.addSeries(series);
//		        dataset.addSeries(avgSeries);

	        
	        
	        
	        
	        } // each metric
	        
	        renderer.setXAxisMin(DateUtil.dateFromString(DateUtil.getOffsetDate(minDate, -1)).getTime() + 43200000);
	        renderer.setXAxisMax(DateUtil.dateFromString(DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), 1)).getTime() - 43200000);
	        
	        renderer.setYAxisMin(ymin * 0.9);
	        renderer.setYAxisMax(ymax * 1.1);
	        
	        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, chartTypes );
	        
//	        if(metric.getType().equals("count")){
//		        renderer.setYAxisMin(ymin * 0.9);
//		        renderer.setYAxisMax(ymax * 1.1);
//		        mChartView = ChartFactory.getTimeChartView(ctx, dataset, renderer, "M/d");
//	        }else if(metric.getType().equals("increment")){
//		        renderer.setYAxisMin(ymin * 0.9);
//		        renderer.setYAxisMax(ymax * 1.1);
//		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
//		        		new String[] { BarChart.TYPE, LineChart.TYPE } );
//	        }else if(metric.getType().equals("binary")){
//	        	renderer.addYTextLabel(0, "no");
//	        	renderer.addYTextLabel(1, "yes");
//		        renderer.setYLabels(0);
//		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
//		        		new String[] { BarChart.TYPE, LineChart.TYPE } );
//	        }

	        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.history_main_chart);
	        layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        
//	        Collections.reverse(entries);
//	        EntriesListAdapter eAdapter = new EntriesListAdapter(ctx, entries);
//	        final ListView eventsListView = (ListView)rootView.findViewById(R.id.metrics_details_events);
//	        eventsListView.setAdapter(eAdapter);
	        
//	        ToggleButton tb = (ToggleButton) rootView.findViewById(R.id.metrics_details_button_average);
//	        tb.setOnClickListener(this);
//	        tb.setText("Avg: "+ new DecimalFormat("#.##").format(avg));
//	        tb.setTextOn("Avg: "+ new DecimalFormat("#.##").format(avg));
//	        tb.setTextOff("Avg Off");
	        
        }

        return rootView;
    }
    
    private XYMultipleSeriesRenderer getMultipleSeriesRenderer(){
    	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    	renderer.setAxesColor(Color.DKGRAY);
        renderer.setAxisTitleTextSize(32);
        renderer.setBarSpacing(0.25);
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsColor(Color.LTGRAY);
        renderer.setLabelsTextSize(30);
        renderer.setMargins(new int[] {40, 50, 50, 50});
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        renderer.setPointSize(8f);
//        renderer.setShowLegend(false);
        renderer.setLegendTextSize(30);
//        renderer.setL
        renderer.setXLabels(0);
        renderer.setYAxisMax(1);
        renderer.setYAxisMin(0);
        renderer.setYLabelsAlign(Align.RIGHT);
//        renderer.setYLabelsPadding(10);
        
        //renderer.setLegendTextSize(30);
        //renderer.setChartTitle(metric.getName());
        return renderer;
    }
    private XYSeriesRenderer getSeriesRenderer(){
    	XYSeriesRenderer r = new XYSeriesRenderer();
        //r.setColor(ctx.getResources().getColor(R.color.lt_blue));
        r.setPointStyle(PointStyle.CIRCLE);
        r.setLineWidth(4f);
        r.setFillPoints(true);
        //r.setFillBelowLine(true);
        //r.setFillBelowLineColor(Color.WHITE);
        return r;
    }
}
