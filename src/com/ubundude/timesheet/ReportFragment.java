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
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Kolby Cansler <kolby@ubundude.com>
 * @version 1.0.3.B4
 * 
 * Inflates the report fragment and handles all interaction with that fragment
 */
public class ReportFragment extends Fragment {
	OnReportsRunListener mCallback;
	
	private Button plusButton, minusButton;
	private EditText dateEditText;
	Button reportChooser;
	int year, month, dayOfMonth;
	/** Gets a valid calendar instance for use */
	final Calendar c = Calendar.getInstance();
	/** Strings for formatting the date's and times for use */
	public String dateForm = "MM/dd/yyyy";
	public String dayForm = "EEE";
	public String monthForm = "LLLL";
	public String weekInYearForm = "ww";
	public String monthNumForm = "MM";
	public String yearForm = "yy";
	int monthHelp;
	private int reportType = 0;
	Spinner rSpinner;
	TextView hoursTextView;
	private String[] arrDate;
	private String gDate, dateView, sendDate;
	private String firstDay;
	private int firstDOW;
	SimpleDateFormat formDay = new SimpleDateFormat(dayForm, Locale.US);
	SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
	SimpleDateFormat formMonth = new SimpleDateFormat(monthForm, Locale.US);
	SimpleDateFormat formWIY = new SimpleDateFormat(weekInYearForm, Locale.US);
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
			c.setFirstDayOfWeek(firstDOW);
			try {
				updateLabel();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	};
	
	public interface OnReportsRunListener {
		public void sendDate(String date, int reportType, int frag);
		public void rTotalSetter(String total);
	}
	
	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		
		try {
			mCallback = (OnReportsRunListener) act;
		} catch (ClassCastException e) {
			throw new ClassCastException(act.toString()
					+ " must implement OnDateSetListener");
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		/** Get the shared preference for the first day of the week */
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		firstDay = prefs.getString(getActivity().getString(R.string.prefDOWKey), "SUNDAY");
		
		firstDOW = getFirstDay(firstDay);
		
		hoursTextView = (TextView)getView().findViewById(R.id.rHoursTV);
		rSpinner = (Spinner)getView().findViewById(R.id.rSpinner);
		ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(getActivity(), R.array.reports, 
				android.R.layout.simple_spinner_item);
		
		adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rSpinner.setAdapter(adapt);
		rSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				switch (pos) {
				case 0: 
					sendDate = formDate.format(c.getTime());
					dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
					dateEditText.setText(dateView);
					reportType = 0;
					getTimestamps(sendDate, reportType);
					break;
				case 1:
					dateView = formWIY.format(c.getTime());
					sendDate = dateView;
					dateEditText.setText(dateView);
					reportType = 1;
					getTimestamps(sendDate, reportType);
					break;
				case 2:
					sendDate = formMonthNum.format(c.getTime());
					monthHelp = Integer.parseInt(sendDate) - 1;
					sendDate = Integer.toString(monthHelp);
					Log.d("Spinner Switch", "Date is: " + sendDate);
					dateView = formMonth.format(c.getTime());
					Log.d("Spinner Switch", "DateView is: " + dateView);
					dateEditText.setText(dateView);
					reportType = 2;
					getTimestamps(sendDate, reportType);
					break;
				}
			}
			
