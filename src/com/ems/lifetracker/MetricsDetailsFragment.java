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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
 
public class MetricsDetailsFragment extends Fragment implements OnClickListener{
	private Metric metric;
	private Context ctx;
	private Bundle bundle;
	private FragmentManager fragmentManager;
	private GraphicalView mChartView;
	private ArrayList<MetricEntry> entries;
	private double avg = 0.0;
	XYMultipleSeriesDataset dataset;
	XYMultipleSeriesRenderer renderer;
	XYSeriesRenderer ravg, rtrend;
	XYSeries avgSeries, trendSeries;
	View rootView;
	LinearLayout defaultButtons;
	LinearLayout editButtons;
	LinearLayout buttonContainer;
	LinearLayout contentContainer;
	RelativeLayout editContainer;
	TextView emptyMsg;
	
    public MetricsDetailsFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	bundle = this.getArguments();
    	String metricName = bundle.getString("metricName");
		rootView = inflater.inflate(R.layout.fragment_metrics_details, container, false);
        ctx = getActivity();
        DataManager dm = new DataManager(ctx);
        entries = (ArrayList<MetricEntry>)dm.getEntriesByName(metricName);
        
        defaultButtons = (LinearLayout) rootView.findViewById(R.id.metrics_details_layout_defaultbuttons);
    	editButtons = (LinearLayout) rootView.findViewById(R.id.metrics_details_layout_editbuttons);
    	buttonContainer = (LinearLayout) rootView.findViewById(R.id.metrics_details_layout_buttonscontainer);
    	contentContainer = (LinearLayout) rootView.findViewById(R.id.metrics_details_content);
    	editContainer = (RelativeLayout) rootView.findViewById(R.id.metrics_details_edit_container);
    	emptyMsg = (TextView) rootView.findViewById(R.id.metrics_details_empty_msg);
		
        // Create header area
        metric = dm.getMetricByName(metricName);
        ArrayList<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        MetricsListAdapter adapter = new MetricsListAdapter(ctx, metrics);
        final ListView listView = (ListView) rootView.findViewById(R.id.metrics_details_list);
		listView.setAdapter(adapter);
		
