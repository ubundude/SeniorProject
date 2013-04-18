package com.ubundude.timesheet;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class EditorActivity extends FragmentActivity implements TimestampEditorFragment.DataPullingInterface, 
	ProjectEditorFragment.OnItemSelectedListener {
	
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
        
        
	}

	
	@Override
	public void onProjectItemSelected(int id) {
		// TODO Auto-generated method stub
		
	}


	/** 
	 * @see com.ubundude.timesheet.TimestampEditorFragment.DataPullingInterface#getData()
	 */
	@Override
	public Bundle getData() {
		Bundle b = new Bundle();
		b.putInt("TIME_ID", timeId);
		b.putInt("PRO_ID", proId);
		Log.d("Bundle", "Sent");
		return b;
	}

}
