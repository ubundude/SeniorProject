package com.ubundude.timesheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

//TODO Make the Date display the day - See ReportFragment
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
	public String weekInMonthForm = "ww";
	public String monthNumForm = "MM";
	public String yearForm = "yy";
	/** Strings to store formated calendar outputs */
	public String date, dateView;
	/** Prepares buttons and EditText for use */
	public Button minusButton, plusButton, quickAdd, addNew;
	public EditText dateEditText;
	public TextView hoursTextView;
	private int proId;
	/** Formatters for the dates */
	SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
	SimpleDateFormat formTime = new SimpleDateFormat(timeForm, Locale.US);
	SimpleDateFormat formWIM = new SimpleDateFormat(weekInMonthForm, Locale.US);
	SimpleDateFormat formYear = new SimpleDateFormat(yearForm, Locale.US);
	SimpleDateFormat formMonthNum = new SimpleDateFormat(monthNumForm, Locale.US);
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
		public void dateSetter(String date, int frag);
		public void mTotalSetter(String total);
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
		hoursTextView = (TextView)getView().findViewById(R.id.hoursTextView);
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
				Log.d("QuickAddOnClick", "Loading Display Dialog");
				displayDialog();
			}

		});

		addNew = (Button)getView().findViewById(R.id.addNewButton);
		addNew.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addNewHandler(v);

			}
		});
	}

	protected void getDailyTimestamps(String date) {
		Log.d("Get Timestamps", "Running");
		mCallback.dateSetter(date, 0);
	}

	/** 
	 * Method to get the current date form
	 * 
	 * Displays the formatted dates in the proper places 
	 * and makes them available for usage elsewhere.
	 * 
	 * @return date The current date formatted for SQL queries.
	 */
	private String initialDates() {
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

		//dateEditText = (EditText)getView().findViewById(R.id.dateEditText);
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

		//dateEditText = (EditText)getView().findViewById(R.id.dateEditText);
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
	private void quickAddHandler(int proId) throws SQLException {
		Log.d("QuickAdd", "Got project id: " + proId);
		/** Strings and int for the current dates and project */
		String timeIn, timeOut, dateIn, dateOut, wim, month, year;
		timeIn = formTime.format(c.getTime());
		timeOut = timeIn;
		dateIn = formDate.format(c.getTime());
		dateOut = dateIn;
		wim = formWIM.format(c.getTime());
		month = formMonthNum.format(c.getTime());
		year = formYear.format(c.getTime());

		/** Open Database for writing */
		db = dbHelp.getWritableDatabase();
		Log.d("QuickAdd", "Database Opened");
		/** String to insert a timestamp into the database */
		String insertSQL = "insert into timestamp (date_in, time_in, date_out, time_out, week_year, year, month, hours, project) " +
				"values('" + dateIn + "', '" + timeIn + "', '" + dateOut + "', '" + timeOut 
				+ "', '" + wim + "', '" + year +"', '" + month + "', 0.00, '" + proId + "')";
		try {
			db.execSQL(insertSQL);
			Log.d("QuickAdd", "SQL Inserted");
		} catch(Exception e) {
			Log.d("save Fail", e.getLocalizedMessage(), e.fillInStackTrace());
		}

		/** Close the Database */
		db.close();
		Log.d("QuickAdd", "Database Closed");
		Log.d("QuickAdd", "Exiting");
		getDailyTimestamps(date);
	}

	private void displayDialog() {
		/** Open the database table for reading and writing */
		db = dbHelp.getReadableDatabase();

		/** Select statement to get data needed for the list view */
		String getProjects = "select _id, name from projects";

		/** Open a cursor and store the return of the query */
		Cursor cu = db.rawQuery(getProjects, null);

		CharSequence[] projects = new CharSequence[cu.getCount()];
		final int[] IDs = new int[cu.getCount()];

		/** Make sure cursor is not null */
		if(cu != null && cu.getCount() > 0){
			cu.moveToFirst();
			//TODO should not display <NEW>
			do{
				projects[cu.getPosition()] = cu.getString(1);
				IDs[cu.getPosition()] = cu.getInt(0);
			} while (cu.moveToNext());
		}
		/** Close the cursor and database */
		cu.close();
		db.close();

		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		build.setTitle("Choose Project");
		build.setItems(projects, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				proId = IDs[which];
				quickAddHandler(proId);
			}
		});

		AlertDialog diag = build.create();
		diag.show();
	}
	private void updateLabel() throws ParseException {
		dateEditText.setText(formDate.format(c.getTime()));
	}
	
	public void setTotal(String total) {
		hoursTextView.setText(total);
	}

}
