/**
 * @author Kolby Canlser
 * @version 1.0.ALPHA_010
 * 
 *  Creates the Project Editor page
 */

package com.ubundude.timesheet;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/*
 * TODO Spinner should load project ID i
 * TODO If project loaded from database, should update, not insert new
 */

/** 
 * ProjectEditorActivity class
 * 
 * Class that creates a layout and defines logic for creating, editing,
 * and deleting projects in the projects table.
 */
public class ProjectEditorActivity extends Activity {
	/** Get an instance of the Projects Data Source */
	private ProjectsDataSource proDS = new ProjectsDataSource(this);
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp = new TimesheetDatabaseHelper(this);
	private EditText shortCodeEdit, fullNameEdit, rateEdit, descEdit;
	private String project;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_project);
        
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	project = extras.getString("PROJECT_NAME");
        }
        
        Log.d("Project Is", "Project is" + project);
        if (!project.equals("<NEW>")) {
        	Log.d("Load Called", "Project loaded with id: " + project);
        	loadProject(project);
        }
	}
	
	/**
	 * Method that is called when the Save Button is pressed
	 * 
	 * @param v Gets the current veiw
	 */
	public void saveHandler(View v) {
		 /** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)findViewById(R.id.rateEditText);
		descEdit = (EditText)findViewById(R.id.descriptionEditText);
		
		/** Variables to store form elements into for insertion to databae */
		final String shortCode, fullName, rate, description;
		
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
	 * Method called to load project from database when ID is passed by intent
	 * 
	 * @param project The ID of the project selected from the spinner
	 */
	public void loadProject(String project) {
		 /** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)findViewById(R.id.rateEditText);
		descEdit = (EditText)findViewById(R.id.descriptionEditText);
		
		Log.d("Project Is", "Project is" + project);
		
		String selectProject = "select * from projects where name = '" + project + "'";
		db = dbHelp.getReadableDatabase();
		Cursor cu = db.rawQuery(selectProject, null);
		
		cu.moveToFirst();
		shortCodeEdit.setText(cu.getString(2));
		fullNameEdit.setText(cu.getString(1));
		rateEdit.setText(cu.getString(3));
		descEdit.setText(cu.getString(4));
		
		cu.close();
		db.close();
	}

	/**
	 * Handler for the Cancel Button
	 * 
	 * @param v Gets the current View
	 */
	public void cancelButtonHandler(View v){
		finish();
	}
	
	public void projectDeleteHandler(View v) {
		//TODO Write me
	}
}
