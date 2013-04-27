package com.ubundude.timesheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ReplacementSpan;
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
 * @author kolby
 *
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
	private String[] arrDate;
	private String gDate, dateView, sendDate;
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
		
		dateEditText = (EditText)getView().findViewById(R.id.rDateEditText);
		dateEditText.setText(dateView);
		dateEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
	
	protected void getTimestamps(String date, int rType) {
		Log.d("getTimestamps", "Report type: " + reportType);
		mCallback.sendDate(date, reportType);
		
	}

	/** Gets the next day and displays to dateEditText 
	 * @throws ParseException 
	 * @return date The current date formatted for SQL queries
	 */
    private String plusButtonHandler() throws ParseException {
    	
    	c.setTime(formDate.parse(gDate));
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
     * @throws ParseException
     * @return date The current date formatted for SQL queries
     */
	private String minusButtonHandler() throws ParseException {
		c.setTime(formDate.parse(gDate));
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
	
	private void pickMonth() {
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		build.setTitle("Choose Project");
		build.setItems(R.array.months, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				month(which);
			}
		});

		AlertDialog diag = build.create();
		diag.show();
	}
	
	private void month(int month) {
		String[] months;
		months = getResources().getStringArray(R.array.months);
		String lMonth = months[month];
		gDate = Integer.toString(month + 1) + "/01/" + formYear.format(c.getTime());
		Log.d("Pick Date", "gDate: " + gDate);
		sendDate = Integer.toString(month);
		Log.d("PickDate", "sendDate: " + sendDate);
		dateView = lMonth;
		dateEditText.setText(dateView);
		getTimestamps(sendDate, reportType);
	}

}
