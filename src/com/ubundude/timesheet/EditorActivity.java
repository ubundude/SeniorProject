package com.ubundude.timesheet;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class EditorActivity extends Activity implements ProjectEditorFragment.OnItemSelectedListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		
		
	}

	@Override
	public void onProjectItemSelected(int id) {
		// TODO Auto-generated method stub
		
	}

}
