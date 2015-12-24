package com.ems.lifetracker.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.ems.lifetracker.domain.*;
import com.ems.lifetracker.util.DataManager;
import com.ems.lifetracker.util.DateUtil;
import com.ems.lifetracker.MainActivity;
import com.ems.lifetracker.R;
import com.ems.lifetracker.adapter.EntriesListAdapter;
import com.ems.lifetracker.adapter.MetricsListAdapter;
import com.ems.lifetracker.domain.MetricEntry;

public class MetricsDetailsFragment extends Fragment implements OnClickListener{
	private String metricName,
		timeframe = "all";
	private double avg;
    private boolean archive;
	
	private Metric metric;
	private DataManager dm;
	private ArrayList<MetricEntry> entries;
	
	private Bundle bundle;
	private Context ctx;
	private FragmentManager fragmentManager;

	private GraphicalView mChartView;
	private ListView metricsDetailsListView;
	private TextView emptyMsg, archiveMsg;
	private View rootView;
    private Button archiveButton;
	
	private LinearLayout defaultButtons, 
		editButtons,
		buttonContainer,
		contentContainer,
		chartLayout;
	private RelativeLayout editContainer;

	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private XYSeriesRenderer ravg, rtrend7, rtrend30;
	private XYSeries avgSeries, trend7Series, trend30Series;
	
    public MetricsDetailsFragment(){}
     
