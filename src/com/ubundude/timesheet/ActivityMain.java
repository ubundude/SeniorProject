/**
 * 
 */
package com.ubundude.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author kolby
 *
 */
public class ActivityMain extends FragmentActivity implements MainUIFragment.SelectedDateListener{
	String lDate;
	/** Keys for the HashMap used in the list view */
	public static final String KEY_ID = "listviewId";
	public static final String KEY_SHORT = "projectShortTextView";
	public static final String KEY_FULL = "projectFullTextView";
	public static final String KEY_HOURS = "listviewHoursTV";
	public static final String KEY_PROID = "projectIdTV";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//BugSenseHandler.initAndStartSession(MainActivity.this, "8b04fe90");
		setContentView(R.layout.activity_main);
	}

	@Override
	public void selectedDateListener(String date) {
		lDate = date;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_preferences:
			startActivity(new Intent(this, EditPreferences.class));
			return(true);
		}
		return(super.onOptionsItemSelected(item));
	}
}
