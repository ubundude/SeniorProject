package com.kolbycansler.timesheet;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ProjectsDataSource {

	/* Create variables for use in accessing Projects table  */
	private SQLiteDatabase database;
	private TimesheetDatabaseHelper dbHelper;
		/* Creates an array of the column names that can be used in place of 
		 * writing out all column names in a query
		 */
	private String[] allColumns = { ProjectsTable.COLUMN_PROJECT_ID,
			ProjectsTable.COLUMN_NAME, ProjectsTable.COLUMN_SHORTCODE,
			ProjectsTable.COLUMN_RATE, ProjectsTable.COLUMN_DESC };
	
	/*  */
	public ProjectsDataSource(Context context) {
		dbHelper = new TimesheetDatabaseHelper(context);
	}
	
	/* Open the database to write to it */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	/* Close the database when done using it */
	public void close() {
		dbHelper.close();
	}
	
	/* Create a new database entry */
	public Project createProject(String name, String shortCode, String rate, String desc) {
		ContentValues values = new ContentValues(); // Set a new values array to store column values
		values.put(ProjectsTable.COLUMN_NAME, name); // Put Project Name into values array
		values.put(ProjectsTable.COLUMN_SHORTCODE, shortCode); // Put Project Short Code into values array
		values.put(ProjectsTable.COLUMN_RATE, rate); // Put Project Rate into values array
		values.put(ProjectsTable.COLUMN_DESC, desc); // Put Project Description into values array
		long insertId = database.insert(ProjectsTable.TABLE_PROJECTS, null, values); 
		Cursor cursor = database.query(ProjectsTable.TABLE_PROJECTS, allColumns, // Query table Projects, columns allColumns
				ProjectsTable.COLUMN_PROJECT_ID + " = " + insertId, null, null, null, null); 
		cursor.moveToFirst();
		Project newProject = cursorToProject(cursor);
		cursor.close();
		return newProject;
	}
	
	/* Create method to delete a project */
	public void deleteProject(Project project) {
		long id = project.getId();
		System.out.println("Project deleted with id: " + id);
		database.delete(ProjectsTable.TABLE_PROJECTS, ProjectsTable.COLUMN_PROJECT_ID 
				+ " = " + id, null);
	}

	/* Put all projects into a list */
	public List<Project> getAllProjects() {
		List<Project> projects = new ArrayList<Project>();
		
		Cursor cursor = database.query(ProjectsTable.TABLE_PROJECTS, allColumns, null,
				null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Project project = cursorToProject(cursor);
			projects.add(project);
			cursor.moveToNext();
		}
		cursor.close();
		return projects;
	}
	
	/*  Create method to move cursor to given project */
	private Project cursorToProject(Cursor cursor) {
		Project project = new Project();
		project.setId(cursor.getLong(0));
		project.setProject(cursor.getString(1));
		return project;
	}
	
	
}