			public void onNothingSelected(AdapterView<?> parent){
				
			}
		});

		dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
		sendDate = formDate.format(c.getTime());
		gDate = sendDate;
		
		getTimestamps(sendDate, reportType);
		
		/** Instantiates the dateEditText, set's it's text to the dateView, and sets the onClickListener */
		dateEditText = (EditText)getView().findViewById(R.id.rDateEditText);
		dateEditText.setText(dateView);
		dateEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/** Gets the date based on the report type selected */
				switch(reportType) {
				case 0:
					Log.d("Initial Dates", "In the On Click Listener");
					arrDate = gDate.split("/");
					
					year = Integer.valueOf(arrDate[2]);
					month = Integer.valueOf(arrDate[0]) - 1;
					dayOfMonth = Integer.valueOf(arrDate[1]);
					new DatePickerDialog(getActivity(), d,
						year, month, dayOfMonth).show();
					
					break;
				case 1: 
					sendDate = dateEditText.getText().toString();
					arrDate = gDate.split("/");
					
					year = Integer.valueOf(arrDate[2]);
					month = Integer.valueOf(arrDate[0]) - 1;
					dayOfMonth = Integer.valueOf(arrDate[1]);
					new DatePickerDialog(getActivity(), d,
							year, month, dayOfMonth).show();
					break;
				case 2:
					pickMonth();
					break;
				}
			}
		});
		
		/**
         * Initialize minus button
         * 
         * This method calls the minus button handler and stores the date returned
         * and also gets new timestamps for the date returned. */
        minusButton = (Button)getView().findViewById(R.id.rMinusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
        	
        	@Override
			public void onClick(View v) {
				try {
					sendDate = minusButtonHandler();
					getTimestamps(sendDate, reportType);
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
        plusButton = (Button)getView().findViewById(R.id.rPlusButton);
        	plusButton.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
				try {
					sendDate = plusButtonHandler();
					getTimestamps(sendDate, reportType);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Method to get the Calendar day of week Integer
	 * 
	 * @param lFirstDay The day of the from the shared preferences
	 * @return DOW The integer for the day of the week
	 */
	private int getFirstDay(String lFirstDay) {
		int DOW = Calendar.SUNDAY ;
		
		if(lFirstDay == "MONDAY")
			DOW = Calendar.MONDAY;
		else if(lFirstDay == "TUESDAY")
			DOW = Calendar.TUESDAY;
		else if(lFirstDay == "WEDNESDAY")
			DOW = Calendar.WEDNESDAY;
		else if(lFirstDay == "THURSDAY")
			DOW = Calendar.THURSDAY;
		else if(lFirstDay == "FRIDAY")
			DOW = Calendar.FRIDAY;
		else if(lFirstDay == "SATURDAY")
			DOW = Calendar.SATURDAY;
		
		return DOW;
	}

	
	protected void getTimestamps(String date, int rType) {
		mCallback.sendDate(date, reportType, 1);
		
	}

	/** Gets the next day and displays to dateEditText 
	 * 
	 * @throws ParseException 
	 * @return date The current date formatted for SQL queries
	 */
    private String plusButtonHandler() throws ParseException {
    	
    	c.setTime(formDate.parse(gDate));
    	c.setFirstDayOfWeek(firstDOW);
    	switch (reportType) {
    	case 0: 
    		c.add(Calendar.DAY_OF_MONTH, 1);
        	dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
        	sendDate = formDate.format(c.getTime());
        	gDate = sendDate;
            dateEditText.setText(dateView, TextView.BufferType.NORMAL);
            break;
    	case 1:
    		c.add(Calendar.DAY_OF_MONTH, 7);
        	dateView = formWIY.format(c.getTime());
        	sendDate = formWIY.format(c.getTime());
        	gDate = formDate.format(c.getTime());
            dateEditText.setText(dateView, TextView.BufferType.NORMAL);
            break;
    	case 2:
    		c.add(Calendar.MONTH, 1);
    		dateView = formMonth.format(c.getTime());
    		sendDate = formMonthNum.format(c.getTime());
			monthHelp = Integer.parseInt(sendDate) - 1;
			sendDate = Integer.toString(monthHelp);
    		gDate = formDate.format(c.getTime());
    		dateEditText.setText(dateView, TextView.BufferType.NORMAL);
    		break;
    	}

    	return sendDate;
	}

    /** Gets the previous day and displays to dateEditText 
     * 
     * @throws ParseException
     * @return date The current date formatted for SQL queries
     */
	private String minusButtonHandler() throws ParseException {
		c.setTime(formDate.parse(gDate));
		c.setFirstDayOfWeek(firstDOW);
		Log.d("Minus Handler", "gDate: " + gDate);
    	switch (reportType) {
    	case 0: 
    		c.add(Calendar.DAY_OF_MONTH, -1);
        	dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
        	sendDate = formDate.format(c.getTime());
        	gDate = sendDate;
            dateEditText.setText(dateView, TextView.BufferType.NORMAL);
            break;
    	case 1:
    		c.add(Calendar.DAY_OF_MONTH, -7);
        	dateView = formWIY.format(c.getTime());
        	sendDate = formWIY.format(c.getTime());
        	gDate = formDate.format(c.getTime());
            dateEditText.setText(dateView, TextView.BufferType.NORMAL);
            break;
    	case 2:
    		c.add(Calendar.MONTH, -1);
    		dateView = formMonth.format(c.getTime());
    		sendDate = formMonthNum.format(c.getTime());
			monthHelp = Integer.parseInt(sendDate) - 1;
			sendDate = Integer.toString(monthHelp);
    		gDate = formDate.format(c.getTime());
    		dateEditText.setText(dateView, TextView.BufferType.NORMAL);
    		break;
    	}
        
		return sendDate;
	}

	/**
	 * Method called by the Date Picker to set the dateEditText to the date selected
	 * 
	 * @throws ParseException
	 */
	private void updateLabel() throws ParseException {
		
		switch (reportType) {
		case 0:
			dateEditText.setText(formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime()));
			sendDate = formDate.format(c.getTime());
			gDate = sendDate;
			getTimestamps(sendDate, reportType);
			break;
		case 1: 
			dateEditText.setText(formWIY.format(c.getTime()));
			sendDate = formWIY.format(c.getTime());
			gDate = formDate.format(c.getTime());
			getTimestamps(sendDate, reportType);
			break;
		}
	}
	
	/**
	 * Method to start a ListDialog that displays the months
	 */
	private void pickMonth() {
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		build.setTitle("Choose Month");
		build.setItems(R.array.months, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				month(which);
			}
		});

		AlertDialog diag = build.create();
		diag.show();
	}
	
	/**
	 * Method to get the month selected and pass it to the host Activity 
	 * 
	 * @param month The month selected in the date view 
	 */
	private void month(int month) {
		/** Creates an array of months */
		String[] months;
		
		/** Stores the array of months from the array resource */
		months = getResources().getStringArray(R.array.months);
		
		/** Get the month from the months array */
		String lMonth = months[month];
		
		/** Calculate a value for the Global gDate */
		gDate = Integer.toString(month + 1) + "/01/" + formYear.format(c.getTime());
	
		/** Store the month in sendDate */
		sendDate = Integer.toString(month);
		
		/** Set the dateEditText to lMonth */
		dateView = lMonth;
		dateEditText.setText(dateView);
		
		getTimestamps(sendDate, reportType);
	}

	/**
	 * Method called from the host activity to set the Hours Text View returned from the ListView
	 * 
	 * @param total The total hours represented by ListViewFragment
	 */
	public void setTotal(String total) {
		hoursTextView.setText(total);
	}
}
