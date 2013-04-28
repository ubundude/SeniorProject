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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

// TODO Finish Javadoc's - Variables and method internals
/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.3.A5
 *
 * TimestampEditorFragment Class
 *
 * Creates the layout and logic needed for the user to interact
 * with the editor to add a new timestamp to the database.
 */
public class TimestampEditorFragment extends Fragment {
	OnSendTimestampId mCallback;
	public static final String KEY_NAME = "projectName";
	public static final String TIME_ID_KEY = "timeId";
	public static final String PRO_ID_KEY = "projectId"; 
	/** Datasources to get objects for using database tables */
	private TimesheetDatabaseHelper dbHelp; 
	private SQLiteDatabase db;
	/** Gets a valid calendar instance */
	final Calendar c = Calendar.getInstance();
	/** Strings for correctly formating the date and time */
	private String dateForm = "MM/dd/yyyy";
	private String timeForm = "HH:mm";
	public String weekInYearForm = "ww";
	public String monthNumForm = "MM";
	public String yearForm = "yy";
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

	/** 
	 * Interface by which host activities can interact with the fragment
	 */
	public interface OnSendTimestampId {
		public void sendTimeId(int timeId, int proId);
	}

	/**
	 * What to do when the fragment is first attached to the activity
	 */
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);

		/** Make sure that the activity implements the public interface */
		try {
			mCallback = (OnSendTimestampId) act;
		} catch (ClassCastException e) {
			throw new ClassCastException(act.toString()
					+ " must implement OnSendTimestampId");
		}

		/** Get a valid instance of the DatabaseHelper */
		dbHelp = new TimesheetDatabaseHelper(getActivity());
		Bundle extras = getArguments();
        timeId = extras.getInt(TIME_ID_KEY);
        proId = extras.getInt(PRO_ID_KEY);
        setHasOptionsMenu(true);
	}

	/**
	 * After the fragment is attached, the view needs to be created
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_editor_timestamp, container, false);
		Log.d("TIMESTAMP onCreateView", "View is being inflated");
		return v;
	}

	/**
	 * After the view is created, Views need initialied and initial logic happens
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		Log.d("TIMESTAMP onStart", "Its started");
		if (timeId != 0)
			getTimestamp(timeId);
		else 
			initializeButtons();
		loadSpinnerData(proId);
		/** Initializes the edit button and sets the onClickListener*/
		editButton = (Button)getView().findViewById(R.id.projectEditButton);
		editButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/** If button is clicked, get the value from the textview */
				TextView idTv = (TextView)getView().findViewById(R.id.proIdTV);
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
				new TimePickerDialog(getActivity(), t,
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
				new TimePickerDialog(getActivity(), t,
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
				new DatePickerDialog(getActivity(), d,
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
				new DatePickerDialog(getActivity(), d,
						year, month, dayOfMonth).show();
				fromWhere = 4;
			}
		});

		/**
		 * Logic to handle clicking the Save button
		 * <p>
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
		 *
		 */
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				getActivity().finish();
			}
		});

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
	 * Create a fragment specific menu
	 */
	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.editor_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	/**
	 * Menu Click handler for the options menu created above
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editor_preferences:
			startActivity(new Intent(getActivity(), EditPreferences.class));
			return(true);
		case R.id.delete_item:
			deleteHandler(timeId);
			return(true);
		}
		return(super.onOptionsItemSelected(item));
	}

	/** 
	 * Method to initialize buttons
	 * 
	 * This method sets the values of the buttons to the current date 
	 * and time if a timestamp is not being loaded from the database.
	 */
	private void initializeButtons() {
		Log.d("TIMESTAMP FRAGMENT", "Initializing Buttons");
		/** Initialize buttons so that they can be set to the proper date */
		dateInButton = (Button)getView().findViewById(R.id.dateInButton);
		timeInButton = (Button)getView().findViewById(R.id.timeInButton);
		dateOutButton = (Button)getView().findViewById(R.id.dateOutButton);
		timeOutButton = (Button)getView().findViewById(R.id.timeOutButton);
		commentsEditText = (EditText)getView().findViewById(R.id.commentsEditText);
		saveButton = (Button)getView().findViewById(R.id.saveTimestampButton);
		cancelButton = (Button)getView().findViewById(R.id.cancelTimestampButton);
		deleteButton = (Button)getView().findViewById(R.id.timestampDeleteButton);
		timeEditText = (EditText)getView().findViewById(R.id.timeEditText);

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

	/** 
	 * Method to set the value of the button used to the date or time picked
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
	 * Method call to pass the project id to the Project Editor fragment
	 * 
	 * @param proId The project that is getting edited
	 */
	private void editProject(int proId) {
		((EditorActivity)getActivity()).setProject(proId);
	}

	/**
	 * Method to save a new timestamp in the database
	 * 
	 * @param v               The View of the cliked button
	 * @throws ParseException Thrown if problem parsing Integers to Strings
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
		
		//Log.d("Date in Cal", Date.toString(lc.getTime()));
		
		wiy = formWIM.format(lc.getTime());
		month = Integer.toString(lMonth);
		year = Integer.toString(lYear);

		TextView idTv = (TextView)getView().findViewById(R.id.proIdTV);
		int proId = Integer.parseInt(idTv.getText().toString());

		if(proId == 1) {
			Toast toast = Toast.makeText(getActivity(), "Please select a project", Toast.LENGTH_LONG);
			toast.show();
		} else {
			String insertSQL = "insert into timestamp (date_in, time_in, date_out, time_out, week_year, year, month, comments, hours, project) " +
					"values('" + dateIn + "', '" + timeIn + "', '" + dateOut + "', '" + timeOut + "', '" 
					+ wiy + "', '" + year + "', '" + month + "', '" + comments + "', '" + hours + "', '" + proId + "')";

			db = dbHelp.getReadableDatabase();

			db.execSQL(insertSQL);

			/** Close the database and return to the previous context */
			db.close();

			getActivity().finish();
		}
	}

	/**
	 * Method to update a timestamp in database based on changes from editor
	 * 
	 * @param v               The View from the click
	 * @param timeId          The Id of the timestamp to be updated
	 * @throws ParseException Thrown if problem parsing Integers to Strings
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
		TextView idTv = (TextView)getView().findViewById(R.id.proIdTV);
		int proId = Integer.parseInt(idTv.getText().toString());
		
		String[] arrDate = dateIn.split("/");
		int lMonth = Integer.parseInt(arrDate[0]) - 1;
		int lDay = Integer.parseInt(arrDate[1]);
		int lYear = Integer.parseInt(arrDate[2]);
		
		Calendar lc = Calendar.getInstance();
		lc.set(Calendar.YEAR, lYear);
		lc.set(Calendar.MONTH, lMonth);
		lc.set(Calendar.DAY_OF_MONTH, lDay);
		
		wiy = formWIM.format(lc.get(Calendar.WEEK_OF_YEAR));
		month = formMonthNum.format(lc.get(Calendar.MONTH));
		year = formYear.format(lc.get(Calendar.YEAR));

		String updateSQL = "update timestamp " +
				"set date_in='" + dateIn + "', time_in='" + timeIn
				+ "', date_out='" + dateOut + "', time_out='" + timeOut
				+ "', week_year='" + wiy + "', year='" + year + "', month='" + month
				+ "', comments='" + comments + "', hours='" + hours
				+ "', project=" + proId + " where _id = " + timeId;

		db = dbHelp.getWritableDatabase();
		db.execSQL(updateSQL);
		db.close();

		getActivity().finish();
	}

	/**
	 * Method to delete a project from the database
	 * 
	 * @param timeId The Id of the timestamp to delete
	 */
	public void deleteHandler(int timeId) {
		String deleteSQL = "delete from timestamp where _id = " + timeId;
		db = dbHelp.getWritableDatabase();
		db.execSQL(deleteSQL);
		db.close();

		getActivity().finish();
	}

	/**
	 * Gets all projects from database and loads to spinner
	 * 
	 * If a timestamp is being loaded from the database, this method gets the 
	 * project that corresponds to that timestamp and displays it in the spinner
	 * by default
	 * <p>
	 * If no timestamp is being loaded, then the project id will be the default
	 * project
	 * 
	 * @param proId The project Id being passed in
	 */
	private void loadSpinnerData(int proId) {
		Log.d("LoadSpinnerData", "Loading Spinner Data");
		int pos = 0;
		int id = proId;
		projectSpinner = (Spinner)getView().findViewById(R.id.projectSpinner);
		ArrayList<HashMap<String, String>> projects = new ArrayList<HashMap<String, String>>();

		String selectQuery = "select _id, name from projects";
		Log.d("LoadSpinnerData", "Opeining database");
		db = dbHelp.getReadableDatabase();
		Log.d("LoadSpinnerData", "Database Opened");

		Cursor cu = db.rawQuery(selectQuery, null);

		if (cu != null && cu.getCount() > 0) {
			cu.moveToFirst();
			do {
				Log.d("loadSpinnerData", "Hashing rows");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_NAME, cu.getString(1));
				map.put(PRO_ID_KEY, Integer.toString(cu.getInt(0)));
				if (cu.getInt(0) == id) {
					Log.d("loadSpinnerData", "Found Loaded project");
					pos = cu.getPosition();
				}
				projects.add(map);

			} while(cu.moveToNext());
		}
		Log.d("loadSpinnerData", "Closing things");
		cu.close();
		db.close();
		Log.d("loadSpinnerData", "Setting adapter");
		adapter = new SpinnerAdapter(getActivity(), projects);
		projectSpinner.setAdapter(adapter);
		Log.d("loadSpinnerData", "Adapter Set");
		Log.d("loadSpinnerData", "Setting position");
		projectSpinner.setSelection(pos);
		Log.d("loadSpinnerData", "Position Set");
	}

	/**
	 * Method called to get timestamp from database
	 * 
	 * This this method it call when a user clicks on a row from the ListView
	 * and returns the selected row from the databse. It also inializes the Views 
	 * used by the editor
	 * 
	 * @param timeId                The id of the timestamp to get 
	 * @return id                   Returns the Id of the timestamp for updating purposes
	 * @throws NullPointerException Problem with the query being empty
	 */
	public int getTimestamp(int timeId) throws NullPointerException {
		int id = timeId;
		Log.d("TIMESTAMP getTimestamp", "TimeId is:" + id);
		String selectTimestamp =" select _id, date_in, time_in, date_out, time_out, comments, hours from timestamp where _id = " + id;
		timeEditText = (EditText)getView().findViewById(R.id.timeEditText);
		dateInButton = (Button)getView().findViewById(R.id.dateInButton);
		timeInButton = (Button)getView().findViewById(R.id.timeInButton);
		dateOutButton = (Button)getView().findViewById(R.id.dateOutButton);
		timeOutButton = (Button)getView().findViewById(R.id.timeOutButton);
		commentsEditText = (EditText)getView().findViewById(R.id.commentsEditText);
		saveButton = (Button)getView().findViewById(R.id.saveTimestampButton);
		cancelButton = (Button)getView().findViewById(R.id.cancelTimestampButton);
		deleteButton = (Button)getView().findViewById(R.id.timestampDeleteButton);

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

	/** 
	 * Method to calculate the time covered
	 * 
	 * This method calculated the total time covered by both the time
	 * buttons and the date buttons and returns that time for use.
	 * 
	 * @throws ParseException There is a problem converting from strings to Dates
	 * @return hour           The hours calculated above
	 */
	private static String timeCalc() throws ParseException {
		/** Strings to store the current button values for the method */
		String cdateIn, ctimeIn, cdateOut, ctimeOut;
		
		/** Stores the button values as strings */
		cdateIn = dateInButton.getText().toString();
		ctimeIn = timeInButton.getText().toString();
		cdateOut = dateOutButton.getText().toString();
		ctimeOut = timeOutButton.getText().toString();

		double hr, diff;
		String in = cdateIn + " " + ctimeIn;
		String out = cdateOut + " " + ctimeOut;
		Date inTime, outTime;
		SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyy HH:mm", Locale.US);

		inTime = dateForm.parse(in);
		outTime = dateForm.parse(out);
		long dif = outTime.getTime() - inTime.getTime();
		diff = (double)dif;
		hr = diff/ (1000 * 60 * 60 );
		String hour = String.format(Locale.US, "%.2f", hr);
		return hour;
	}

}