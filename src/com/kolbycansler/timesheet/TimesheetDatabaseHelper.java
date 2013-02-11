package com.kolbycansler.timesheet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimesheetDatabaseHelper extends SQLiteOpenHelper {
	/*  */
	
	/* Create Global Variables for Database Name and Version */
	private static final String DATABASE_NAME = "timesheet.db";
	private static final int DATABASE_VERSION = 2;
	
	/* Sets the context of the database */
	public TimesheetDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* Method called during the creation of the database */
	@Override
	public void onCreate(SQLiteDatabase database) {
		ProjectsTable.onCreate(database);
		TimestampTable.onCreate(database);
		
	}

	/* Method called during an upgrade of the database
	 * for example, if the version number is increased 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		ProjectsTable.onUpdate(database, oldVersion, newVersion);
		TimestampTable.onUpdate(database, oldVersion, newVersion);
		
	}

}
