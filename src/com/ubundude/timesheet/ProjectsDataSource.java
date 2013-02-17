
package com.ubundude.timesheet;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ProjectsDataSource {

	/* Create variables for use in accessing Projects table  */
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelper;
		/* Creates an array of the column names that can be used in place of 
		 * writing out all column names in a query
		 */
	
	/*  */
	public ProjectsDataSource(Context context) {
		dbHelper = new TimesheetDatabaseHelper(context);
	}
	
	/* Open the db to write to it */
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	/* Close the db when done using it */
	public void close() {
		dbHelper.close();
	}
	
	/* Put all projects into a list */
	public List<String> getAllProjects() {
		List<String> projects = new ArrayList<String>();
		
		String selectQuery = "select * from projects";
		
		this.open();
		
		Cursor cu = db.rawQuery(selectQuery, null);

		if (cu.moveToFirst()) {
			do {
				projects.add(cu.getString(1));
			} while(cu.moveToNext());
		}
		
		cu.close();
		this.close();
		
		return projects;
	}
}
	
