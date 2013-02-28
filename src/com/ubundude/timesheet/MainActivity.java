/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA_015
 * 
 * Implements the Main layout class.
 */

package com.ubundude.timesheet;

import com.bugsense.trace.BugSenseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/*
 * TODO Figure out what else needs done :p
 */

/**
 * Main Activity Class
 * 
 * Implements the main page layout and logic for the elements of the page
 */
public class MainActivity extends Activity {
	public static final String KEY_ID = "listviewId";
	public static final String KEY_SHORT = "projectShortTextView";
	public static final String KEY_FULL = "projectFullTextView";
	public static final String KEY_HOURS = "listviewHoursTV";
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp = new TimesheetDatabaseHelper(this);
	/** Gets a valid calendar instance for use */
	final Calendar c = Calendar.getInstance();
	/** Strings for formatting the date's for use */
	public String dateViewForm = "EEE\nMM/dd/yyyy";
	public String dateForm = "MM/dd/yyyy";
	/** Strings to store formated calendar outputs */
	public String date, dateView;
	/** Prepares buttons and EditText for use */
	public Button minusButton, plusButton;
	public EditText dateEditText;
	public ListView list;
	TimestampAdapter adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(MainActivity.this, "8b04fe90");
        setContentView(R.layout.activity_main);
      
        /** Method to get todays date and display it in the proper places */
        date = initialDates();
        
        getDailyTimestamps(date);
        
        /**
         * Implements the Minus Button with OnCLickListener and
         * calls the minusButtonHandler if called */
        minusButton = (Button)findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				minusButtonHandler();
			}
		});
        
        /**
         * Implements the Plus Button with OnCLickListener and
         * calls the plusButtonHandler if called */
        plusButton = (Button)findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				plusButtonHandler();
			}
		});
    }
    
    @Override 
    protected void onResume() {
    	super.onResume();
    	getDailyTimestamps(date);
    }
    
    private void getDailyTimestamps(String date) {
    	int count;
    	int iter = 0;
    	
    	ArrayList<HashMap<String, String>> stampList = new ArrayList<HashMap<String, String>>();
    	
    	/** Open the database table for reading and writing */
        db = dbHelp.getReadableDatabase();
        
        String getTimestamps = "select ti._id, pr.name, pr.shortcode, ti.hours "
        		+ "from timestamp ti inner join projects pr "
        		+ "where ti.project = pr._id and ti.date_in = '" + date + "'";
    	
        Cursor cu = db.rawQuery(getTimestamps, null);
        
        if(cu != null && cu.getCount() > 0){
	        count = cu.getCount();
			cu.moveToFirst();
			
			while (iter != count) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_ID, Integer.toString(cu.getInt(0)));
				map.put(KEY_SHORT, cu.getString(1));
				map.put(KEY_FULL, cu.getString(2));
				map.put(KEY_HOURS, cu.getString(3));
				
				stampList.add(map);
				
				cu.moveToNext();
				iter++;
			}
        }
		
		cu.close();
		db.close();
        
        list = (ListView)findViewById(R.id.timestampListView);
        
        adapter = new TimestampAdapter(this, stampList);
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
 
            }
        });
  
		
	}

	private String initialDates() {
		/** Format the current date for use and store it in the date variables */
        SimpleDateFormat formDateView = new SimpleDateFormat(dateViewForm, Locale.US);
        SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
        dateView = formDateView.format(c.getTime());
    	date = formDate.format(c.getTime());
        
    	/** Sets the text in the dateEditText to the current date */
        dateEditText = (EditText)findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);

		return date;
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
   /* public void quickAddHandler(View view) {
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
    */
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public static void editClicked() {
		// TODO Auto-generated method stub
		
	}
	 
}