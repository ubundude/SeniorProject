/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.ALPHA
 * 
 * Implements the Main layout class.
 */

package com.kolbycansler.timesheet;

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
	
	public String date; //Date will be gotten from the date selected on layout
	
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
        //addNew.setOnClickListener(addNewHandler());
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
