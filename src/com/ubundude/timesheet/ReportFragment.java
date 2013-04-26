package com.ubundude.timesheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class ReportFragment extends Fragment {
	OnReportsRunListener mCallback;
	
	private Button plusButton, minusButton;
	private EditText dateEditText;
	Button reportChooser;
	/** Gets a valid calendar instance for use */
	final Calendar c = Calendar.getInstance();
	/** Strings for formatting the date's and times for use */
	public String dateForm = "MM/dd/yyyy";
	public String dayForm = "EEE";
	public String monthForm = "LLLL";
	public String weekInMonthForm = "ww";
	public String monthNumForm = "MM";
	public String yearForm = "yy";
	private int reportType = 0;
	Spinner rSpinner;
	private String date, dateView;
	SimpleDateFormat formDay = new SimpleDateFormat(dayForm, Locale.US);
	SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
	SimpleDateFormat formMonth = new SimpleDateFormat(monthForm, Locale.US);
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
	
	public interface OnReportsRunListener {
		public void sendDate(String date, int reportType);
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
		rSpinner = (Spinner)getView().findViewById(R.id.rSpinner);
		ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(getActivity(), R.array.reports, 
				android.R.layout.simple_spinner_item);
		
		adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rSpinner.setAdapter(adapt);
		rSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				switch (pos) {
				case 0: 
					date = formDate.format(c.getTime());
					dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
					dateEditText.setText(dateView);
					reportType = 0;
					break;
				case 1:
					date = formDate.format(c.getTime());
					dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
					dateEditText.setText(dateView);
					reportType = 1;
					break;
				case 2:
					date = formMonth.format(c.getTime());
					Log.d("Spinner Switch", "Date is: " + date);
					dateView = date;
					Log.d("Spinner Switch", "DateView is: " + dateView);
					dateEditText.setText(dateView);
					reportType = 2;
					break;
				}
			}
			
			public void onNothingSelected(AdapterView<?> parent){
				
			}
		});

		dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
		date = formDate.format(c.getTime());
		
		getTimestamps(date);
		
		dateEditText = (EditText)getView().findViewById(R.id.rDateEditText);
		dateEditText.setText(dateView);
		dateEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("Initial Dates", "In the On Click Listener");
				int year, month, dayOfMonth;
				String date = dateEditText.getText().toString();
				Log.d("Report DateEditText OnClick", "Date is: " + date);
				year = Integer.valueOf(date.substring(10));
				month = Integer.valueOf(date.substring(4, 6)) - 1;
				dayOfMonth = Integer.valueOf(date.substring(7, 9));
				new DatePickerDialog(getActivity(), d,
						year, month, dayOfMonth).show();
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
					date = minusButtonHandler();
					getTimestamps(date);
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
					date = plusButtonHandler();
					getTimestamps(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void getTimestamps(String date) {
		mCallback.sendDate(date, reportType);
		
	}

	/** Gets the next day and displays to dateEditText 
	 * @throws ParseException 
	 * @return date The current date formatted for SQL queries
	 */
    private String plusButtonHandler() throws ParseException {
    	
    	c.setTime(formDate.parse(date));
    	c.add(Calendar.DAY_OF_MONTH, 1);
    	
    	dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
    	date = formDate.format(c.getTime());
    	
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);

    	return date;
	}

    /** Gets the previous day and displays to dateEditText 
     * @throws ParseException
     * @return date The current date formatted for SQL queries
     */
	private String minusButtonHandler() throws ParseException {
		c.setTime(formDate.parse(date));
    	c.add(Calendar.DAY_OF_MONTH, -1);
    	dateView = formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime());
    	
    	date = formDate.format(c.getTime());
    	
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);
        
		return date;
	}

	private void updateLabel() throws ParseException {
		dateEditText.setText(formDay.format(c.getTime()) + "\n" + formDate.format(c.getTime()));
		date = formDate.format(c.getTime());
		getTimestamps(date);
	}

}
