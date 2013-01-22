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
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
 * TODO Date Class to get date for page and select
 * TODO Logic for plus and minus date buttons
 * TODO Quick Add button logic
 * TODO Figure out what else needs done :p
 */

public class MainActivity extends Activity { 
	private TimestampDataSource dataSource;
	public Calendar c = Calendar.getInstance();
	public String date, dateView;
	public String format = "EEE\nMM/dd/yyyy";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        SimpleDateFormat form = new SimpleDateFormat(format);
    	dateView = form.format(c.getTime());
        
        dataSource = new TimestampDataSource(this);
        dataSource.open();
        
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
    	//TODO Implement logic to insert a basic entry into timestamp with the current time
    }

    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
