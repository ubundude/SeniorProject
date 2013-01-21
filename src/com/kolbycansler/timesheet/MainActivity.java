/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA
 * 
 * Implements the Main layout class.
 */

package com.kolbycansler.timesheet;

import java.text.SimpleDateFormat;

/*
 * TODO Get date stuff working
 * TODO Quick Add button logic
 * TODO Figure out what else needs done :p
 */
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity { 
	private TimestampDataSource dataSource;
	Button quickAdd, addNew; 
	Date c;
	public String date;
	
	SimpleDateFormat formatter = new SimpleDateFormat("EEE");
	
	//date = formatter.format(c.getTime());
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dataSource = new TimestampDataSource(this);
        dataSource.open();
        List<Timestamp> values = dataSource.getAllTimestamps(date); 
        
        ListView listView = (ListView)findViewById(R.id.timestampListView);
        listView.setAdapter(new TimestampAdapter(this, values));
        
        quickAdd = (Button) findViewById(R.id.quickAddButton);
        addNew = (Button) findViewById(R.id.addNewButton);
        //quickAdd.setOnClickListener(quickAddHandler);
       
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
