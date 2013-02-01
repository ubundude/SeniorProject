/**
 * @author Kolby Canlser
 * @version 1.0.ALPHA_008
 * 
 *  Creates the Project Editor page
 */

package com.kolbycansler.timesheet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/*
 * TODO Need Delete button
 */

/** 
 * ProjectEditorActivity class
 * 
 * Class that creates a layout and defines logic for creating, editing,
 * and deleting projects in the projects table.
 */
public class ProjectEditorActivity extends Activity {
	/** Get an instance of the Projects Data Source */
	ProjectsDataSource proDS = new ProjectsDataSource(this);
	public EditText shortCodeEdit, fullNameEdit, rateEdit, descEdit;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_project);
        
        
	}
	
	/**
	 * Method that is called when the Save Button is pressed
	 * 
	 * @param v Gets the current veiw
	 */
	public void saveHandler(View v) {
		/** Variables to store form elements into for insertion to databae */
		final String shortCode, fullName, rate, description;

		/** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)findViewById(R.id.rateEditText);
		descEdit = (EditText)findViewById(R.id.descriptionEditText);
		
		/** Get the text from the EditTexts, convert to strings, and store the values */
		shortCode = shortCodeEdit.getText().toString();
		fullName = fullNameEdit.getText().toString();
		rate = rateEdit.getText().toString();
		description = descEdit.getText().toString();
	
		/** Open the datasource, insert the values, and close the datasouce */
		proDS.open();
		try {
			proDS.createProject(fullName, shortCode, rate, description);
		} catch (Exception ex) {
			Log.d("projectSaveFail", ex.getMessage(), ex.fillInStackTrace());
		}
		proDS.close();
		
		/** Return to the previous activity */
		finish();
	}

	/**
	 * Handler for the Cancel Button
	 * 
	 * @param v Gets the current View
	 */
	public void cancelButtonHandler(View v){
		finish();
	}
}