		if(entries.size() > 0){
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
	    	ravg = getSeriesRenderer();
	    	ravg.setColor(ctx.getResources().getColor(R.color.lt_green));
	    	ravg.setFillPoints(false);
	    	renderer.addSeriesRenderer(ravg);
	    	
	    	// Set up trend renderer
	    	rtrend = getSeriesRenderer();
	    	rtrend.setColor(ctx.getResources().getColor(R.color.lt_yellow));
	    	rtrend.setFillPoints(false);
	    	renderer.addSeriesRenderer(rtrend);
	    	
	        XYSeries series = new XYSeries(metric.getUnit());
	        avgSeries = new XYSeries("average");
	        trendSeries = new XYSeries("trend");
	    	
	    	for(MetricEntry e : entries){
	    		avg += e.getCount();
	    	}
	    	avg = avg / entries.size();
	
	    	double ymin = entries.get(0).getCount();
	        double ymax = entries.get(0).getCount();
	        double trend = 0.0;
	    	for(int i=0; i<entries.size(); i++){
	    		MetricEntry e = entries.get(i);
	    		
	    		long t = DateUtil.dateFromString(e.getDate()).getTime();
	        	trend += e.getCount();
	        	
	    		series.add(t, e.getCount());
	        	avgSeries.add(t, avg);
	        	trendSeries.add(t, trend/(i+1));
	        	
	        	if((entries.size() <= 4) || (i % (entries.size() / 3) == 1)){
	        		renderer.addXTextLabel(t, DateUtil.getFormattedDay(e.getDate()));
	        	}
	        	if(e.getCount() >= ymax) ymax = e.getCount();
	        	if(e.getCount() <= ymin) ymin = e.getCount();
	    	}
	        
	        dataset.addSeries(series);
	        dataset.addSeries(avgSeries);
	        dataset.addSeries(trendSeries);
	        
	        if(metric.getType().equals("count")){
		        renderer.setYAxisMin(ymin * 0.9);
		        renderer.setYAxisMax(ymax * 1.1);
//		        mChartView = ChartFactory.getTimeChartView(ctx, dataset, renderer, "M/d");
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE, LineChart.TYPE } );
	        }else if(metric.getType().equals("increment")){
		        renderer.setYAxisMin(ymin * 0.9);
		        renderer.setYAxisMax(ymax * 1.1);
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE, LineChart.TYPE } );
	        }else if(metric.getType().equals("binary")){
	        	renderer.addYTextLabel(0, "no");
	        	renderer.addYTextLabel(1, "yes");
		        renderer.setYLabels(0);
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE, LineChart.TYPE } );
	        }
	
	        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.metrics_details_chart);
	        layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        
	        Collections.reverse(entries);
	        EntriesListAdapter eAdapter = new EntriesListAdapter(ctx, entries);
	        final ListView eventsListView = (ListView)rootView.findViewById(R.id.metrics_details_events);
	        eventsListView.setAdapter(eAdapter);
	        
	        ToggleButton avgButton = (ToggleButton) rootView.findViewById(R.id.metrics_details_button_average);
	        avgButton.setOnClickListener(this);
	        avgButton.setText("Avg: "+ new DecimalFormat("#.##").format(avg));
	        avgButton.setTextOn("Avg: "+ new DecimalFormat("#.##").format(avg));
	        avgButton.setTextOff("Avg");
	        
	        ToggleButton trendButton = (ToggleButton) rootView.findViewById(R.id.metrics_details_button_trend);
	        trendButton.setOnClickListener(this);
	        trendButton.setText("Trend");
	        trendButton.setTextOn("Trend");
	        trendButton.setTextOff("Trend");
		}else{
			contentContainer.setVisibility(View.GONE);
		}
		
		Button b = (Button) rootView.findViewById(R.id.metrics_details_button_cancel);
        b.setOnClickListener(this);
        b = (Button) rootView.findViewById(R.id.metrics_details_button_edit);
        b.setOnClickListener(this);
        b = (Button) rootView.findViewById(R.id.metrics_details_button_delete);
        b.setOnClickListener(this);
        b = (Button) rootView.findViewById(R.id.metrics_details_button_save);
        b.setOnClickListener(this);
        b = (Button) rootView.findViewById(R.id.metrics_details_button_editcancel);
        b.setOnClickListener(this);
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.metrics_edit_radio_group);        
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            	if(checkedId == R.id.metrics_edit_radio_binary){
            		rootView.findViewById(R.id.metrics_edit_dflt_text).setVisibility(LinearLayout.GONE);
            		rootView.findViewById(R.id.metrics_edit_binary_default_radio_group).setVisibility(LinearLayout.VISIBLE);
            	}else{
            		rootView.findViewById(R.id.metrics_edit_binary_default_radio_group).setVisibility(LinearLayout.GONE);
            		rootView.findViewById(R.id.metrics_edit_dflt_text).setVisibility(LinearLayout.VISIBLE);
            	}
            }
        });
        return rootView;
    }
    
    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();

    	switch (v.getId()) {
    	case R.id.metrics_details_button_average:
    		ToggleButton avgButton = (ToggleButton) v;
    		if(avgButton.isChecked()){
    	    	renderer.addSeriesRenderer(ravg);
    	    	dataset.addSeries(avgSeries);
    	    	mChartView.repaint();
    		}else{
    	    	renderer.removeSeriesRenderer(ravg);
    	    	dataset.removeSeries(avgSeries);
    	    	mChartView.repaint();
    		}
    		break;
    	case R.id.metrics_details_button_trend:
    		ToggleButton trendButton = (ToggleButton) v;
    		if(trendButton.isChecked()){
    	    	renderer.addSeriesRenderer(rtrend);
    	    	dataset.addSeries(trendSeries);
    	    	mChartView.repaint();
    		}else{
    	    	renderer.removeSeriesRenderer(rtrend);
    	    	dataset.removeSeries(trendSeries);
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
        	defaultButtons.setVisibility(View.GONE);
        	editButtons.setVisibility(View.VISIBLE);
        	buttonContainer.setVisibility(View.VISIBLE);
        	contentContainer.setVisibility(View.GONE);
        	editContainer.setVisibility(View.VISIBLE);
        	emptyMsg.setVisibility(View.GONE);
        	
        	EditText editDesc = (EditText) rootView.findViewById(R.id.metrics_edit_desc);
        	if(metric.getDesc() != null) 
        		editDesc.setText(metric.getDesc()); 
        	EditText editUnit = (EditText) rootView.findViewById(R.id.metrics_edit_unit);
        	if(metric.getUnit() != null) 
        		editUnit.setText(metric.getUnit());
        	
        	RadioButton defaultRadio = null;
        	if(metric.getType().equals("binary")){
        		defaultRadio = (RadioButton) rootView.findViewById(R.id.metrics_edit_radio_binary);
        		defaultRadio.setChecked(true);
        		RadioButton defaultBinary = null;
        		if(metric.getDflt() == 0){
        			defaultBinary = (RadioButton) rootView.findViewById(R.id.metrics_edit_radio_binary_yes);
        		}else{
        			defaultBinary = (RadioButton) rootView.findViewById(R.id.metrics_edit_radio_binary_no);
        		}
        		defaultBinary.setChecked(true);
            }else if(metric.getType().equals("increment")){
            	defaultRadio = (RadioButton) rootView.findViewById(R.id.metrics_edit_radio_increment);
            	defaultRadio.setChecked(true);
        		EditText editDflt = (EditText) rootView.findViewById(R.id.metrics_edit_dflt_text);
        		editDflt.setText(""+ metric.getDflt());
        	}else if(metric.getType().equals("count")){
        		defaultRadio = (RadioButton) rootView.findViewById(R.id.metrics_edit_radio_count);
        		defaultRadio.setChecked(true);
        		EditText editDflt = (EditText) rootView.findViewById(R.id.metrics_edit_dflt_text);
        		editDflt.setText(""+ metric.getDflt());
        	}
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
        case R.id.metrics_details_button_save:
        	
        	break;
        case R.id.metrics_details_button_editcancel:
        	editButtons.setVisibility(View.GONE);
        	defaultButtons.setVisibility(View.VISIBLE);
        	buttonContainer.setVisibility(View.VISIBLE);
        	editContainer.setVisibility(View.GONE);
        	if(entries.size() > 0){
        		contentContainer.setVisibility(View.VISIBLE);
        	}else{
        		emptyMsg.setVisibility(View.VISIBLE);
        	}
        	break;
        }
    }
    
    private XYMultipleSeriesRenderer getMultipleSeriesRenderer(){
    	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    	renderer.setAxesColor(Color.DKGRAY);
        renderer.setBarSpacing(0.25);
        renderer.setFitLegend(true);
        renderer.setLabelsColor(Color.LTGRAY);
        renderer.setLabelsTextSize(30);
//        renderer.setMargins(new int[] {20, 30, renderer.getLegendHeight() + 50, 20});
        renderer.setMarginsColor(ctx.getResources().getColor(R.color.default_background));
        renderer.setPointSize(0f);
        renderer.setShowCustomTextGrid(true);
        renderer.setShowLegend(false);
        renderer.setXLabels(0);
        renderer.setYAxisMax(1);
        renderer.setYAxisMin(0);
        renderer.setYLabelsAlign(Align.LEFT);
        return renderer;
    }
    private XYSeriesRenderer getSeriesRenderer(){
    	XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(ctx.getResources().getColor(R.color.lt_blue));
        r.setPointStyle(PointStyle.CIRCLE);
        r.setLineWidth(4f);
        r.setFillPoints(true);
        return r;
    }
}
