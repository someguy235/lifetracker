package com.ems.lifetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
 
public class HistoryMainFragment extends Fragment {
	private View rootView;
	private Context ctx;
	private DataManager dm;
	private ArrayList<Metric> allMetrics;
//	private ArrayList<Metric> metrics;
	private LinearLayout layout;
	private HistoryListAdapter listAdapter;
	private HashMap<String, Double> averages;
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private GraphicalView mChartView;
	
    public HistoryMainFragment(){}
     
    public String getAverage(String metricName){
    	return new DecimalFormat("#.##").format(averages.get(metricName));
    }
    
    public void updateChart(){
    	String minDate = DateUtil.getFormattedDate(null);
    	ArrayList<Metric> activeMetrics = listAdapter.getActiveMetrics();
    	ArrayList<Metric> activeAverages = listAdapter.getActiveAverages();
    	
        if(activeMetrics.size() == 0 && activeAverages.size() == 0){
        	//TODO: show no metrics message
        }else{
        	//TODO: hide no metrics message
	    	int[] colors = ctx.getResources().getIntArray(R.array.chart_colors);
        	String[] chartTypes = new String[activeMetrics.size() + activeAverages.size()];
            double ymin = Double.MAX_VALUE;
        	double ymax = 0.0;
	    	
        	// Get chart container and add default data series
        	dataset = new XYMultipleSeriesDataset();
	    	renderer = getMultipleSeriesRenderer();
    		int index = 0;
	        for(int m=0; m<allMetrics.size(); m++){
		        Metric metric = allMetrics.get(m);
		        ArrayList<MetricEntry> entries = (ArrayList<MetricEntry>)dm.getEntriesByName(metric.getName());
		        double avg = 0.0;
		        XYSeries series = null, avgseries = null;
		        
	        	if(activeMetrics.contains(allMetrics.get(m))){
		        	XYSeriesRenderer r = getSeriesRenderer();
			    	r.setColor(colors[m%10]);
			        renderer.addSeriesRenderer(r);
			        
			        if(metric.getType().equals("binary")){
			        	chartTypes[index++] = BarChart.TYPE;
			        }else{
			        	chartTypes[index++] = LineChart.TYPE;
			        }
			        
			        series = new XYSeries(metric.getName() +"  ");
	        	}
	        	
	        	if(activeAverages.contains(allMetrics.get(m))){
			    	XYSeriesRenderer ravg = getSeriesRenderer();
			    	ravg.setColor(colors[m%10]);
			        renderer.addSeriesRenderer(ravg);
			        chartTypes[index++] = LineChart.TYPE;
			        
			        for(MetricEntry e : entries){
			    		avg += e.getCount();
			    	}
			    	avg = avg / entries.size();
			    	averages.put(metric.getName(), avg);
			    	avgseries = new XYSeries(metric.getName() + " avg ");
	        	}
	            
		    	for(int i=0; i<entries.size(); i++){
		    		MetricEntry e = entries.get(i);
		    		
		    		long t = DateUtil.dateFromString(e.getDate()).getTime();
		        	
		    		if(activeMetrics.contains(allMetrics.get(m)))
		    			series.add(t, e.getCount());
		    		if(activeAverages.contains(allMetrics.get(m)))
		    			avgseries.add(t, avg);
		        	
		        	if((entries.size() <= 4) || (i % (entries.size() / 3) == 1)){
		        		renderer.addXTextLabel(t, DateUtil.getFormattedDay(e.getDate()));
		        	}
		        	if(e.getCount() >= ymax) ymax = e.getCount();
		        	if(e.getCount() <= ymin) ymin = e.getCount();
		        	if(e.getDate().compareTo(minDate) < 0) minDate = e.getDate();
		    	}
		        
		    	if(activeMetrics.contains(allMetrics.get(m)))
		    		dataset.addSeries(series);
		    	if(activeAverages.contains(allMetrics.get(m)))
		    		dataset.addSeries(avgseries);
	        
	        } // each metric

	        renderer.setXAxisMin(DateUtil.dateFromString(DateUtil.getOffsetDate(minDate, -1)).getTime() + 43200000);
	        renderer.setXAxisMax(DateUtil.dateFromString(DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), 1)).getTime() - 43200000);
	        
	        renderer.setYAxisMin(ymin * 0.9);
	        renderer.setYAxisMax(ymax * 1.1);
	        
	        renderer.setPanLimits(new double[]{renderer.getXAxisMin(), renderer.getXAxisMax(), renderer.getYAxisMin(), renderer.getYAxisMax()});
	        
	        layout.removeAllViews();
	        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, chartTypes );
	        layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = inflater.inflate(R.layout.fragment_history_main, container, false);
        ctx = getActivity();
        dm = new DataManager(ctx);
        allMetrics = (ArrayList<Metric>)dm.getAllMetrics();
        layout = (LinearLayout) rootView.findViewById(R.id.history_main_chart);
        averages = new HashMap<String, Double>();
        
        ListView listview = (ListView) rootView.findViewById(R.id.history_main_events);
        listAdapter = new HistoryListAdapter(ctx, allMetrics, HistoryMainFragment.this);
        listview.setAdapter(listAdapter);

        updateChart();
        
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
        renderer.setLegendTextSize(30);
        renderer.setMargins(new int[] {40, 50, 50, 50});
        renderer.setPointSize(8f);
        renderer.setShowCustomTextGrid(true);
        renderer.setXLabels(0);
        renderer.setYAxisMax(1);
        renderer.setYAxisMin(0);
        renderer.setYLabelsAlign(Align.RIGHT);
        return renderer;
    }
    private XYSeriesRenderer getSeriesRenderer(){
    	XYSeriesRenderer r = new XYSeriesRenderer();
        r.setPointStyle(PointStyle.CIRCLE);
        r.setLineWidth(4f);
        r.setFillPoints(true);
        return r;
    }
}
