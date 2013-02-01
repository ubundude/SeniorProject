/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA_008
 * 
 * Creates the Timestamp Editor Page
 */

package com.kolbycansler.timesheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.Spinner;
import android.widget.TimePicker;

/* 
 * TODO Need way to populate from database
 * TODO Buttons should display current date or date loaded from database
 * TODO Buttons should popup a datePicker when clicked and set text to that
 * TODO Hours TextView should update to total time calulated from timeIn - timeOut
 * TODO Need delete button
 * TODO Figure out the spinner
 */

/**
 * TimestampEditorActivity Class
 *
 * Creates the layout and logic needed for the user to interact
 * with the editor to add a new timestamp to the database.
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TimestampEditorActivity extends Activity {
	/** Datasources to get objects for using database tables */
	private TimestampDataSource timeDS;
	//private ProjectsDataSource proDS;
	/** Gets a valid calendar instance */
	final Calendar c = Calendar.getInstance();
	/** Strings for correctly formating the date and time */
	private String dateForm = "MM/dd/yyyy";
	private String timeForm = "HH:mm";
	/** Strings to store formatted dates and times */
	private static String timeIn;
	private static String timeOut;
	private String dateIn;
	private String dateOut;
	private static String fromWhere;
	/** Id's for the dialogs used by time and date pickers */
	static final int DATE_IN_DIALOG_ID = 0;
	static final int TIME_IN_DIALOG_ID = 0;
	static final int DATE_OUT_DIALOG_ID = 0;
	static final int TIME_OUT_DIALOG_ID = 0;
	/** Initialize all buttons for use*/
	Button dateInButton;
	static Button timeInButton;
	Button dateOutButton;
	static Button timeOutButton;
	/** Initializes spinner for use */
	//Spinner projectSpinner;
	/** Initialize the EditText for use */
	EditText commentsEditText;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_timestamp);
        
    	/** Initialize buttons so that they can be set to the proper date */
		dateInButton = (Button)findViewById(R.id.dateInButton);
	    timeInButton = (Button)findViewById(R.id.timeInButton);
	    dateOutButton = (Button)findViewById(R.id.dateOutButton);
	    timeOutButton = (Button)findViewById(R.id.timeOutButton);

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
        
        //projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
        
        //loadSpinnerData();
		
		timeInButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				fromWhere = "timeIn";
				showTimePickerDialog(this);
			}
		});
		
		timeOutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				fromWhere = "timeOut";
				showTimePickerDialog(this);
				
			}
		});
        
	}

		/**
		 * Handler to save the data entered in the form to the database
		 * 
		 * @param view The current activity context
		 */
		public void saveHandler(View view) {
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
        	
        	try {
        		/** Call method to insert values into timestamp table */        
        		timeDS.createTimestamp(dateIn, timeIn, dateOut, timeOut, comments, project);
            } catch (Exception ex) {
            	Log.d("SaveFail", ex.getMessage(), ex.fillInStackTrace());
            } 

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
       	
       	@SuppressLint("NewApi")
		public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
       		
       		@SuppressLint("NewApi")
			@Override
       		public Dialog onCreateDialog(Bundle savedInstanceState) {
       			final Calendar c = Calendar.getInstance();
       			int hour = c.get(Calendar.HOUR_OF_DAY);
       			int minute = c.get(Calendar.MINUTE);
       			
       			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
       		}
       		
       		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
       			// TODO Set to something
       			String time;
       			time = hourOfDay + ":" + minute;
       			if (fromWhere.equals(timeIn)) {
       				timeInButton.setText(time);
       			} else if (fromWhere.equals(timeOut)) {
       				timeOutButton.setText(time);
       			}
       			
       		}
       	}
       	
       	public void showTimePickerDialog(OnClickListener click) {
       		DialogFragment newFragment = new TimePickerFragment();
       		newFragment.show(getFragmentManager(), "timePicker");
       	}
       
       //TODO Fix spinner loading data
       
      /* private void loadSpinnerData() {
    	   List<Project> labels = proDS.getAllProjects();
    	   ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels);
    	   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	   projectSpinner.setAdapter(dataAdapter);
       }
       */
       
}
