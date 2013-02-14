/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA_008
 * 
 * Creates the Timestamp Editor Page
 */

package com.ubundude.timesheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

/* 
 * TODO Need way to populate from database
 * TODO Buttons should display current date or DATE LOADED FROM DATABASE
 * TODO Hours TextView should update to total time calculated from timeIn - timeOut
 * TODO Figure out the spinner
 */

/**
 * TimestampEditorActivity Class
 *
 * Creates the layout and logic needed for the user to interact
 * with the editor to add a new timestamp to the database.
 */
public class TimestampEditorActivity extends Activity {
	/** Datasources to get objects for using database tables */
	private TimestampDataSource timeDS = new TimestampDataSource(this);
	private ProjectsDataSource proDS = new ProjectsDataSource(this);
	/** Gets a valid calendar instance */
	final Calendar c = Calendar.getInstance();
	/** Strings for correctly formating the date and time */
	private String dateForm = "MM/dd/yyyy";
	private String timeForm = "HH:mm";
	/** Strings to store formatted dates and times */
	private static String timeIn;
	private static String timeOut;
	private static String dateIn;
	private static String dateOut;
	/** Initialize all form elements for use*/
	static Button dateInButton;
	static Button timeInButton;
	static Button dateOutButton;
	static Button timeOutButton;
	static Button saveButton;
	static Spinner projectSpinner;
	EditText commentsEditText;
	/** Variable to store the value of the button calling the picker */
	private int fromWhere = 0;
	/** Gets a new TimePickerDialog and sets the calendar time to value picked */
	TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {		
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			updateLabel();
		}
	};
	/** Gets a new DatePickerDialog and sets the calendar time to the value picked */
	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month,
				int dayOfMonth) {
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}
	};
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_timestamp);
        
        initializeButtons();
        
		//try {
       	//	loadSpinnerData();
		//} catch (Exception ex){
		//	Log.d("spinnerLoadFail", ex.getMessage(), ex.fillInStackTrace());
		//}
		
		/**
		 * Logic to handle clicking the Time In button
		 * 
		 * Gets current time from the button to pass to the Time Picker
		 * and then calls a new TimePickerDialog to set the button
		 * to a new time
		 */
		timeInButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int hour, minute;
				hour = Integer.valueOf(timeInButton.getText().toString().substring(0, 2));
				minute = Integer.valueOf(timeInButton.getText().toString().substring(3));
				new TimePickerDialog(TimestampEditorActivity.this, t, 
						hour, minute,
						true).show();
				fromWhere = 1;
			}
		});
		
		/**
		 * Logic to handle clicking the Time Out button
		 * 
		 * Gets current time from the button to pass to the Time Picker
		 * and then calls a new TimePickerDialog to set the button
		 * to a new time
		 */
		timeOutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int hour, minute;
				hour = Integer.valueOf(timeOutButton.getText().toString().substring(0, 2));
				minute = Integer.valueOf(timeOutButton.getText().toString().substring(3));
				new TimePickerDialog(TimestampEditorActivity.this, t, 
						hour, minute,
						true).show();
				fromWhere = 2;
			}
		});
		
		/**
		 * Logic to handle clicking the Date In button
		 * 
		 * Gets current time from the button to pass to the Time Picker
		 * and then calls a new Date PickerDialog to set the button
		 * to a new time
		 */
		dateInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int year, month, dayOfMonth;
				year = Integer.valueOf(dateInButton.getText().toString().substring(6));
				month = Integer.valueOf(dateInButton.getText().toString().substring(0, 2)) - 1;
				dayOfMonth = Integer.valueOf(dateInButton.getText().toString().substring(3, 5));
				new DatePickerDialog(TimestampEditorActivity.this, d,
						year, month, dayOfMonth).show();
				fromWhere = 3;
			}
		});
		
		/**
		 * Logic to handle clicking the Date Out button
		 * 
		 * Gets current time from the button to pass to the Time Picker
		 * and then calls a new DatePickerDialog to set the button
		 * to a new time
		 */
		dateOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int year, month, dayOfMonth;
				year = Integer.valueOf(dateOutButton.getText().toString().substring(6));
				month = Integer.valueOf(dateOutButton.getText().toString().substring(0, 2)) - 1;
				dayOfMonth = Integer.valueOf(dateOutButton.getText().toString().substring(3, 5));
				new DatePickerDialog(TimestampEditorActivity.this, d,
						year, month, dayOfMonth).show();
				fromWhere = 4;
			}
		});
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveHandler(v);
			}
		});
        
	}
	
	private void initializeButtons() {
		/** Initialize buttons so that they can be set to the proper date */
		dateInButton = (Button)findViewById(R.id.dateInButton);
	    timeInButton = (Button)findViewById(R.id.timeInButton);
	    dateOutButton = (Button)findViewById(R.id.dateOutButton);
	    timeOutButton = (Button)findViewById(R.id.timeOutButton);
	    commentsEditText = (EditText)findViewById(R.id.commentsEditText);
	    saveButton = (Button)findViewById(R.id.saveTimestampButton);

		/** Get current date and time and store into proper variables */
		SimpleDateFormat dateFormer = new SimpleDateFormat(dateForm, Locale.US);
		SimpleDateFormat timeFormer = new SimpleDateFormat(timeForm, Locale.US);
		timeIn = timeFormer.format(c.getTime());
		timeOut = timeIn;
		dateIn = dateFormer.format(c.getTime());
		dateOut = dateIn;

		/** Set buttons to current time and date */
		dateInButton.setText(dateIn);
		dateOutButton.setText(dateOut);
		timeInButton.setText(timeIn);
		timeOutButton.setText(timeOut);
		
	}

	/** Method to set the value of the button used to the date or time picked */
	public void updateLabel() {
		SimpleDateFormat formTime = new SimpleDateFormat(timeForm, Locale.US);
		SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
		
		switch (fromWhere) {
		case 1:
			timeInButton.setText(formTime.format(c.getTime()));
			break;
		case 2:
			timeOutButton.setText(formTime.format(c.getTime()));
			break;
		case 3: 
			dateInButton.setText(formDate.format(c.getTime()));
			break;
		case 4:
			dateOutButton.setText(formDate.format(c.getTime()));
			break;
		}
	}

		/**
		 * Handler to save the data entered in the form to the database
		 * 
		 * @param view The current activity context
		 */
		public void saveHandler(View v) {
			/** Initialize variables to store information from the form elements */
			String timeIn, dateIn, timeOut, dateOut, comments;
        	int project;

        	/** Get and store form element values*/
        	dateIn = dateInButton.getText().toString();
        	timeIn = timeInButton.getText().toString();
        	dateOut = dateOutButton.getText().toString();
        	timeOut = timeOutButton.getText().toString();
        	comments = commentsEditText.getText().toString();
        	//TODO Implement spinners and get the id of the selected project
        	project = 1;
  
        	/** Open the timestamp table for writing */
        	timeDS.open();
        	
        	/** Call method to insert values into timestamp table */        
    		timeDS.createTimestamp(dateIn, timeIn, dateOut, timeOut, comments, project);

	        /** Close the database and return to the previous context */
	        timeDS.close();
	        finish();
        }

   		/**
   		 * Intent to move to project editor for editing currently selected project in the spinner
   		 * 
   		 * @param v Gets the current activity context to return to
   		 */
       	public void editHandler(View v) {
       		Intent intent = new Intent(this, ProjectEditorActivity.class);
       		startActivity(intent);
       	}

    	/**
    	 * Method to cancel adding new timestamp and return to the previous activity
    	 * 
    	 * @param v 
    	 */
       	public void cancelHandler(View v) {
       		finish();
       	}
       	
       	public void timestampDeleteHandler(View v) {
       		// TODO Write method
       	}
       /*
      	public List<Project> fetchAllProjects() {
       		
       		proDS.open();
			return proDS.getAllProjects();
       	}
       	
       //TODO Fix spinner loading data
       
       @SuppressWarnings("deprecation")
       private void loadSpinnerData() {
    	   Cursor c = fetchAllProjects();
    	   startManagingCursor(c);
    	   
    	   String[] from = new String[]{"name"};
    	   int[] to = new int[]{android.R.id.text1};
    	   SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
    			   c, from, to, 0);
    	   
    	   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	   
    	   projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
    	   projectSpinner.setAdapter(adapter);
    	   dbHelp.close();
    	   
       }*/
       
       
}
