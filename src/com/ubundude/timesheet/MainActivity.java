/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA_015
 * 
 * Implements the Main layout class.
 */

package com.ubundude.timesheet;

import com.bugsense.trace.BugSenseHandler;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	public String dateViewForm = "EEE MM/dd/yyyy";
	public String dateForm = "MM/dd/yyyy";
	public String timeForm = "HH:mm";
	/** Strings to store formated calendar outputs */
	public String date, dateView;
	/** Prepares buttons and EditText for use */
	public Button minusButton, plusButton, quickAdd;
	public EditText dateEditText;
	public ListView list;
	TimestampAdapter adapter;
	SimpleDateFormat formDateView = new SimpleDateFormat(dateViewForm, Locale.US);
    SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
    SimpleDateFormat formTime = new SimpleDateFormat(timeForm, Locale.US);
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(MainActivity.this, "8b04fe90");
        setContentView(R.layout.activity_main);
      
        try {
			updateCheck();
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		
        /** Method to get todays date and display it in the proper places */
        date = initialDates();
        
        getDailyTimestamps(date);
        
        /**
         * Implements the Minus Button with OnCLickListener and
         * calls the minusButtonHandler if called */
        minusButton = (Button)findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					date = minusButtonHandler();
					getDailyTimestamps(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        /**
         * Implements the Plus Button with OnCLickListener and
         * calls the plusButtonHandler if called */
        plusButton = (Button)findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					date = plusButtonHandler();
					getDailyTimestamps(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
        
        quickAdd = (Button)findViewById(R.id.quickAddButton);
        quickAdd.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		quickAddHandler(v);
        		getDailyTimestamps(date);
        	}
        });
    }
    
    @SuppressWarnings("deprecation")
	private void updateCheck() throws NameNotFoundException, IOException, InterruptedException, ExecutionException {
    	Log.d("Checking for updates", "true");
    	String urlVersion;
    	UpdateCheck check = new UpdateCheck();
    	urlVersion = check.execute().get();
    	
		Log.d("urlVerion", urlVersion);
    	
    	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		String packageVersion = pInfo.versionName;
		
		Log.d("Package Version", packageVersion);
		
		if (!urlVersion.equals(packageVersion)) {
			AlertDialog alert = new AlertDialog.Builder(this).create();
			alert.setTitle("Version Check");
			alert.setMessage("You're version is out of date. Please visit " 
					+ "www.ubundude.com/p/beta.html to update to the latest version.");
			
			alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
        });
			alert.show();
		}
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
            	TextView idTV = (TextView)findViewById(R.id.listviewId);
            	int timeId = Integer.parseInt(idTV.getText().toString());
            	Intent intent = new Intent(MainActivity.this, TimestampEditorActivity.class);
            	intent.putExtra("TIMESTAMP_ID", timeId);
            	startActivity(intent);
            }
        });
	}

	private String initialDates() {
        dateView = formDateView.format(c.getTime());
    	date = formDate.format(c.getTime());
        
    	/** Sets the text in the dateEditText to the current date */
        dateEditText = (EditText)findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);

		return date;
	}

	/** Gets the next day and displays to dateEditText 
	 * @throws ParseException */
    private String plusButtonHandler() throws ParseException {
    	
    	c.setTime(formDateView.parse(dateView));
    	c.add(Calendar.DAY_OF_MONTH, 1);
    	
    	dateView = formDateView.format(c.getTime());
    	date = formDate.format(c.getTime());
    	
    	dateEditText = (EditText)findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);

    	return date;
	}

    /** Gets the previous day and displays to dateEditText 
     * @throws ParseException */
	private String minusButtonHandler() throws ParseException {
		c.setTime(formDateView.parse(dateView));
    	c.add(Calendar.DAY_OF_MONTH, -1);
    	
    	dateView = formDateView.format(c.getTime());
    	date = formDate.format(c.getTime());
    	
    	dateEditText = (EditText)findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);
        
		return date;
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
	 * @throws SQLException
	 */
   public void quickAddHandler(View view) throws SQLException {
    	String timeIn, timeOut, dateIn, dateOut;
    	int project = 1; //Will get from Default project in settings
    	timeIn = formTime.format(c.getTime());
    	timeOut = timeIn;
    	dateIn = formDate.format(c.getTime());
    	dateOut = dateIn;
    	db = dbHelp.getWritableDatabase();
    	String insertSQL = "insert into timestamp (date_in, time_in, date_out, time_out, hours, project) " +
				"values('" + dateIn + "', '" + timeIn + "', '" + dateOut +
				"', '" + timeOut + "', 0, '" + project + "')";
    	try {
    		db.execSQL(insertSQL);
    	} catch(Exception e) {
    		Log.d("save Fail", e.getLocalizedMessage(), e.fillInStackTrace());
    	}
    	db.close();
    }
    
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	
	
	public static void editClicked() {
		//TODO fix this motherfucker
	}
	 
}