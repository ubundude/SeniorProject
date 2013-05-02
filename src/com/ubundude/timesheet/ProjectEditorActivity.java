package com.ubundude.timesheet;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProjectEditorActivity extends Activity {

	public static final String PRO_KEY = "projectKey";
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp = new TimesheetDatabaseHelper(this);
	private EditText shortCodeEdit, fullNameEdit, rateEdit, descEdit;
	private int project;
	private Button saveButton, cancelButton, deleteButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor_project);
	
		Bundle extras = getIntent().getExtras();
		project = extras.getInt(PRO_KEY);
		
		 if (project != 1) {
	        	loadProject(project);
	        } 
		
		saveButton = (Button)findViewById(R.id.saveProjectButton);
        cancelButton = (Button)findViewById(R.id.cancelProjectButton);
        deleteButton = (Button)findViewById(R.id.projectDeleteButton);
        
        saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(project != 1) {
					updateProject(project);
				} else {
					insertNewProject();
				}
				
			}
		});
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelButtonHandler(v);
			}
		});
        
        if(android.os.Build.VERSION.RELEASE.startsWith("3.") ||
				android.os.Build.VERSION.RELEASE.startsWith("4.")) {
			deleteButton.setVisibility(View.GONE);
		} else {
	        deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteHandler(project);
				}
			});
		}
	}
	
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.editor_menu, menu);
        super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editor_preferences:
			startActivity(new Intent(this, EditPreferences.class));
			return(true);
		case R.id.delete_item:
			deleteHandler(project);
			return(true);
		}
		return(super.onOptionsItemSelected(item));
	}
	
	/**
	 * Method called to load project from database when ID is passed by intent
	 * 
	 * @param project The ID of the project selected from the spinner
	 */
	private void loadProject(int project) {
		/** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)findViewById(R.id.rateEditText);
		descEdit = (EditText)findViewById(R.id.descriptionEditText);
		
		String selectProject = "select * from projects where _id = " + project;
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
	
	private void updateProject(int projectId) {
		/** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)findViewById(R.id.rateEditText);
		descEdit = (EditText)findViewById(R.id.descriptionEditText);
		
		/** Variables to store form elements into for insertion to database */
		final String shortCode, fullName, rate, description;
		
		/** Get the text from the EditTexts, convert to strings, and store the values */
		shortCode = shortCodeEdit.getText().toString();
		fullName = fullNameEdit.getText().toString();
		rate = rateEdit.getText().toString();
		description = descEdit.getText().toString();
		
		String updateSQL = "update projects " +
				"set name='" + fullName + "', shortcode='" + shortCode + "', rate='" +
				rate + "', description='" +
				description + "' " +
				"where _id = " + projectId;
		
		db = dbHelp.getWritableDatabase();
		db.execSQL(updateSQL);
		db.close();
		
		finish();
		
	}
	
	/**
	 * Method that is called when the Save Button is pressed
	 * 
	 * Only called if a new project needs to be inserted. 
	 * Otherwise, project gets updated via updateProject()
	 */
	private void insertNewProject() {
		 /** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)findViewById(R.id.rateEditText);
		descEdit = (EditText)findViewById(R.id.descriptionEditText);
		
		/** Variables to store form elements into for insertion to database */
		final String shortCode, fullName, rate, description;
		
		/** Get the text from the EditTexts, convert to strings, and store the values */
		shortCode = shortCodeEdit.getText().toString();
		fullName = fullNameEdit.getText().toString();
		rate = rateEdit.getText().toString();
		description = descEdit.getText().toString();
		
		if (fullName.equals("") && shortCode.equals("")) {
			Toast.makeText(this, "Project is empty", Toast.LENGTH_LONG).show();
		} else {
			String insertString = "insert into projects (name, shortcode, rate, description) " +
					"values('" + fullName + "', '" + shortCode + "', '" + rate +
					"', '" + description + "')";
		
			db = dbHelp.getWritableDatabase();
			try {
				db.execSQL(insertString);
			} catch (Exception ex) {
				Log.d("projectSaveFail", ex.getMessage(), ex.fillInStackTrace());
			}
			db.close();
			
			/** Return to the Timestamp Editor */
			finish();
		}
	}
	
	/**
	 * Handler for the Cancel Button
	 * 
	 * @param v Gets the current View
	 */
	private void cancelButtonHandler(View v){
		finish();
	}
	
	private void deleteHandler(int projectId) {
		String deleteSQL = "delete from projects where _id = " + projectId;
		if(projectId != 1) {
			db = dbHelp.getWritableDatabase();
			db.execSQL(deleteSQL);
			db.close();
			finish();
		} else {
			Toast.makeText(this, "Cannot delete an empty project", Toast.LENGTH_LONG).show();
		}
	}
	
}
