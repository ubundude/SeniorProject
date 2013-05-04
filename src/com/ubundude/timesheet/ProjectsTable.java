/** Copyright 2013 Kolby Cansler
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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
* @author Kolby Cansler
* @version 1.0.3.B4
*
* Class to define the projects table and methods
* relating to creating and updating that table
*/
public class ProjectsTable {

	/** Define Database Constants */
	public static final String TABLE_PROJECTS = "projects";
	public static final String COLUMN_PROJECT_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SHORTCODE = "shortcode";
	public static final String COLUMN_RATE = "rate";
	public static final String COLUMN_DESC = "description";
	
	/** String to create the table */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PROJECTS
			+ "("
			+ COLUMN_PROJECT_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null unique, "
			+ COLUMN_SHORTCODE + " text not null, "
			+ COLUMN_RATE + " text, "
			+ COLUMN_DESC + " text"
			+ ");";
	
	/** Method Called to create the Database Table projects */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		/** Adds a new project to the table on at creation time */
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, "<NEW>");
		values.put(COLUMN_SHORTCODE, "NEW");
		database.insert(TABLE_PROJECTS, null, values);
	}
	
	/** Method called to update the Database table Timestamp */
	public static void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(ProjectsTable.class.getName(), "Upgrading Database from version "
				+ oldVersion + " to " + newVersion 
				+", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
		onCreate(database);
	}
}
