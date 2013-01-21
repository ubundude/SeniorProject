package com.kolbycansler.timesheet;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProjectsTable {

	/* Define Database Constants */
	public static final String TABLE_PROJECTS = "projects";
	public static final String COLUMN_PROJECT_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SHORTCODE = "shortcode";
	public static final String COLUMN_RATE = "rate";
	public static final String COLUMN_DESC = "description";
	
	/* Database Creation SQL Statement */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PROJECTS
			+ "("
			+ COLUMN_PROJECT_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_SHORTCODE + " text not null, "
			+ COLUMN_RATE + " text not null, "
			+ COLUMN_DESC + " text not null"
			+ ");";
	
	/* Create the Database Table Projects */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	/* Update to the new Database Version */
	public static void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(ProjectsTable.class.getName(), "Upgrading Database from version "
				+ oldVersion + " to " + newVersion 
				+", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
		onCreate(database);
	}
}
