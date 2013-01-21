package com.kolbycansler.timesheet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.Spinner;

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

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_timestamp);
        timeDS = new TimestampDataSource(this);
        
        //Spinner projectSpinner = (Spinner)findViewById(R.id.projectSpinner);
        
	}
	
        public void saveHandler(View view) {
        	String timeIn, dateIn, timeOut, dateOut, comments;
        	int project;
        	
        	Button dateInButton = (Button)findViewById(R.id.dateInButton);
            EditText timeInEditText = (EditText)findViewById(R.id.timeInEditText);
            Button dateOutButton = (Button)findViewById(R.id.dateOutButton);
            EditText timeOutEditText = (EditText)findViewById(R.id.timeOutEditText);
            EditText commentsEditText = (EditText)findViewById(R.id.commentsEditText);
            
        	dateIn = dateInButton.getText().toString();
        	timeIn = timeInEditText.getText().toString();
        	dateOut = dateOutButton.getText().toString();
        	timeOut = timeOutEditText.getText().toString();
        	comments = commentsEditText.getText().toString();
        	//TODO Implement spinner and get the id of the selected project
        	project = 1;
        	
        	timeDS.createTimestamp(dateIn, timeIn, dateOut, timeOut, comments, project);
        	
        	Intent intent = new Intent(this, MainActivity.class);
        	startActivity(intent);
        	
        }
}
