package com.ubundude.timesheet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/** 
 * ProjectEditorActivity class
 * 
 * Class that creates a layout and defines logic for creating, editing,
 * and deleting projects in the projects table.
 */
public class ProjectEditorFragment extends Fragment {
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp;
	private EditText shortCodeEdit, fullNameEdit, rateEdit, descEdit;
	private int project;
	private Button saveButton, cancelButton, deleteButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_editor_project, container, false);
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		saveButton = (Button)getView().findViewById(R.id.saveProjectButton);
        cancelButton = (Button)getView().findViewById(R.id.cancelProjectButton);
        deleteButton = (Button)getView().findViewById(R.id.projectDeleteButton);
        
        if (project != 1) {
        	loadProject(project);
        } 
        
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
        
        deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				projectDeleteHandler(v, project);
			}
		});
		
	}
	
	
	/**
	 * Method called to load project from database when ID is passed by intent
	 * 
	 * @param project The ID of the project selected from the spinner
	 */
	public void loadProject(int project) {
		/** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)getView().findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)getView().findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)getView().findViewById(R.id.rateEditText);
		descEdit = (EditText)getView().findViewById(R.id.descriptionEditText);
		
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
	
	public void updateProject(int projectId) {
		/** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)getView().findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)getView().findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)getView().findViewById(R.id.rateEditText);
		descEdit = (EditText)getView().findViewById(R.id.descriptionEditText);
		
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
		
		//TODO See if this works
		getActivity().finish();
		
	}
	
	/**
	 * Method that is called when the Save Button is pressed
	 * <p>
	 * Only called if a new project needs to be inserted. 
	 * Otherwise, project gets updated via updateProject()
	 */
	public void insertNewProject() {
		 /** Initializes the edit text fields for use */
		shortCodeEdit = (EditText)getView().findViewById(R.id.shortCodeEditText);
		fullNameEdit = (EditText)getView().findViewById(R.id.fullNameEditText);
		rateEdit = (EditText)getView().findViewById(R.id.rateEditText);
		descEdit = (EditText)getView().findViewById(R.id.descriptionEditText);
		
		/** Variables to store form elements into for insertion to database */
		final String shortCode, fullName, rate, description;
		
		/** Get the text from the EditTexts, convert to strings, and store the values */
		shortCode = shortCodeEdit.getText().toString();
		fullName = fullNameEdit.getText().toString();
		rate = rateEdit.getText().toString();
		description = descEdit.getText().toString();
		
		if (fullName.equals("") && shortCode.equals("")) {
			Toast.makeText(getActivity(), "Project is empty", Toast.LENGTH_LONG).show();
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
			
			/** Return to the previous activity */
			//TODO Figure this out
			//finish();
		}
	}
	
	/**
	 * Handler for the Cancel Button
	 * 
	 * @param v Gets the current View
	 */
	public void cancelButtonHandler(View v){
		//TODO Figure this out
		//finish();
	}
	
	public void projectDeleteHandler(View v, int projectId) {
		String deleteSQL = "delete from projects where _id = " + projectId;
		if(projectId != 1) {
			db = dbHelp.getWritableDatabase();
			db.execSQL(deleteSQL);
			db.close();
			//TODO Figure this out
			//finish();
		} else {
			Toast.makeText(getActivity(), "Cannot delete an empty project", Toast.LENGTH_LONG).show();
		}
	}

}
