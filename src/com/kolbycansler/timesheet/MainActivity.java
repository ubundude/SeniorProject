/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA
 * 
 * Implements the Main layout class.
 */

package com.kolbycansler.timesheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
 * TODO Logic for plus and minus date buttons
 * TODO Fix timestamps not displaying - test the date format being passed
 * TODO Figure out what else needs done :p
 */

public class MainActivity extends Activity { 
	private TimestampDataSource dataSource;
	Calendar c = Calendar.getInstance();
	public String dateViewForm = "EEE\nMM/dd/yyyy";
	public String dateForm = "M/dd/yyyy";
	public String date, dateView;
	TimestampDataSource timeDS = new TimestampDataSource(this);
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dataSource = new TimestampDataSource(this);
        dataSource.open();
        
        SimpleDateFormat formDateView = new SimpleDateFormat(dateViewForm, Locale.US);
        SimpleDateFormat formDate = new SimpleDateFormat(dateForm, Locale.US);
        dateView = formDateView.format(c.getTime());
    	date = formDate.format(c.getTime());
        
        List<Timestamp> values = dataSource.getAllTimestamps(date); 
        
        ListView listView = (ListView)findViewById(R.id.timestampListView);
        listView.setAdapter(new TimestampAdapter(this, values));
        
        //Button quickAdd = (Button) findViewById(R.id.quickAddButton);
        //Button addNew = (Button) findViewById(R.id.addNewButton);
       
        EditText dateEditText = (EditText)findViewById(R.id.dateEditText);
        dateEditText.setText(dateView, TextView.BufferType.NORMAL);
    }
    
    public void addNewHandler(View view) {
    	Intent intent = new Intent(this, TimestampEditorActivity.class);
    	startActivity(intent);
    }
    
    public void quickAddHandler(View view) {
    	String timeIn, timeOut, dateIn, dateOut, comment;
    	int project = 0;
    	comment = null;
    	timeIn = Integer.toString(Calendar.HOUR_OF_DAY) + ":" + Integer.toString(Calendar.MINUTE);
    	timeOut = timeIn;
    	dateIn = Integer.toString(Calendar.MONTH) + "/" + Integer.toString(Calendar.DAY_OF_MONTH) + "/" + Integer.toString(Calendar.YEAR);
    	dateOut = dateIn;
    	
    	timeDS.open();
    	try {
    	timeDS.createTimestamp(dateIn, timeIn, dateOut, timeOut, comment, project);
    	} catch (Exception ex) {
    		Log.d("QuickAddFail", ex.getMessage(), ex.fillInStackTrace());
    	}
    	timeDS.close();
    }

    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
