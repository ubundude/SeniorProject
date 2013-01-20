/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA
 * 
 * Implements the Main layout class.
 */

package com.kolbycansler.timesheet;

import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class MainActivity extends ListActivity { 
	private TimestampDataSource dataSource;
	Button quickAdd, addNew; //TODO Define quickAdd Button logic
	
	public String date; //Date will be gotten from the date selected on layout
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dataSource = new TimestampDataSource(this);
        dataSource.open();
        
     //  List<Timestamp> values = dataSource.getAllTimestamps(date); 
       
       //Use own Layout
       //ArrayAdapter<TimestampAdapter> adapter = new ArrayAdapter<TimestampAdapter>(this, 
    		//   R.layout.listview_timestamp);
       //setListAdapter(adapter);
       
       quickAdd = (Button) findViewById(R.id.quickAddButton);
       addNew = (Button) findViewById(R.id.addNewButton);
       //addNew.setOnClickListener(addNewHandler);
       
    }
    
    public void addNewHandler(View view) {
    	Intent intent = new Intent(this, TimestampEditorActivity.class);
    	startActivity(intent);
    }
  

      
    

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
