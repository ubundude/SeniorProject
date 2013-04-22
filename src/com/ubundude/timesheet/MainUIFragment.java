package com.ubundude.timesheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class MainUIFragment extends Fragment {
	OnDateSetListener mCallback;
	/** Database instance and call to Timesheet OpenHelper */
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp;
	/** Gets a valid calendar instance for use */
	final Calendar c = Calendar.getInstance();
	/** Strings for formatting the date's and times for use */
	public String dateForm = "MM/dd/yyyy";
	public String timeForm = "HH:mm";
	/** Strings to store formated calendar outputs */
	public String date, dateView;
	/** Prepares buttons and EditText for use */
	public Button minusButton, plusButton, quickAdd;
	public EditText dateEditText;
	/** Formatters for the dates */
    SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
    SimpleDateFormat formTime = new SimpleDateFormat(timeForm, Locale.US);
    /** Gets a new DatePickerDialog and sets the calendar time to the value picked */
	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month,
				int dayOfMonth) {
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			try {
				updateLabel();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
	public interface OnDateSetListener {
		public void dateSetter(String date);
	}
	
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		
		try {
			mCallback = (OnDateSetListener) act;
		} catch (ClassCastException e) {
			throw new ClassCastException(act.toString()
					+ " must implement OnDateSetListener");
		}
		dbHelp = new TimesheetDatabaseHelper(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_ui, container, false);
	}
	
	@Override 
	public void onStart() {
		super.onStart();
		/** Method to get todays date and display it in the proper places */
        date = initialDates();
        Log.d("Initial Dates", date);
        
        /** Call to get the timestamps for the currently selected date */
        getDailyTimestamps(date);
        
        /**
         * Initialize minus button
         * 
         * This method calls the minus button handler and stores the date returned
         * and also gets new timestamps for the date returned. */
        minusButton = (Button)getView().findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					date = minusButtonHandler();
					getDailyTimestamps(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
        
        /**
         * Initialize plus button 
         * 
         * This method calls the plus button handler and stores the date returned
         * and also gets new timestamps for the date returned. */
        plusButton = (Button)getView().findViewById(R.id.plusButton);
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
        
       /** 
        * Initilize the quick add button
        * 
        * This method calls the quick add handler and then 
        * reloads timestamps for the current date.
        */
        quickAdd = (Button)getView().findViewById(R.id.quickAddButton);
        quickAdd.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		quickAddHandler(v);
        		getDailyTimestamps(date);
        	}

        });
	}
	
	protected void getDailyTimestamps(String date) {
		mCallback.dateSetter(date);
	}
	
	/** 
     * Method to get the current date form
     * 
     * Displays the formatted dates in the proper places 
     * and makes them available for usage elsewhere.
     * 
     * @return date The current date formatted for SQL queries.
     */
	public String initialDates() {
		Log.d("Initial Dates", "Funcion Entred");
        dateView = formDate.format(c.getTime());
    	date = formDate.format(c.getTime());
    	Log.d("Initial Dates", "Dates Formated");
        
    	/** Sets the text in the dateEditText to the current date */
        dateEditText = (EditText)getView().findViewById(R.id.dateEditText);
        Log.d("Initial Dates", "DateEditText Initialized");
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);
        Log.d("Initial Dates", "Before onCLickListener");
        dateEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("Initial Dates", "In the On Click Listener");
				int year, month, dayOfMonth;
				year = Integer.valueOf(dateEditText.getText().toString().substring(6));
				month = Integer.valueOf(dateEditText.getText().toString().substring(0, 2)) - 1;
				dayOfMonth = Integer.valueOf(dateEditText.getText().toString().substring(3, 5));
				new DatePickerDialog(getActivity(), d,
						year, month, dayOfMonth).show();
			}
		});
        
		return date;
	}
	
	/** Gets the next day and displays to dateEditText 
	 * @throws ParseException 
	 * @return date The current date formatted for SQL queries
	 */
    private String plusButtonHandler() throws ParseException {
    	
    	c.setTime(formDate.parse(dateView));
    	c.add(Calendar.DAY_OF_MONTH, 1);
    	
    	dateView = formDate.format(c.getTime());
    	date = formDate.format(c.getTime());
    	
    	dateEditText = (EditText)getView().findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);

    	return date;
	}

    /** Gets the previous day and displays to dateEditText 
     * @throws ParseException
     * @return date The current date formatted for SQL queries
     */
	private String minusButtonHandler() throws ParseException {
		c.setTime(formDate.parse(dateView));
    	c.add(Calendar.DAY_OF_MONTH, -1);
    	
    	dateView = formDate.format(c.getTime());
    	date = formDate.format(c.getTime());
    	
    	dateEditText = (EditText)getView().findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);
        
		return date;
	}
	
	/**
	 * Intent to move to TimestampEditorActivity
	 * 
	 *  @param view Gets the current view context to pass with the intent
	 */
	private void addNewHandler(View view) {
    	Intent intent = new Intent(getActivity(), EditorActivity.class);
    	startActivity(intent);
    }
    
	/**
	 * Get current date and time and place them into Timestamp table as generic entry
	 * 
	 * @param view
	 * @throws SQLException
	 */
	private void quickAddHandler(View view) throws SQLException {
	   /** Strings and int for the current dates and project */
	   String timeIn, timeOut, dateIn, dateOut;
	   int project = 1; //Will get from Default project in settings
	   timeIn = formTime.format(c.getTime());
	   timeOut = timeIn;
	   dateIn = formDate.format(c.getTime());
	   dateOut = dateIn;
	   
	   /** Open Database for writing */
	   db = dbHelp.getWritableDatabase();
	   
	   /** String to insert a timestamp into the database */
	   String insertSQL = "insert into timestamp (date_in, time_in, date_out, time_out, hours, project) " +
			   "values('" + dateIn + "', '" + timeIn + "', '" + dateOut +
			   "', '" + timeOut + "', 0, '" + project + "')";
	   try {
		   db.execSQL(insertSQL);
	   } catch(Exception e) {
		   Log.d("save Fail", e.getLocalizedMessage(), e.fillInStackTrace());
	   }
	   
	   /** Close the Database */
	   db.close();
    }
   
   public void updateLabel() throws ParseException {
		dateEditText.setText(formDate.format(c.getTime()));
   }

}
