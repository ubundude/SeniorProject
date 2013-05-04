/** 
 * Copyright 2013 Kolby Cansler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ubundude.timesheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimestampEditorActivity extends Activity {

	/** Keys for bundle passing */
	public static final String KEY_NAME = "projectName";
	public static final String TIME_ID_KEY = "timeId";
	public static final String PRO_ID_KEY = "projectId";
	/** Datasources to get objects for using database tables */
	private TimesheetDatabaseHelper dbHelp = new TimesheetDatabaseHelper(this);
	private SQLiteDatabase db;
	/** Gets a valid calendar instance */
	final Calendar c = Calendar.getInstance();
	/** Strings for correctly formating the date and time */
	private String dateForm = "MM/dd/yyyy";
	private String timeForm = "HH:mm";
	public String weekInYearForm = "ww";
	public String monthNumForm = "MM";
	public String yearForm = "yy";
	/** DateFormaters */
	SimpleDateFormat dateFormer = new SimpleDateFormat(dateForm, Locale.US);
	SimpleDateFormat timeFormer = new SimpleDateFormat(timeForm, Locale.US);
	SimpleDateFormat formWIM = new SimpleDateFormat(weekInYearForm, Locale.US);
	SimpleDateFormat formYear = new SimpleDateFormat(yearForm, Locale.US);
	SimpleDateFormat formMonthNum = new SimpleDateFormat(monthNumForm, Locale.US);
	/** Strings to store formatted dates and times */
	private static String timeIn;
	private static String timeOut;
	private static String dateIn;
	private static String dateOut;
	/** Initialize all form elements for use */
	static Button dateInButton;
	static Button timeInButton;
	static Button dateOutButton;
	static Button timeOutButton;
	static Button editButton, saveButton, cancelButton, deleteButton;
	static Spinner projectSpinner;
	EditText commentsEditText, timeEditText;
	SpinnerAdapter adapter;
	/** Variable to store the value of the button calling the picker */
	private int fromWhere = 0;
	private int timeId, proId;
	String LINE_END = System.getProperty("line.separator");
	/** Gets a new TimePickerDialog and sets the calendar time to value picked */
	TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {	
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			try {
				updateLabel();
			} catch (ParseException e) {
				e.printStackTrace();
			}
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
			try {
				updateLabel();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor_timestamp);

		/** Get the shared preference for the Default Project */
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int proIdPref = Integer.parseInt(pref.getString(getResources().getString(R.string.prefProjectKey), "1"));
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			timeId = extras.getInt(TIME_ID_KEY);
			Log.d("TIMESTAMP onAttach", "Got TimeId: " + timeId);
			proId = extras.getInt(PRO_ID_KEY);
			Log.d("TIMESTAMP onAttach", "Got ProId: " + proId);
		} else {
			timeId = 0;
			proId = proIdPref;
		}

		Log.d("TIMESTAMP onStart", "Its started");
		if (timeId != 0) {
			Log.d("TIMESTAMP onStart", "Getting timestamps");
			getTimestamp(timeId);
		} else {
			Log.d("TIMESTAMP onStart", "Initializing Buttons");
			initializeButtons();
		}
		loadSpinnerData(proId);
		/** Initializes the edit button and sets the onClickListener*/
		editButton = (Button)findViewById(R.id.projectEditButton);
		editButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/** If button is clicked, get the value from the textview */
				TextView idTv = (TextView)findViewById(R.id.proIdTV);
				Log.d("Edit Button", "textView set");
				/** Get the value of textView and store it to send on */
				int proId = Integer.parseInt(idTv.getText().toString());
				Log.d("Edit Button", "Project id: " + proId);
				editProject(proId);
			}


		});

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

		/**
		 * Logic to handle clicking the Save button
		 * 
		 * If a timestamp ID was passed to the editor, then
		 * the update handler is called, otherwise, a new
		 * timestamp is saved to the database
		 */
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(timeId == 0) {
					try {
						saveHandler(v);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					try {
						updateHandler(v, timeId);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

			}
		});

		/**
		 *Set the Cancel Button OnClickListener
		 */
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				finish();
			}
		});

		/**
		 * Checks the build number of the host android platform and if 
		 * 3.x or 4.x, hides the delete button in the layout as it will 
		 * be shown in the action bar, otherwise sets the Delete Button
		 * with an OnClickListener
		 */
		if(android.os.Build.VERSION.RELEASE.startsWith("3.") ||
				android.os.Build.VERSION.RELEASE.startsWith("4.")) {
			deleteButton.setVisibility(View.GONE);
		} else {
			deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteHandler(timeId);
				}
			});
		}
	}

	/**
	 * Method to start the project editor and pass the projectId
	 * 
	 * @param lProId
	 */
	protected void editProject(int lProId) {
		Intent intent = new Intent(this, ProjectEditorActivity.class);
		intent.putExtra(ProjectEditorActivity.PRO_KEY, lProId);
		startActivity(intent);
	}

	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.editor_menu, menu);
		super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editor_preferences:
			startActivity(new Intent(this, EditPreferences.class));
			return(true);
		case R.id.delete_item:
			deleteHandler(timeId);
			return(true);
		}
		return(super.onOptionsItemSelected(item));
	}

	/**
	 * Initialize the buttons for the editor
	 */
	private void initializeButtons() {
		Log.d("TIMESTAMP FRAGMENT", "Initializing Buttons");
		/** Initialize buttons so that they can be set to the proper date */
		dateInButton = (Button)findViewById(R.id.dateInButton);
		timeInButton = (Button)findViewById(R.id.timeInButton);
		dateOutButton = (Button)findViewById(R.id.dateOutButton);
		timeOutButton = (Button)findViewById(R.id.timeOutButton);
		commentsEditText = (EditText)findViewById(R.id.commentsEditText);
		saveButton = (Button)findViewById(R.id.saveTimestampButton);
		cancelButton = (Button)findViewById(R.id.cancelTimestampButton);
		deleteButton = (Button)findViewById(R.id.timestampDeleteButton);
		timeEditText = (EditText)findViewById(R.id.timeEditText);

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

	/** Method to set the value of the button used to the date or time picked
	 * 
	 * @throws ParseException 
	 */
	private void updateLabel() throws ParseException {
		SimpleDateFormat formTime = new SimpleDateFormat(timeForm, Locale.US);
		SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);

		switch (fromWhere) {
		case 1:
			timeInButton.setText(formTime.format(c.getTime()));
			timeEditText.setText(timeCalc() + " h");
			break;
		case 2:
			timeOutButton.setText(formTime.format(c.getTime()));
			timeEditText.setText(timeCalc() + " h");
			break;
		case 3:
			dateInButton.setText(formDate.format(c.getTime()));
			timeEditText.setText(timeCalc() + " h");
			break;
		case 4:
			dateOutButton.setText(formDate.format(c.getTime()));
			timeEditText.setText(timeCalc() + " h");
			break;
		}
	}

	/**
	 * Handler to save the data entered in the form to the database
	 *
	 * @param view The current activity context
	 * @throws ParseException
	 */
	private void saveHandler(View v) throws ParseException {
		/** Initialize variables to store information from the form elements */
		String timeIn, dateIn, timeOut, dateOut, wiy, month, year, comments, hours;

		/** Get and store form element values*/
		dateIn = dateInButton.getText().toString();
		timeIn = timeInButton.getText().toString();
		dateOut = dateOutButton.getText().toString();
		timeOut = timeOutButton.getText().toString();
		comments = commentsEditText.getText().toString();
		hours = timeCalc();

		/** Splits the date into a string array that can be proccessed and used to set the calendar instance */
		String[] arrDate = dateIn.split("/");
		int lMonth = Integer.parseInt(arrDate[0]) - 1;
		Log.d("SaveHandler", "Month is: " + lMonth);
		int lDay = Integer.parseInt(arrDate[1]);
		Log.d("SaveHandler", "Day is: " + lDay);
		int lYear = Integer.parseInt(arrDate[2]);
		Log.d("SaveHandler", "Year is: " + lYear);

		Calendar lc = Calendar.getInstance();
		lc.set(Calendar.YEAR, lYear);
		lc.set(Calendar.MONTH, lMonth);
		lc.set(Calendar.DAY_OF_MONTH, lDay);
		//TODO set first day of week from preference

		/** Sets the date of the calendar instance */
		wiy = formWIM.format(lc.getTime());
		month = Integer.toString(lMonth);
		year = Integer.toString(lYear);

		/** Get the project id from the spinner text view */
		TextView idTv = (TextView)findViewById(R.id.proIdTV);
		int proId = Integer.parseInt(idTv.getText().toString());

		/** If the project Id is 1, display a toast, else insert time stamp into database */
		if(proId == 1) {
			Toast toast = Toast.makeText(this, "Please select a project", Toast.LENGTH_LONG);
			toast.show();
		} else {
			String insertSQL = "insert into timestamp (date_in, time_in, date_out, time_out, week_year, year, month, comments, hours, project) " +
					"values('" + dateIn + "', '" + timeIn + "', '" + dateOut + "', '" + timeOut + "', '"
					+ wiy + "', '" + year + "', '" + month + "', '" + comments + "', '" + hours + "', '" + proId + "')";

			db = dbHelp.getReadableDatabase();

			db.execSQL(insertSQL);

			/** Close the database and return to the previous context */
			db.close();

			finish();
		}
	}

	/**
	 * Method to update record in the database
	 * 
	 * @param v      The passed view from the button
	 * @param timeId The Id of the time stamp to update
	 * @throws ParseException
	 */
	private void updateHandler(View v, int timeId) throws ParseException {
		String timeIn, dateIn, timeOut, dateOut, wiy, month, year, comments, hours;

		/** Get and store form element values*/
		dateIn = dateInButton.getText().toString();
		timeIn = timeInButton.getText().toString();
		dateOut = dateOutButton.getText().toString();
		timeOut = timeOutButton.getText().toString();
		comments = commentsEditText.getText().toString();
		hours = timeCalc();
		TextView idTv = (TextView)findViewById(R.id.proIdTV);
		int proId = Integer.parseInt(idTv.getText().toString());

		/** Splits the date into a string array that can be proccessed and used to set the calendar instance */
		String[] arrDate = dateIn.split("/");
		int lMonth = Integer.parseInt(arrDate[0]) - 1;
		int lDay = Integer.parseInt(arrDate[1]);
		int lYear = Integer.parseInt(arrDate[2]);

		/** Sets the date of the calendar instance */
		Calendar lc = Calendar.getInstance();
		lc.set(Calendar.YEAR, lYear);
		lc.set(Calendar.MONTH, lMonth);
		lc.set(Calendar.DAY_OF_MONTH, lDay);

		/** Format the date into variables for storage */
		wiy = formWIM.format(lc.get(Calendar.WEEK_OF_YEAR));
		month = formMonthNum.format(lc.get(Calendar.MONTH));
		year = formYear.format(lc.get(Calendar.YEAR));

		/** SQL Query to update the time stamp */
		String updateSQL = "update timestamp " +
				"set date_in='" + dateIn + "', time_in='" + timeIn
				+ "', date_out='" + dateOut + "', time_out='" + timeOut
				+ "', week_year='" + wiy + "', year='" + year + "', month='" + month
				+ "', comments='" + comments + "', hours='" + hours
				+ "', project=" + proId + " where _id = " + timeId;

		db = dbHelp.getWritableDatabase();
		db.execSQL(updateSQL);
		db.close();

		finish();
	}

	/**
	 * Method to delete the timestamp from the database
	 * 
	 * @param timeId The Id of the timestamp to delete
	 */
	public void deleteHandler(int timeId) {
		/** SQL Query to delete the timestamp */
		String deleteSQL = "delete from timestamp where _id = " + timeId;
		db = dbHelp.getWritableDatabase();
		db.execSQL(deleteSQL);
		db.close();

		finish();
	}

	/**
	 * Method to load the projects into the custom spinner adapter]
	 * 
	 * @param proId The Id of the project to load
	 */
	private void loadSpinnerData(int proId) {
		int pos = 0;
		int id = proId;
		
		/** Instantiates the spinner */
		projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
		/** Creates a new array list that takes a Hash Map for arguments */
		ArrayList<HashMap<String, String>> projects = new ArrayList<HashMap<String, String>>();

		/** SQL Query to get the projects from the database */
		String selectQuery = "select _id, name from projects";
		db = dbHelp.getReadableDatabase();

		Cursor cu = db.rawQuery(selectQuery, null);

		if (cu != null && cu.getCount() > 0) {
			cu.moveToFirst();
			do {
				/** Create a new HashMap to pass to the ArrayList */
				HashMap<String, String> map = new HashMap<String, String>();
				
				/** Puts the cursor values in the HashMap */
				map.put(KEY_NAME, cu.getString(1));
				map.put(PRO_ID_KEY, Integer.toString(cu.getInt(0)));
				
				/** Looks for the project that matches the passed id to set the spinner to */
				if (cu.getInt(0) == id) {
					Log.d("loadSpinnerData", "Found Loaded project");
					pos = cu.getPosition();
				}
				
				/** Add the map to projects */
				projects.add(map);

			} while(cu.moveToNext());
		}
		
		/** Close the cursor and database */
		cu.close();
		db.close();
		
		/** Get a new spinner adapter instance and set the adapter to it */
		adapter = new SpinnerAdapter(this, projects);
		projectSpinner.setAdapter(adapter);

		projectSpinner.setSelection(pos);
	}

	/**
	 * 
	 * @param  timeId The id of the timestamp to load into the editor
	 * @return id     Returns the id of the timestamp
	 * @throws NullPointerException
	 */
	public int getTimestamp(int timeId) throws NullPointerException {
		/** Set id to the timeId */
		int id = timeId;
		
		/** Select SQL to select the timestamp from the database */
		String selectTimestamp =" select _id, date_in, time_in, date_out, time_out, comments, hours from timestamp where _id = " + id;
		
		/** Initiaates the textviews and buttons */
		timeEditText = (EditText)findViewById(R.id.timeEditText);
		dateInButton = (Button)findViewById(R.id.dateInButton);
		timeInButton = (Button)findViewById(R.id.timeInButton);
		dateOutButton = (Button)findViewById(R.id.dateOutButton);
		timeOutButton = (Button)findViewById(R.id.timeOutButton);
		commentsEditText = (EditText)findViewById(R.id.commentsEditText);
		saveButton = (Button)findViewById(R.id.saveTimestampButton);
		cancelButton = (Button)findViewById(R.id.cancelTimestampButton);
		deleteButton = (Button)findViewById(R.id.timestampDeleteButton);

		/** Open the database, and run the query */
		db = dbHelp.getReadableDatabase();
		Cursor cu = db.rawQuery(selectTimestamp, null);
		cu.moveToFirst();

		/** Sets the views to the loaded timestamp values */
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

	/** 
	 * Method to calculate the difference between the clock in and clock out time 
	 * 
	 * @return hour
	 * @throws ParseException
	 */
	private static String timeCalc() throws ParseException {
		/** Local variables to store dates and times getting calculated */
		String cdateIn, ctimeIn, cdateOut, ctimeOut;
		/** Variables to store calculated times */
		double hr;
		double diff;

		/** Gets the button labels and stores them */
		cdateIn = dateInButton.getText().toString();
		ctimeIn = timeInButton.getText().toString();
		cdateOut = dateOutButton.getText().toString();
		ctimeOut = timeOutButton.getText().toString();

		/** Puts the dates and times together formating */
		String in = cdateIn + " " + ctimeIn;
		String out = cdateOut + " " + ctimeOut;
		
		/** Creates date variables and form for parsing date strings */
		Date inTime, outTime;
		SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyy HH:mm", Locale.US);

		/** Parses the in time and out time into Dates */
		inTime = dateForm.parse(in);
		outTime = dateForm.parse(out);
		
		/** Calculate the difference between the Dates and store as a long */
		long dif = outTime.getTime() - inTime.getTime();
		
		/** Cast the long to a double */
		diff = (double)dif;
		
		/** Calculate the hours from milliseconds */
		hr = diff/ (1000 * 60 * 60 );
		
		/** Cast the hours to a string and return hours */
		String hour = String.format(Locale.US, "%.2f", hr);
		return hour;
	}

}