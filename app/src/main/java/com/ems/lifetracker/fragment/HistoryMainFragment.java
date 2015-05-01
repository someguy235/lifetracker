package com.ems.lifetracker.fragment;

import com.ems.lifetracker.domain.*;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ems.lifetracker.util.DataManager;
import com.ems.lifetracker.util.DateUtil;
import com.ems.lifetracker.MainActivity;
import com.ems.lifetracker.R;
import com.ems.lifetracker.adapter.HistoryListAdapter;
import com.ems.lifetracker.domain.MetricEntry;

public class HistoryMainFragment extends Fragment {
	private View rootView;
	private Context ctx;
	private DataManager dm;
	private ArrayList<Metric> allMetrics;
	private HashSet<String> xAxisDates;
	private LinearLayout layout;
	private List<String> xAxisDateList;
	private String minDate;
	private HistoryListAdapter listAdapter;
	private HashMap<String, Double> averages;
	private HashMap<String, XYSeries> metricSeries;
	private HashMap<String, XYSeries> averageSeries;
	private HashMap<String, double[]> metricMinMax;
	private HashMap<String, List<String>> metricDatesMap;
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private GraphicalView mChartView;
	private String timeframe = "all";
	
    public HistoryMainFragment(){}
    
    public String getAverage(String metricName){
    	return new DecimalFormat("#.##").format(averages.get(metricName));
    }
    
    public void setTimeFrame(String timeframe){
    	this.timeframe = timeframe;
    }
    
