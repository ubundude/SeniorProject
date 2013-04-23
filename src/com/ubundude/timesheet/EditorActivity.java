package com.ubundude.timesheet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class EditorActivity extends FragmentActivity
	implements TimestampEditorFragment.OnSendTimestampId,
	ProjectEditorFragment.OnProjectNeedsEdited {
	
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
        	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    		proId = Integer.parseInt(sharedPref.getString("perf_default_project", "1"));
        }
        
        Log.d("OnCreate", "TimeId: " + timeId);
        Log.d("OnCreate", "ProId: " + proId);
        sendTimeId(timeId, proId);
        
	}

	@Override
	public void sendTimeId(int timeId, int proId) {
		TimestampEditorFragment timeFrag = (TimestampEditorFragment)getSupportFragmentManager().findFragmentById(R.id.editorFragments);
		
		if (timeFrag != null) {
			timeFrag.getTimestamp(timeId);
		} else {
			TimestampEditorFragment nTimeFrag = new TimestampEditorFragment();
			Bundle args = new Bundle();
			args.putInt(TimestampEditorFragment.TIME_ID_KEY, timeId);
			args.putInt(TimestampEditorFragment.PRO_ID_KEY, proId);
			nTimeFrag.setArguments(args);
			FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
			fragTrans.replace(R.id.editorFragments, nTimeFrag);
			fragTrans.commit();
		}
	}

	
	@Override
	public void setProject(int proId) {
		ProjectEditorFragment proFrag = new ProjectEditorFragment();
		Bundle args = new Bundle();
		args.putInt(ProjectEditorFragment.PRO_KEY, proId);
		proFrag.setArguments(args);
		FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
		fragTrans.replace(R.id.editorFragments, proFrag);
		fragTrans.addToBackStack(null);
		fragTrans.commit();
	}
	
}