    public void setTimeFrame(String timeframe){
    	this.timeframe = timeframe;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// Get handles for a bunch of UI elements
    	rootView = inflater.inflate(R.layout.fragment_metrics_details, container, false);
    	defaultButtons = (LinearLayout) rootView.findViewById(R.id.metrics_details_layout_defaultbuttons);
    	editButtons = (LinearLayout) rootView.findViewById(R.id.metrics_details_layout_editbuttons);
    	buttonContainer = (LinearLayout) rootView.findViewById(R.id.metrics_details_layout_buttonscontainer);
    	contentContainer = (LinearLayout) rootView.findViewById(R.id.metrics_details_content);
    	editContainer = (RelativeLayout) rootView.findViewById(R.id.metrics_details_edit_container);
    	emptyMsg = (TextView) rootView.findViewById(R.id.metrics_details_empty_msg);
        archiveMsg = (TextView) rootView.findViewById(R.id.metrics_details_archive_date);
    	metricsDetailsListView = (ListView) rootView.findViewById(R.id.metrics_details_list);
    	chartLayout = (LinearLayout) rootView.findViewById(R.id.metrics_details_chart);
        
    	// Set up button handlers
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
        archiveButton = (Button) rootView.findViewById(R.id.metrics_details_button_archive);
        archiveButton.setOnClickListener(this);

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
        
        bundle = this.getArguments();
    	ctx = getActivity();
    	((MainActivity)ctx).showActionBarMenu(true);
    	((MainActivity)ctx).setVisibleChart("details");
    	setHasOptionsMenu(true);
    	
    	dm = new DataManager(ctx);
    	metricName = bundle.getString("metricName");

		updateView();

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
    	case R.id.metrics_details_button_trend7:
    		ToggleButton trend7Button = (ToggleButton) v;
    		if(trend7Button.isChecked()){
    	    	renderer.addSeriesRenderer(rtrend7);
    	    	dataset.addSeries(trend7Series);
    	    	mChartView.repaint();
    		}else{
    	    	renderer.removeSeriesRenderer(rtrend7);
    	    	dataset.removeSeries(trend7Series);
    	    	mChartView.repaint();
    		}
    		break;
        case R.id.metrics_details_button_trend30:
            ToggleButton trend30Button = (ToggleButton) v;
            if(trend30Button.isChecked()){
                renderer.addSeriesRenderer(rtrend30);
                dataset.addSeries(trend30Series);
                mChartView.repaint();
            }else{
                renderer.removeSeriesRenderer(rtrend30);
                dataset.removeSeries(trend30Series);
                mChartView.repaint();
            }
            break;
        case R.id.metrics_details_button_cancel:
    		fragmentManager.beginTransaction()
        		.replace(R.id.main_container, new MetricsMainFragment())
        		.addToBackStack(null)
    			.commit();
    		break;

        case R.id.metrics_details_button_delete:
        	AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(ctx);

            deleteBuilder.setTitle("Confirm");
            deleteBuilder.setMessage("Are you sure you want to delete this metric?");
            deleteBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
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

            deleteBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
    	        @Override
    	        public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
    	    });

    	    AlertDialog deleteAlert = deleteBuilder.create();
            deleteAlert.show();

    		break;
        case R.id.metrics_details_button_archive:
            Calendar cal = Calendar.getInstance();
            if(metric.getArch() == null) {
                DatePickerDialog dialog = new DatePickerDialog(ctx,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int archYear, int archMonth, int archDay) {
								System.out.println("onDateSet ");
								Calendar archCal = Calendar.getInstance();
								archCal.set(Calendar.YEAR, archYear);
								archCal.set(Calendar.MONTH, archMonth);
								archCal.set(Calendar.DAY_OF_MONTH, archDay);
								String archDate = DateUtil.getFormattedDate(archCal.getTime());
								System.out.println("archDate: "+ archDate);
								DataManager dm = new DataManager(ctx);
								String metricName = bundle.getString("metricName");
								if (dm.archiveMetricByName(metricName, archDate)) {
									updateView();
								} else {
									Toast.makeText(ctx, "Something went wrong!",
											Toast.LENGTH_LONG).show();
								}
                            }
                        }
                        , cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)) {
                };

                dialog.show();
            }else{
                AlertDialog.Builder unarchiveBuilder = new AlertDialog.Builder(ctx);

                unarchiveBuilder.setTitle("Confirm");
                unarchiveBuilder.setMessage("Are you sure you want to unarchive this metric?");
                unarchiveBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager dm = new DataManager(ctx);
                        String metricName = bundle.getString("metricName");
                        if(dm.unarchiveMetricByName(metricName)){
                            updateView();
                        }else{
                            Toast.makeText(ctx, "Something went wrong!",
                                    Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }

                });

                unarchiveBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                });

                AlertDialog unarchiveAlert = unarchiveBuilder.create();
                unarchiveAlert.show();
            }

            break;
        case R.id.metrics_details_button_edit:
            System.out.println("edit");
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
        		if(metric.getDflt() > 0.0){
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
        case R.id.metrics_details_button_save:
        	EditText metricDescText = (EditText) rootView.findViewById(R.id.metrics_edit_desc);
        	String metricDesc = metricDescText.getText().toString();
        	
        	EditText metricUnitText = (EditText) rootView.findViewById(R.id.metrics_edit_unit);
        	String metricUnit = metricUnitText.getText().toString();
        	
        	EditText metricDfltText = (EditText) rootView.findViewById(R.id.metrics_edit_dflt_text);
        	double metricDflt;
        	if(metricDfltText.getText().toString().matches("")){
        		metricDflt = 0.0;
        	}else{
        		metricDflt = Double.parseDouble(metricDfltText.getText().toString()); 
        	}
        	
        	RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.metrics_edit_radio_group);
        	int selectedType = radioGroup.getCheckedRadioButtonId();
        	View radioButton = radioGroup.findViewById(selectedType);
        	int typeIdx = radioGroup.indexOfChild(radioButton);
        	
        	String metricType = null;
        	switch(typeIdx){
        	case 0:
        		metricType = "binary";
        		//get default value from yes/no radio group
            	RadioGroup g = (RadioGroup) rootView.findViewById(R.id.metrics_edit_binary_default_radio_group); 
            	int selected = g.getCheckedRadioButtonId();
            	RadioButton b = (RadioButton) rootView.findViewById(selected);
            	String metricDfltStr = b.getText().toString().toLowerCase();
            	metricDflt = metricDfltStr.equals("yes") ? 1 : 0;
        		break;
        	case 1:
        		metricType = "count";
        		break;
        	case 2:
        		metricType = "increment";
        		break;
        	}
 
	        Metric updateMetric = new Metric(metric.getName(), metricDesc, metricUnit, metricType, metricDflt);
	         
	        if(dm.updateMetric(updateMetric)){
	        	updateView();
	        }else{
	        	Toast.makeText(ctx, "Something went wrong!", 
	        			Toast.LENGTH_LONG).show();
	        }
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
    
    public void updateView(){
        // Create header area
        metric = dm.getMetricByName(metricName);
        ArrayList<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        MetricsListAdapter metricsListAdapter = new MetricsListAdapter(ctx, metrics);
        metricsDetailsListView.setAdapter(metricsListAdapter);
        entries = (ArrayList<MetricEntry>)dm.getEntriesByNameAndTimeframe(metricName, timeframe);

        if(entries.size() > 0){
			emptyMsg.setVisibility(View.GONE);
			contentContainer.setVisibility(View.VISIBLE);
			
			// Get chart container and add default data series
			dataset = new XYMultipleSeriesDataset();
	    	renderer = getMultipleSeriesRenderer();
	        
	    	// Set up the data renderer
	    	XYSeriesRenderer r = getSeriesRenderer();
	        renderer.addSeriesRenderer(r);
	        renderer.setXAxisMin(DateUtil.dateFromString(DateUtil.getOffsetDate(entries.get(0).getDate(), -1)).getTime() + 43200000);
            double maxDate = DateUtil.dateFromString( (entries.get(entries.size() - 1)).getDate() ).getTime();
            renderer.setXAxisMax(maxDate + 43200000);
	        //renderer.setXAxisMax(DateUtil.dateFromString(DateUtil.getOffsetDate(DateUtil.getFormattedDate(null), 1)).getTime() - 43200000);
	        
	        // Set up average renderer
	    	ravg = getSeriesRenderer();
	    	ravg.setColor(ctx.getResources().getColor(R.color.lt_green));
	    	ravg.setFillPoints(false);
	    	renderer.addSeriesRenderer(ravg);
	    	
	    	// Set up 7 day trend renderer
	    	rtrend7 = getSeriesRenderer();
	    	rtrend7.setColor(ctx.getResources().getColor(R.color.lt_yellow));
	    	rtrend7.setFillPoints(false);
	    	renderer.addSeriesRenderer(rtrend7);

            // Set up 30 day trend renderer
            rtrend30 = getSeriesRenderer();
            rtrend30.setColor(ctx.getResources().getColor(R.color.lt_yellow));
            rtrend30.setFillPoints(false);
            renderer.addSeriesRenderer(rtrend30);

            XYSeries series = new XYSeries(metric.getUnit());
	        avgSeries = new XYSeries("average");
	        trend7Series = new XYSeries("trend7");
            trend30Series = new XYSeries("trend30");
	    	
	        avg = 0.0;
	    	for(MetricEntry e : entries){
	    		avg += e.getCount();
	    	}
	    	avg = avg / entries.size();
	
	    	double ymin = entries.get(0).getCount();
	        double ymax = entries.get(0).getCount();
	        double trend7 = 0.0;
            double trend30 = 0.0;
            long entryDate;
            LinkedList<Double> trend7Queue = new LinkedList<Double>();
            LinkedList<Double> trend30Queue = new LinkedList<Double>();

	    	for(int i=0; i<entries.size(); i++){
	    		MetricEntry e = entries.get(i);

                trend7Queue.add(e.getCount());
                trend30Queue.add(e.getCount());

	    		entryDate = DateUtil.dateFromString(e.getDate()).getTime();
	    		series.add(entryDate, e.getCount());
                avgSeries.add(entryDate, avg);

                if(i >= 7){
                    trend7 = 0.0;
                    for(int j=0; j<trend7Queue.size(); j++){
                        trend7 += (7.0-j)/28.0 * trend7Queue.get(7-j);
                    }
                    trend7Series.add(entryDate, trend7);
                    trend7Queue.remove();
                }

                if(i >= 30){
                    trend30 = 0.0;
                    for(int j=0; j<trend30Queue.size(); j++){
                        trend30 += (30.0-j)/435.0 * trend30Queue.get(30-j);
                    }
                    trend30Series.add(entryDate, trend30);
                    trend30Queue.remove();
                }

	        	if((entries.size() <= 4) || (i % (entries.size() / 3) == 1)){
	        		renderer.addXTextLabel(entryDate, DateUtil.getFormattedDay(e.getDate()));
	        	}
	        	if(e.getCount() >= ymax) ymax = e.getCount();
	        	if(e.getCount() <= ymin) ymin = e.getCount();
	    	}
	        
	        dataset.addSeries(series);
	        dataset.addSeries(avgSeries);
	        dataset.addSeries(trend7Series);
            dataset.addSeries(trend30Series);
	        
	        if(metric.getType().equals("count")){
		        renderer.setYAxisMin(ymin * 0.9);
		        renderer.setYAxisMax(ymax * 1.1);
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE, LineChart.TYPE, LineChart.TYPE } );
	        }else if(metric.getType().equals("increment")){
		        renderer.setYAxisMin(ymin * 0.9);
		        renderer.setYAxisMax(ymax * 1.1);
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE, LineChart.TYPE, LineChart.TYPE } );
	        }else if(metric.getType().equals("binary")){
	        	renderer.addYTextLabel(0, "no");
	        	renderer.addYTextLabel(1, "yes");
		        renderer.setYLabels(0);
		        mChartView = ChartFactory.getCombinedXYChartView(ctx, dataset, renderer, 
		        		new String[] { BarChart.TYPE, LineChart.TYPE, LineChart.TYPE, LineChart.TYPE } );
	        }
	
	        chartLayout.removeAllViews();
	        chartLayout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        
	        Collections.reverse(entries);
	        EntriesListAdapter eAdapter = new EntriesListAdapter(ctx, entries);
	        final ListView eventsListView = (ListView)rootView.findViewById(R.id.metrics_details_events);
	        eventsListView.setAdapter(eAdapter);
	        
	        ToggleButton avgButton = (ToggleButton) rootView.findViewById(R.id.metrics_details_button_average);
	        avgButton.setOnClickListener(this);
	        avgButton.setText("Avg: "+ new DecimalFormat("#.##").format(avg));
	        avgButton.setTextOn("Avg: "+ new DecimalFormat("#.##").format(avg));
	        avgButton.setTextOff("Avg");
	        
	        ToggleButton trend7Button = (ToggleButton) rootView.findViewById(R.id.metrics_details_button_trend7);
	        trend7Button.setOnClickListener(this);
	        trend7Button.setText("7d Trend");
	        trend7Button.setTextOn("7d Trend");
	        trend7Button.setTextOff("7d Trend");

            ToggleButton trend30Button = (ToggleButton) rootView.findViewById(R.id.metrics_details_button_trend30);
            trend30Button.setOnClickListener(this);
            trend30Button.setText("30d Trend");
            trend30Button.setTextOn("30d Trend");
            trend30Button.setTextOff("30d Trend");
		}else{
			contentContainer.setVisibility(View.GONE);
			emptyMsg.setVisibility(View.VISIBLE);
		}

        if(metric.getArch() != null) {
            archiveMsg.setVisibility(View.VISIBLE);
            archiveMsg.setText("archived as of " + metric.getArch());
            archiveButton.setText("Restore");
        }else{
            archiveMsg.setVisibility(View.GONE);
            archiveButton.setText("Archive");
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
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}
