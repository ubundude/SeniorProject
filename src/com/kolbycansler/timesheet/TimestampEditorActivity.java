package com.kolbycansler.timesheet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

/* 
 * TODO Need way to populate from database
 * TODO Buttons should display current date or date loaded from database
 * TODO Buttons should popup a datePicker when clicked and set text to that 
 * TODO Cancel Button Should return to MainActivity
 * TODO Hours TextView should update to total time calulated from timeIn - timeOut
 * TODO Need delete button
 * TODO Figure out the spinner
 */


public class TimestampEditorActivity extends Activity {
	private TimestampDataSource timeDS;
	private ProjectsDataSource proDS;
	final Calendar c = Calendar.getInstance();
	String dateForm = "MM/dd/yyyy";
	String timeForm = "HH:mm";
	String timeIn, timeOut, dateIn, dateOut;
	static final int DATE_IN_DIALOG_ID = 0;
	static final int TIME_IN_DIALOG_ID = 0;
	static final int DATE_OUT_DIALOG_ID = 0;
	static final int TIME_OUT_DIALOG_ID = 0;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_timestamp);
        timeDS = new TimestampDataSource(this);
		
		Button dateInButton = (Button)findViewById(R.id.dateInButton);
	    Button timeInButton = (Button)findViewById(R.id.timeInButton);
	    Button dateOutButton = (Button)findViewById(R.id.dateOutButton);
	    Button timeOutButton = (Button)findViewById(R.id.timeOutButton);
		
		SimpleDateFormat dateFormer = new SimpleDateFormat(dateForm, Locale.US);
		SimpleDateFormat timeFormer = new SimpleDateFormat(timeForm, Locale.US);
		timeIn = timeFormer.format(c.getTime());
		timeOut = timeIn;
		dateIn = dateFormer.format(c.getTime());
		dateOut = dateIn;
		
		dateInButton.setText(dateIn);
		dateOutButton.setText(dateOut);
		timeInButton.setText(timeIn);
		timeOutButton.setText(timeOut);
        
        Spinner projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
        
        //loadSpinnerData();
        
	}
	
       public void saveHandler(View view) {
        	String timeIn, dateIn, timeOut, dateOut, comments;
        	int project;

        	Button dateInButton = (Button)findViewById(R.id.dateInButton);
            Button timeInButton = (Button)findViewById(R.id.timeInButton);
            Button dateOutButton = (Button)findViewById(R.id.dateOutButton);
            Button timeOutButton = (Button)findViewById(R.id.timeOutButton);
            EditText commentsEditText = (EditText)findViewById(R.id.commentsEditText);
            
        	dateIn = dateInButton.getText().toString();
        	timeIn = timeInButton.getText().toString();
        	dateOut = dateOutButton.getText().toString();
        	timeOut = timeOutButton.getText().toString();
        	comments = commentsEditText.getText().toString();
        	//TODO Implement spinners and get the id of the selected project
        	project = 1;
        	timeDS.open();
        	
        	try {
        	timeDS.createTimestamp(dateIn, timeIn, dateOut, timeOut, comments, project);
            } catch (Exception ex) {
            	Log.d("SaveFail", ex.getMessage(), ex.fillInStackTrace());
            } 
        	
        	timeDS.close();
        	finish();
        	
        }
       
       	public void editHandler(View v) {
       		Intent intent = new Intent(this, ProjectEditorActivity.class);
       		startActivity(intent);
       	}
       	
       	public void cancelHandler(View v) {
       		finish();
       	}
       	
       	/*
       public static class TimePickerFragement extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    	   @Override
    	   public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	   final Calendar c = Calendar.getInstance();
	    	   int hour = c.get(Calendar.HOUR_OF_DAY);
	    	   int minute = c.get(Calendar.MINUTE);
	    	   
	    	   return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    	   }

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
		}
    	 	
       }
       
       public void showTimePickerDialog(View v){
    	   DialogFragment  timeFrag = new TimePickerFragement();
    	   timeFrag.show(getFragmentManager(), "timePicker");
       }
       
       public void editButtonClicked() {
    	   //TODO Make logic!
       }
       */
       //TODO Fix spinner loading data
       
      /* private void loadSpinnerData() {
    	   List<Project> labels = proDS.getAllProjects();
    	   ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels);
    	   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	   projectSpinner.setAdapter(dataAdapter);
       }
       */
       
}
