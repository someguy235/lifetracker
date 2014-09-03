package com.ems.lifetracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
//	private Menu menu;
	private boolean showMenu = false;
	private String visibleChart = null;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        DataManager dm = new DataManager(this);
        ArrayList<Metric> metrics = (ArrayList<Metric>)dm.getAllMetrics();
        if(metrics.size() > 0){
        	onNavigationDrawerItemSelected(0);
        }else{
        	onNavigationDrawerItemSelected(1);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;
        switch (position){
        	case 0:
        		mTitle = getString(R.string.title_section1);
        		fragment = new TrackMainFragment();
        		break;
        	case 1:
        		mTitle = getString(R.string.title_section2);
        		fragment = new MetricsMainFragment();
        		break;
        	case 2:
        		mTitle = getString(R.string.title_section3);
        		fragment = new HistoryMainFragment();
        		break;
        }
        fragmentManager.beginTransaction()
        	.replace(R.id.main_container, fragment)
        	.addToBackStack(null)
        	.commit();
    } 

    public void showActionBarMenu(boolean show){
        this.showMenu = show;
    }
    
    public void setVisibleChart(String visibleChart){
    	this.visibleChart = visibleChart;
    }
    
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//    	this.menu = menu;
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
        	if(showMenu){
        		getMenuInflater().inflate(R.menu.main, menu);
        		menu.getItem(2).setChecked(true);
        	}
    		restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	String timeframe = null;
        int id = item.getItemId();
        if (id == R.id.time_menu_week) {
        	item.setChecked(true);
        	timeframe = "week";
        }
        if (id == R.id.time_menu_month) {
        	item.setChecked(true);
        	timeframe = "month";
        }
        if (id == R.id.time_menu_all) {
        	item.setChecked(true);
        	timeframe = "all";
        }

        if(timeframe != null){
        	if(visibleChart.equals("history")){
        		HistoryMainFragment fragment = (HistoryMainFragment) getFragmentManager().findFragmentById(R.id.main_container);
        		fragment.setTimeFrame(timeframe);
        		fragment.updateEntries();
        		fragment.updateDates();
        		fragment.updateChart();
        	}
        	if(visibleChart.equals("details")){
        		MetricsDetailsFragment fragment = (MetricsDetailsFragment) getFragmentManager().findFragmentById(R.id.main_container);
        		fragment.setTimeFrame(timeframe);
        		fragment.updateView();
        	}
        	return true;
        }else{
        	return super.onOptionsItemSelected(item);
        }
    }
        
    public void chooseActivity(){
    }
}
