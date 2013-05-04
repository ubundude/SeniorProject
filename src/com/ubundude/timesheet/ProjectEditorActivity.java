/** 
 * Copyright 2013 Kolby Cansler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * @author Kolby Cansler
 * @version 1.0.3.B4
 * 
 * Project Editor Activity
 * 
 * Class to inflate the Project Editor Activity layout and handle all 
 * interaction with the activity
 */
public class ProjectEditorActivity extends Activity {
	/** Public Key for intent variable */
	public static final String PRO_KEY = "projectKey";
	/** Database instance and call to Timesheet OpenHelper */
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp = new TimesheetDatabaseHelper(this);
	/** Defines editText variables for use */
	private EditText shortCodeEdit, fullNameEdit, rateEdit, descEdit;
	/** Variable for storing the project ID */
	private int project;
	/** Defines Button Variables for use */
	private Button saveButton, cancelButton, deleteButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor_project);

		/** Gets the extras from the passed intent */
		Bundle extras = getIntent().getExtras();
		project = extras.getInt(PRO_KEY);

		/** If the passed project id is 1, then don't load anything, otherwise, load the project */
		if (project != 1) {
			loadProject(project);
		} 
		
		/** Initializes the buttons */
		saveButton = (Button)findViewById(R.id.saveProjectButton);
		cancelButton = (Button)findViewById(R.id.cancelProjectButton);

		/** Sets the OnClickListener for the Save Button */
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

		/** Sets the OnClickListener for the Cancel button */
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelButtonHandler(v);
			}
		});

		/**
		 * Checks the build number of the host android platform and if 
		 * 3.x or 4.x, hides the delete button in the layout as it will 
		 * be shown in the action bar, otherwise sets the Delete Button
		 * with an OnClickListener
		 */
		if(android.os.Build.VERSION.RELEASE.startsWith("3.") ||
				android.os.Build.VERSION.RELEASE.startsWith("4.")) {
			deleteButton.setVisibility(View.GONE);
		} else {
			deleteButton = (Button)findViewById(R.id.projectDeleteButton);
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

		/** SQL Query to get a project from the database */
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
	/**
	 * Method called to update a project in the database
	 * 
	 * @param projectId The Id of the project that should be updated
	 */
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

		/** SQL Query to update the current project */
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

		/** If name and short code are empty, then do nothing, otherwise insert everything */
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
	 * Returns to the previous activity
	 * @param v Gets the current View
	 */
	private void cancelButtonHandler(View v){
		finish();
	}

	/**
	 * If the delete button is clicked, deletes the project from the database
	 * 
	 * @param projectId gets the id of the project to be deleted from the database
	 */
	private void deleteHandler(int projectId) {
		/** SQL Query to delete the open project */
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
