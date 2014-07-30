package com.ems.lifetracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
//import org.achartengine.chartdemo.demo.R;
//import org.achartengine.chartdemo.demo.R;
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
        
        //mChartView = ChartFactory.getLineChartView(ctx, dataset, renderer);
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
    
    
    
    //TODO: pull all this out to a ChartFragment class to extend
    /**
     * Builds an XY multiple series renderer.
     * 
     * @param colors the series rendering colors
     * @param styles the series point styles
     * @return the XY multiple series renderers
     */
//    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
//	  XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
//	  setRenderer(renderer, colors, styles);
//	  return renderer;
//    }
//    
//    protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
//	    renderer.setAxisTitleTextSize(16);
//	    renderer.setChartTitleTextSize(20);
//	    renderer.setLabelsTextSize(15);
//	    renderer.setLegendTextSize(15);
//	    renderer.setPointSize(5f);
//	    renderer.setMargins(new int[] { 20, 30, 15, 20 });
//	    int length = colors.length;
//	    for (int i = 0; i < length; i++) {
//	      XYSeriesRenderer r = new XYSeriesRenderer();
//	      r.setColor(colors[i]);
//	      r.setPointStyle(styles[i]);
//	      renderer.addSeriesRenderer(r);
//	    }
//    }
//
//    /**
//     * Sets a few of the series renderer settings.
//     * 
//     * @param renderer the renderer to set the properties to
//     * @param title the chart title
//     * @param xTitle the title for the X axis
//     * @param yTitle the title for the Y axis
//     * @param xMin the minimum value on the X axis
//     * @param xMax the maximum value on the X axis
//     * @param yMin the minimum value on the Y axis
//     * @param yMax the maximum value on the Y axis
//     * @param axesColor the axes color
//     * @param labelsColor the labels color
//     */
//    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
//        String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
//        int labelsColor) {
//      renderer.setChartTitle(title);
//      renderer.setXTitle(xTitle);
//      renderer.setYTitle(yTitle);
//      renderer.setXAxisMin(xMin);
//      renderer.setXAxisMax(xMax);
//      renderer.setYAxisMin(yMin);
//      renderer.setYAxisMax(yMax);
//      renderer.setAxesColor(axesColor);
//      renderer.setLabelsColor(labelsColor);
//    }
//
//    /**
//     * Builds an XY multiple dataset using the provided values.
//     * 
//     * @param titles the series titles
//     * @param xValues the values for the X axis
//     * @param yValues the values for the Y axis
//     * @return the XY multiple dataset
//     */
//    protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
//        List<double[]> yValues) {
//      XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//      addXYSeries(dataset, titles, xValues, yValues, 0);
//      return dataset;
//    }
//
//    public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
//	      List<double[]> yValues, int scale) {
//	    int length = titles.length;
//	    for (int i = 0; i < length; i++) {
//	      XYSeries series = new XYSeries(titles[i], scale);
//	      double[] xV = xValues.get(i);
//	      double[] yV = yValues.get(i);
//	      int seriesLength = xV.length;
//	      for (int k = 0; k < seriesLength; k++) {
//	        series.add(xV[k], yV[k]);
//	      }
//	      dataset.addSeries(series);
//	    }
//	  }
//
//    private XYMultipleSeriesRenderer getDemoRenderer() {
//        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
//        renderer.setAxisTitleTextSize(16);
//        renderer.setChartTitleTextSize(20);
//        renderer.setLabelsTextSize(15);
//        renderer.setLegendTextSize(15);
//        renderer.setPointSize(5f);
//        renderer.setMargins(new int[] {20, 30, 15, 0});
//        XYSeriesRenderer r = new XYSeriesRenderer();
//        r.setColor(Color.BLUE);
//        r.setPointStyle(PointStyle.SQUARE);
//        r.setFillBelowLine(true);
//        r.setFillBelowLineColor(Color.WHITE);
//        r.setFillPoints(true);
//        renderer.addSeriesRenderer(r);
////        r = new XYSeriesRenderer();
////        r.setPointStyle(PointStyle.CIRCLE);
////        r.setColor(Color.GREEN);
////        r.setFillPoints(true);
////        renderer.addSeriesRenderer(r);
//        renderer.setAxesColor(Color.DKGRAY);
//        renderer.setLabelsColor(Color.LTGRAY);
//        return renderer;
//      }
//    
//    private XYSeriesRenderer getDemoXYRenderer() {
////        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
////        renderer.setAxisTitleTextSize(16);
////        renderer.setChartTitleTextSize(20);
////        renderer.setLabelsTextSize(15);
////        renderer.setLegendTextSize(15);
////        renderer.setPointSize(5f);
////        renderer.setMargins(new int[] {20, 30, 15, 0});
//        XYSeriesRenderer r = new XYSeriesRenderer();
//        r.setColor(Color.BLUE);
//        r.setPointStyle(PointStyle.SQUARE);
//        r.setFillBelowLine(true);
//        r.setFillBelowLineColor(Color.WHITE);
//        r.setFillPoints(true);
////        renderer.addSeriesRenderer(r);
//        r = new XYSeriesRenderer();
//        r.setPointStyle(PointStyle.CIRCLE);
//        r.setColor(Color.GREEN);
//        r.setFillPoints(true);
////        renderer.addSeriesRenderer(r);
////        renderer.setAxesColor(Color.DKGRAY);
////        renderer.setLabelsColor(Color.LTGRAY);
////        return renderer;
//        return r;
//      }
    
    
}
