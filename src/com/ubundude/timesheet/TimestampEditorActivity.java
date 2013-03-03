/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA_016
 * 
 * Creates the Timestamp Editor Page
 */

package com.ubundude.timesheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

/* 
 * TODO Need way to populate from database
 * TODO Buttons should display current date or DATE LOADED FROM DATABASE
 * TODO Hours TextView should update to total time calculated from timeIn - timeOut
 * TODO Should reload spinner on return from Project Editor
 */

/**
 * TimestampEditorActivity Class
 *
 * Creates the layout and logic needed for the user to interact
 * with the editor to add a new timestamp to the database.
 */
public class TimestampEditorActivity extends Activity {
	/** Datasources to get objects for using database tables */
	private TimesheetDatabaseHelper dbHelp;
	private SQLiteDatabase db;
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
	static Button saveButton, cancelButton, deleteButton;
	static Spinner projectSpinner;
	EditText commentsEditText, timeEditText;
	/** Variable to store the value of the button calling the picker */
	private int fromWhere = 0;
	private int timeId;
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
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	timeId = extras.getInt("TIMESTAMP_ID");
        } else {
        	timeId = 0;
        }
        
        projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
        dbHelp = new TimesheetDatabaseHelper(this);
        
		try {
       		loadSpinnerData();
		} catch (Exception ex){
			Log.d("spinnerLoadFail", ex.getMessage(), ex.fillInStackTrace());
		}
        
	    try {
	        if(timeId != 0) {
	        	getTimestamp(timeId);
	        } else {
	        	initializeButtons();
	        }
	    }catch (Exception e) {
	    	Log.d("Timestamps Fail", e.getLocalizedMessage(), e.fillInStackTrace());
	    }
		
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
				if(timeId == 0) {
					saveHandler(v);
				} else {
					updateHandler(v, timeId);
				}
				
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				finish();
			}
		});
		
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteHandler(v, timeId);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		 projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
	        
			try {
	       		loadSpinnerData();
			} catch (Exception ex){
				Log.d("spinnerLoadFail", ex.getMessage(), ex.fillInStackTrace());
			}
	}
	
	private void initializeButtons() {
		/** Initialize buttons so that they can be set to the proper date */
		dateInButton = (Button)findViewById(R.id.dateInButton);
	    timeInButton = (Button)findViewById(R.id.timeInButton);
	    dateOutButton = (Button)findViewById(R.id.dateOutButton);
	    timeOutButton = (Button)findViewById(R.id.timeOutButton);
	    commentsEditText = (EditText)findViewById(R.id.commentsEditText);
	    saveButton = (Button)findViewById(R.id.saveTimestampButton);
	    cancelButton = (Button)findViewById(R.id.cancelTimestampButton);
	    deleteButton = (Button)findViewById(R.id.timestampDeleteButton);

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
			String timeIn, dateIn, timeOut, dateOut, comments, hours;
        	String projectName;
			int project;

        	/** Get and store form element values*/
        	dateIn = dateInButton.getText().toString();
        	timeIn = timeInButton.getText().toString();
        	dateOut = dateOutButton.getText().toString();
        	timeOut = timeOutButton.getText().toString();
        	comments = commentsEditText.getText().toString();
        	hours = timeCalc(dateIn, timeIn, dateOut, timeOut);
        	projectName = (String) projectSpinner.getSelectedItem();
        	project = getProjectId(projectName);
        	
        	if(project == 1) {
        		Context context = getApplicationContext();
    			String toastTest = "Please select a project.";
    			int duration = Toast.LENGTH_LONG;
    			
    			Toast toast = Toast.makeText(context, toastTest, duration);
    			toast.show();
        	} else {
        		String insertSQL = "insert into timestamp (date_in, time_in, date_out, time_out, comments, hours, project) " +
					"values('" + dateIn + "', '" + timeIn + "', '" + dateOut +
					"', '" + timeOut + "', '" + comments + "', '" + hours + "', '" + project + "')";
	        	/** Open the timestamp table for writing */
        		
        		db = dbHelp.getReadableDatabase();
        		
        		db.execSQL(insertSQL);
	     
		        /** Close the database and return to the previous context */
		        db.close();
		        finish();
        	}
        }
		
		private void updateHandler(View v, int timeId) {
			String timeIn, dateIn, timeOut, dateOut, comments, hours;
        	String projectName;
			int project;

        	/** Get and store form element values*/
        	dateIn = dateInButton.getText().toString();
        	timeIn = timeInButton.getText().toString();
        	dateOut = dateOutButton.getText().toString();
        	timeOut = timeOutButton.getText().toString();
        	comments = commentsEditText.getText().toString();
        	hours = timeCalc(dateIn, timeIn, dateOut, timeOut);
        	projectName = (String) projectSpinner.getSelectedItem();
        	project = getProjectId(projectName);
        	
        	String updateSQL = "update timestamp " +
        			"set date_in='" + dateIn + "', time_in='" + timeIn
        			+ "', date_out='" + dateOut + "', time_out='" + timeOut
        			+ "', comments='" + comments + "', hours='" + hours 
        			+ "', project=" + project + ", where _id = " + timeId;
        	
        	db = dbHelp.getWritableDatabase();
        	db.execSQL(updateSQL);
        	db.close();
        	finish();
		}

   		private int getProjectId(String projectName) {
   			String idSelect;
   			int projectId;
   			idSelect = "select _id from projects where name = '" + projectName + "'";
   			
   			db = dbHelp.getReadableDatabase();
   			
   			Cursor cu = db.rawQuery(idSelect, null);
   			cu.moveToFirst();
   			
   			projectId = cu.getInt(0);
   			
   			cu.close();
   		
			return projectId;
		}

		/**
   		 * Intent to move to project editor for editing currently selected project in the spinner
   		 * 
   		 * @param v Gets the current activity context to return to
   		 */
       	public void editHandler(View v) {
       		String project = (String) projectSpinner.getSelectedItem();
       		Intent intent = new Intent(this, ProjectEditorActivity.class);
       		intent.putExtra("PROJECT_NAME", project);
       		startActivity(intent);
       	}
       	
       	public void deleteHandler(View v, int timeId) {
       		String deleteSQL = "delete from timestamp where _id = " + timeId;
       		db = dbHelp.getWritableDatabase();
       		db.execSQL(deleteSQL);
       		db.close();
       		finish();
       	}
       
       	public void loadSpinnerData() {
       		List<String> projects = new ArrayList<String>();
    		
    		String selectQuery = "select * from projects";
    		
    		db = dbHelp.getReadableDatabase();
    		
    		Cursor cu = db.rawQuery(selectQuery, null);

    		if (cu.moveToFirst()) {
    			do {
    				projects.add(cu.getString(1));
    			} while(cu.moveToNext());
    		}
    		
    		cu.close();
    		db.close();
       		
       		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
       				android.R.layout.simple_spinner_item, projects);
       		
       		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       		
       		projectSpinner.setAdapter(dataAdapter);
       	}
       	
       	private int getTimestamp(int timeId) throws NullPointerException {
       		int id = timeId;
       		String selectTimestamp =" select * from timestamp where _id = " + id;
       		timeEditText = (EditText)findViewById(R.id.timeEditText);
       		dateInButton = (Button)findViewById(R.id.dateInButton);
	       	timeInButton = (Button)findViewById(R.id.timeInButton);
	 	    dateOutButton = (Button)findViewById(R.id.dateOutButton);
	 	    timeOutButton = (Button)findViewById(R.id.timeOutButton);
	 	    commentsEditText = (EditText)findViewById(R.id.commentsEditText);
	 	    saveButton = (Button)findViewById(R.id.saveTimestampButton);
	 	    cancelButton = (Button)findViewById(R.id.cancelTimestampButton);
		    deleteButton = (Button)findViewById(R.id.timestampDeleteButton);
	       		
       		db = dbHelp.getReadableDatabase();
       		Cursor cu = db.rawQuery(selectTimestamp, null);
       		cu.moveToFirst();
       		
       		
       		dateInButton.setText(cu.getString(1));
       		timeInButton.setText(cu.getString(2));
       		dateOutButton.setText(cu.getString(3));
       		timeOutButton.setText(cu.getString(4));
       		commentsEditText.setText(cu.getString(5));
       		timeEditText.setText(cu.getString(6));
       		
       		db.close();
       		cu.close();
       		
       		return id;
       	}
       	
       	public static String timeCalc(String dateIn, String timeIn, String dateOut, String timeOut) {
    		String hours = "0.00";
    		String hr, min;
    		long hrb, minb;
    		String in = dateIn + " " + timeIn;
    		String out = dateOut + " " + timeOut;
    		Date inTime, outTime;
    		SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyy HH:mm", Locale.US);
    		
    		
				try {
					inTime = dateForm.parse(in);
				outTime = dateForm.parse(out);
				long diff = outTime.getTime() - inTime.getTime();
				hrb = diff/ (1000 * 60 * 60 );
				minb = diff / (1000 * 60);
				minb = minb - 60 * hrb;
				hr = String.valueOf(hrb);
				min = String.valueOf(minb);
				hours = hr + "." + min;
				} catch (ParseException e) {
					e.printStackTrace();
				}
    		
    		return hours;
				
    	}
       
}