    public void updateChart(){
    	ArrayList<Metric> activeMetrics = listAdapter.getActiveMetrics();
    	ArrayList<Metric> activeAverages = listAdapter.getActiveAverages();
    	
    	final TextView t = (TextView) rootView.findViewById(R.id.history_main_empty_msg);
    	final LinearLayout l = (LinearLayout) rootView.findViewById(R.id.history_main_chart);
    	
        if(activeMetrics.size() == 0 && activeAverages.size() == 0){
        	l.setVisibility(View.GONE);
        	t.setVisibility(View.VISIBLE);
        }else{
			t.setVisibility(View.GONE);
			l.setVisibility(View.VISIBLE);

			int[] colors = ctx.getResources().getIntArray(R.array.chart_colors);
        	String[] chartTypes = new String[activeMetrics.size() + activeAverages.size()];
        	double ymin = Double.MAX_VALUE, ymax = 0.0;
        	
        	// Get chart container and add default data series
        	dataset = new XYMultipleSeriesDataset();
	    	renderer = getMultipleSeriesRenderer();
    		int index = 0;
	        for(int m=0; m<allMetrics.size(); m++){
		        Metric metric = allMetrics.get(m);

		        if(activeMetrics.contains(metric)){
		        	XYSeriesRenderer r = getSeriesRenderer();
			    	r.setColor(colors[m % colors.length]);
			        renderer.addSeriesRenderer(r);
			        
			        if(metric.getType().equals("binary")){
			        	chartTypes[index++] = BarChart.TYPE;
			        }else{
			        	chartTypes[index++] = LineChart.TYPE;
			        }

			        if(metricMinMax.get(metric.getName())[0] < ymin)
			        	ymin = metricMinMax.get(metric.getName())[0];
			        if(metricMinMax.get(metric.getName())[1] > ymax)
			        	ymax = metricMinMax.get(metric.getName())[1];

			        dataset.addSeries(metricSeries.get(metric.getName()));
		        }
	        	
	        	if(activeAverages.contains(allMetrics.get(m))){
			    	XYSeriesRenderer ravg = getSeriesRenderer();
			    	ravg.setColor(colors[m % colors.length]);
			        renderer.addSeriesRenderer(ravg);
			        chartTypes[index++] = LineChart.TYPE;
			        
			        if(averages.get(metric.getName()) < ymin)
			        	ymin = averages.get(metric.getName());
			        if(averages.get(metric.getName()) > ymax)
			        	ymax = averages.get(metric.getName());
			        
			        dataset.addSeries(averageSeries.get(metric.getName()));
	        	}
	            
	        } // each metric
	        
	        for(int i=0; i<xAxisDateList.size(); i++){
	        	String xAxisDate = xAxisDateList.get(i);
        		if((xAxisDateList.size() <= 4) || (i % (xAxisDateList.size() / 3) == 1)){
	        		renderer.addXTextLabel(
	        				DateUtil.dateFromString(xAxisDate).getTime(),
	        				DateUtil.getFormattedDay(xAxisDate)
        				);
	        	}
	        }
	        
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
    
    public void updateEntries(){
    	metricSeries = new HashMap<String, XYSeries>();
    	averageSeries = new HashMap<String, XYSeries>();
//    	xAxisDates = new HashSet<String>();
    	XYSeries series = null, avgseries = null;
    	metricMinMax = new HashMap<String, double[]>();
    	metricDatesMap = new HashMap<String, List<String>>();
    	
//    	minDate = DateUtil.getFormattedDate(null);
    	averages = new HashMap<String, Double>();
    	
    	for(int m=0; m<allMetrics.size(); m++){
        	Metric metric = allMetrics.get(m);
        	series = new XYSeries(metric.getName() +"  ");
	    	avgseries = new XYSeries(metric.getName() + " avg  ");
	    	
	    	ArrayList<MetricEntry> entries = (ArrayList<MetricEntry>)dm.getEntriesByNameAndTimeframe(metric.getName(), timeframe);
	    
            double avg = 0.0, ymin = Double.MAX_VALUE, ymax = 0.0;
	        for(MetricEntry e : entries){
	    		avg += e.getCount();
	    		if(e.getCount() > ymax) ymax = e.getCount();
	        	if(e.getCount() < ymin) ymin = e.getCount();
	    	}
	    	avg = avg / entries.size();
	    	averages.put(metric.getName(), avg);
	    	metricMinMax.put(metric.getName(), new double[]{ymin, ymax});
	    	
	    	List<String> metricDates = new ArrayList<String>();
	    	for(MetricEntry e : entries){
	    		long xAxisDate = DateUtil.dateFromString(e.getDate()).getTime();
    			series.add(xAxisDate, e.getCount());
	        	avgseries.add(xAxisDate, avg);
    			//xAxisDates.add(e.getDate());
    			//if(e.getDate().compareTo(minDate) < 0) minDate = e.getDate();
	        	metricDates.add(e.getDate());
	    	}     

	    	metricDatesMap.put(metric.getName(), metricDates);
	    	
	    	metricSeries.put(metric.getName(), series);
	    	averageSeries.put(metric.getName(), avgseries);
        }
//    	xAxisDateList = new ArrayList<String>(xAxisDates);
//        Collections.sort(xAxisDateList);
    }
    
    public void updateDates(){
    	xAxisDates = new HashSet<String>();
    	minDate = DateUtil.getFormattedDate(null);
    	List<String> mDates;
    	
    	ArrayList<Metric> activeMetrics = listAdapter.getActiveMetrics();
    	for(Metric m : activeMetrics){
    		mDates = metricDatesMap.get(m.getName());
    		for(String d : mDates){
    			if(d.compareTo(minDate) < 0) minDate = d;
    			xAxisDates.add(d);
    		}
    	}
    	
    	ArrayList<Metric> activeAverages = listAdapter.getActiveAverages();
    	for(Metric m : activeAverages){
    		mDates = metricDatesMap.get(m.getName());
    		for(String d : mDates){
    			if(d.compareTo(minDate) < 0) minDate = d;
    			xAxisDates.add(d);
    		}
    	}
    	
    	if(timeframe.equals("week")){
    		minDate = DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), -6);
    	}else if(timeframe.equals("month")){
    		minDate = DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), -29);
    	}
    	
    	xAxisDateList = new ArrayList<String>(xAxisDates);
        Collections.sort(xAxisDateList);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = inflater.inflate(R.layout.fragment_history_main, container, false);
    	layout = (LinearLayout) rootView.findViewById(R.id.history_main_chart);

    	ctx = getActivity();
    	((MainActivity)ctx).showActionBarMenu(true);
    	((MainActivity)ctx).setVisibleChart("history");
    	dm = new DataManager(ctx);
        allMetrics = (ArrayList<Metric>)dm.getAllNonEmptyMetrics();
    	if(allMetrics.size() > 1)
    		allMetrics.add(new Metric("All", null, null, null, 0.0));
    	
    	ListView listview = (ListView) rootView.findViewById(R.id.history_main_events);
        listAdapter = new HistoryListAdapter(ctx, allMetrics, HistoryMainFragment.this);
        listview.setAdapter(listAdapter);

        if(allMetrics.size() > 2)
        	allMetrics.remove(allMetrics.size()-1);
        
        updateEntries();

        updateDates();
        
        updateChart();
        
        return rootView;
    }
    
    private XYMultipleSeriesRenderer getMultipleSeriesRenderer(){
    	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    	renderer.setAxesColor(Color.DKGRAY);
        renderer.setBarSpacing(0.25);
        renderer.setFitLegend(true);
        renderer.setLabelsColor(Color.LTGRAY);
        renderer.setLabelsTextSize(30);
        renderer.setLegendTextSize(30);
//        renderer.setMargins(new int[] {20, 30, renderer.getLegendHeight() + 50, 20});
        renderer.setMargins(new int[] {20, 0, renderer.getLegendHeight() + 50, 0});
        renderer.setMarginsColor(ctx.getResources().getColor(R.color.default_background));
        renderer.setPointSize(8f);
        renderer.setShowCustomTextGrid(true);
        renderer.setXLabels(0);
        renderer.setYAxisMax(1);
        renderer.setYAxisMin(0);
        renderer.setYLabelsAlign(Align.LEFT);
        
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
