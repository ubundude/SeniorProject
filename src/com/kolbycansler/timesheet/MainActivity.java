/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA_007
 * 
 * Implements the Main layout class.
 */

package com.kolbycansler.timesheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/*
 * TODO Fix timestamps not displaying - test the date format being passed
 * TODO Figure out what else needs done :p
 */

/**
 * Main Activity Class
 * 
 * Implements the main page layout and logic for the elements of the page
 */
public class MainActivity extends Activity {
	/** Gets a valid calendar instance for use */
	final Calendar c = Calendar.getInstance();
	/** Strings for formatting the date's for use */
	public String dateViewForm = "EEE\nMM/dd/yyyy";
	public String dateForm = "M/dd/yyyy";
	/** Strings to store formated calendar outputs */
	public String date, dateView;
	/** Gets a new Datasource instance for dealing with the database tables */
	private TimestampDataSource timeDS = new TimestampDataSource(this);
	/** Prepares buttons and EditText for use */
	public ImageButton minusButton, plusButton;
	public EditText dateEditText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /** Open the database table for reading and writing */
        timeDS.open();
        
        /** Format the current date for use and store it in the date variables */
        SimpleDateFormat formDateView = new SimpleDateFormat(dateViewForm, Locale.US);
        SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
        dateView = formDateView.format(c.getTime());
    	date = formDate.format(c.getTime());
        
    	/** Sets the text in the dateEditText to the current date */
        dateEditText = (EditText)findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);
    	
        /** Database table function to return all timestamps for a given date */
        List<Timestamp> values = timeDS.getAllTimestamps(date); 
        
        /** 
         * Calls the custom ListView timestampListView and displays 
         * Timestamps to the main screen */
        ListView listView = (ListView)findViewById(R.id.timestampListView);
        listView.setAdapter(new TimestampAdapter(this, values));
       
        /**
         * Implements the Minus Button with OnCLickListener and
         * calls the minusButtonHandler if called */
        minusButton = (ImageButton)findViewById(R.id.minusImageButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				minusButtonHandler();
			}
		});
        
        /**
         * Implements the Plus Button with OnCLickListener and
         * calls the plusButtonHandler if called */
        plusButton = (ImageButton)findViewById(R.id.plusImageButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				plusButtonHandler();
			}
		});
    }
    
    /** Gets the next day and displays to dateEditText */
    protected void plusButtonHandler() {
    	// TODO Finish Logic here
    	// use add(c.Date, num to add);0
	}

    /** Gets the previous day and displays to dateEditText */
	protected void minusButtonHandler() {
		// TODO Finish Logic here
		
		
	}

	/**
	 * Intent to move to TimestampEditorActivity
	 * 
	 *  @param view Gets the current view context to pass with the intent
	 */
	public void addNewHandler(View view) {
    	Intent intent = new Intent(this, TimestampEditorActivity.class);
    	startActivity(intent);
    }
    
	/**
	 * Get current date and time and place them into Timestamp table as generic entry
	 * 
	 * @param view
	 */
    public void quickAddHandler(View view) {
    	String timeIn, timeOut, dateIn, dateOut, comment;
    	int project = 0;
    	comment = null;
    	timeIn = Integer.toString(Calendar.HOUR_OF_DAY) + ":" + Integer.toString(Calendar.MINUTE);
    	timeOut = timeIn;
    	dateIn = Integer.toString(Calendar.MONTH) + "/" + Integer.toString(Calendar.DAY_OF_MONTH) + "/" + Integer.toString(Calendar.YEAR);
    	dateOut = dateIn;
    	
    	timeDS.open();
    	try {
    	timeDS.createTimestamp(dateIn, timeIn, dateOut, timeOut, comment, project);
    	} catch (Exception ex) {
    		Log.d("QuickAddFail", ex.getMessage(), ex.fillInStackTrace());
    	}
    	timeDS.close();
    }

    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
