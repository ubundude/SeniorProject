package com.ubundude.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class EditorActivity extends FragmentActivity
	implements TimestampEditorFragment.OnSendTimestampId {
	
	private int timeId, proId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	Log.d("Timestamp ID's", "Got from bundle");
        	timeId = extras.getInt("TIMESTAMP_ID");
        	proId = extras.getInt("PROJECT_ID");
        } else {
        	Log.d("Timestamp ID's", "setting defaults");
        	timeId = 0;
        	proId = 1;
        }
        
        Log.d("OnCreate", "TimeId: " + timeId);
        Log.d("OnCreate", "ProId: " + proId);
        sendTimeId(timeId, proId);
        
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editor_preferences:
			startActivity(new Intent(this, EditPreferences.class));
			return(true);
		case R.id.delete_item:
			//deleteHandler(timeId);
			return(true);
		}
		return(super.onOptionsItemSelected(item));
	}

	@Override
	public void sendTimeId(int timeId, int proId) {
		TimestampEditorFragment timeFrag = (TimestampEditorFragment)getSupportFragmentManager().findFragmentById(R.id.editorFragment);
		if(timeId != 0) {
		timeFrag.getTimestamp(timeId);
		Log.d("SendTimeId", "Loaded the timestamp");
		Log.d("SendTimeId", "Sending proId: " + proId);
		timeFrag.loadSpinnerData(proId);
		} else {
			timeFrag.initializeButtons();
		}
	}
}
