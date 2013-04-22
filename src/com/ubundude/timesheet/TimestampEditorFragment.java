/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.3.A1
 * 
 * Creates the Timestamp Editor Page
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
import android.database.Cursor;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * TimestampEditorActivity Class
 *
 * Creates the layout and logic needed for the user to interact
 * with the editor to add a new timestamp to the database.
 */
public class TimestampEditorFragment extends Fragment {
	OnSendTimestampId mCallback;
	public static final String KEY_NAME = "projectName";
	public static final String KEY_ID = "projectId";
	/** Datasources to get objects for using database tables */
	private TimesheetDatabaseHelper dbHelp; //TODO Needs the = new part added in the onAttach
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
	static Button editButton, saveButton, cancelButton, deleteButton;
	static Spinner projectSpinner;
	EditText commentsEditText, timeEditText;
	SpinnerAdapter adapter;
	/** Variable to store the value of the button calling the picker */
	private int fromWhere = 0;
	private int timeId, proId;
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
	
	public interface OnSendTimestampId {
		public void sendTimeId(int timeId, int proId);
	}
	
	@Override 
	public void onAttach(Activity act) {
		super.onAttach(act);
		
		try { 
			mCallback = (OnSendTimestampId) act;
		} catch (ClassCastException e) {
			throw new ClassCastException(act.toString() 
					+ " must implement OnSendTimestampId");
		}
				
		dbHelp = new TimesheetDatabaseHelper(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_editor_timestamp, container, false);
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		editButton = (Button)getView().findViewById(R.id.projectEditButton);
		editButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
	       		TextView idTv = (TextView)getView().findViewById(R.id.proIdTV);
	       		Log.d("Edit Button", "textView set");
	       		int proId = Integer.parseInt(idTv.getText().toString());
	       		Log.d("Edit Button", "Project id: " + proId);
				//FIXME editProject(proId);
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
					deleteHandler(v, timeId);
				}
			});
		}
	
	}

	public void initializeButtons() {
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
		
		loadSpinnerData(0);
	}
	
	/** Method to set the value of the button used to the date or time picked 
	 * @throws ParseException */
	public void updateLabel() throws ParseException {
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
	public void saveHandler(View v) throws ParseException {
		/** Initialize variables to store information from the form elements */
		String timeIn, dateIn, timeOut, dateOut, comments, hours;

    	/** Get and store form element values*/
    	dateIn = dateInButton.getText().toString();
    	timeIn = timeInButton.getText().toString();
    	dateOut = dateOutButton.getText().toString();
    	timeOut = timeOutButton.getText().toString();
    	comments = commentsEditText.getText().toString();
    	hours = timeCalc();
    	
    	TextView idTv = (TextView)getView().findViewById(R.id.proIdTV);
   		int proId = Integer.parseInt(idTv.getText().toString());
    	
    	if(proId == 1) {
    		Toast toast = Toast.makeText(getActivity(), "Please select a project", Toast.LENGTH_LONG);
			toast.show();
    	} else {
    		String insertSQL = "insert into timestamp (date_in, time_in, date_out, time_out, comments, hours, project) " +
				"values('" + dateIn + "', '" + timeIn + "', '" + dateOut +
				"', '" + timeOut + "', '" + comments + "', '" + hours + "', '" + proId + "')";
    		
    		db = dbHelp.getReadableDatabase();
    		
    		db.execSQL(insertSQL);
     
	        /** Close the database and return to the previous context */
	        db.close();
	        
	        getActivity().finish();
    	}
    }
	
	private void updateHandler(View v, int timeId) throws ParseException {
		String timeIn, dateIn, timeOut, dateOut, comments, hours;
		
    	/** Get and store form element values*/
    	dateIn = dateInButton.getText().toString();
    	timeIn = timeInButton.getText().toString();
    	dateOut = dateOutButton.getText().toString();
    	timeOut = timeOutButton.getText().toString();
    	comments = commentsEditText.getText().toString();
    	hours = timeCalc();
    	TextView idTv = (TextView)getView().findViewById(R.id.proIdTV);
   		int proId = Integer.parseInt(idTv.getText().toString());
    	
    	String updateSQL = "update timestamp " +
    			"set date_in='" + dateIn + "', time_in='" + timeIn
    			+ "', date_out='" + dateOut + "', time_out='" + timeOut
    			+ "', comments='" + comments + "', hours='" + hours 
    			+ "', project=" + proId + " where _id = " + timeId;
    	
    	db = dbHelp.getWritableDatabase();
    	db.execSQL(updateSQL);
    	db.close();
    	
    	getActivity().finish();
	}
	
	public void deleteHandler(View v, int timeId) {
   		String deleteSQL = "delete from timestamp where _id = " + timeId;
   		db = dbHelp.getWritableDatabase();
   		db.execSQL(deleteSQL);
   		db.close();
   		
   		getActivity().finish();
   	}
	
	public void loadSpinnerData(int proId) {
   		int pos = 0;
   		int id = proId;
   		projectSpinner = (Spinner)getView().findViewById(R.id.projectSpinner);
   		ArrayList<HashMap<String, String>> projects = new ArrayList<HashMap<String, String>>();
		
		String selectQuery = "select _id, name from projects";
		
		db = dbHelp.getReadableDatabase();
		
		Cursor cu = db.rawQuery(selectQuery, null);

		if (cu != null && cu.getCount() > 0) {
			cu.moveToFirst();
			do {
				Log.d("loadSpinnerData", "Hashing rows");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_NAME, cu.getString(1));
				map.put(KEY_ID, Integer.toString(cu.getInt(0)));
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
	
	public int getTimestamp(int timeId) throws NullPointerException {
   		int id = timeId;
   		
   		String selectTimestamp =" select * from timestamp where _id = " + id;
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
	
	public static String timeCalc() throws ParseException { //TODO Update to get values inside
   		String cdateIn, ctimeIn, cdateOut, ctimeOut;
 	  
 	    cdateIn = dateInButton.getText().toString();
 	    ctimeIn = timeInButton.getText().toString();
 	    cdateOut = dateOutButton.getText().toString();
   		ctimeOut = timeOutButton.getText().toString();
 	    
		double hr;
		double diff;
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
