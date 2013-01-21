package com.kolbycansler.timesheet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/*
 * TODO Cancel button should return to TimestampEditor
 * TODO Need Delete button
 */

public class ProjectEditorActivity extends Activity {
	ProjectsDataSource proDS = new ProjectsDataSource(this);
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_project);
        
        
	}
	
	public void saveHandler(View v) {
		String shortCode, fullName, rate, description;

		
		EditText shortCodeEditText = (EditText)findViewById(R.id.shortCodeEditText);
		EditText fullNameEditText = (EditText)findViewById(R.id.fullNameEditText);
		EditText rateEditText = (EditText)findViewById(R.id.rateEditText);
		EditText descEditText = (EditText)findViewById(R.id.descriptionEditText);
		
		shortCode = shortCodeEditText.getText().toString();
		fullName = fullNameEditText.getText().toString();
		rate = rateEditText.getText().toString();
		description = descEditText.getText().toString();
		
		proDS.createProject(fullName, shortCode, rate, description);
		
		Intent intent = new Intent(this, TimestampEditorActivity.class);
    	startActivity(intent);
		
	}

}
