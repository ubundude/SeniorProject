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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Kolby Cansler
 * @version 1.0.3.B4
 *
 * Class to define the Timestamp table and methods
 * relating to creating and updating that table
 */
public class TimestampTable {

	/** Define Database Constants */
	public static final String TABLE_TIMESTAMP = "timestamp";
	public static final String TABLE_PROJECTS = "projects";
	public static final String PROJECT_ID = "COLUMN_PROJECT_ID";
	public static final String COLUMN_TIMESTAMP_ID = "_id";
	public static final String COLUMN_TIME_IN = "time_in";
	public static final String COLUMN_DATE_IN = "date_in";
	public static final String COLUMN_TIME_OUT = "time_out";
	public static final String COLUMN_DATE_OUT = "date_out";
	public static final String COLUMN_YEAR = "year";
	public static final String COLUMN_MONTH = "month";
	public static final String COLUMN_WEEK_IN_YEAR = "week_year";
	public static final String COLUMN_COMMENTS = "comments";
	public static final String COLUMN_PROJECT = "project";
	public static final String COLUMN_HOURS = "hours";

	/** String to create Timestamp table */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TIMESTAMP
			+ "("
			+ COLUMN_TIMESTAMP_ID + " integer primary key autoincrement, "
			+ COLUMN_DATE_IN + " text not null, "
			+ COLUMN_TIME_IN + " text not null, "
			+ COLUMN_DATE_OUT + " text not null, "
			+ COLUMN_TIME_OUT + " text not null, "
			+ COLUMN_WEEK_IN_YEAR + " text not null, "
			+ COLUMN_YEAR + " text not null, "
			+ COLUMN_MONTH + " text not null, "
			+ COLUMN_COMMENTS + " text, "
			+ COLUMN_HOURS + " text not null, "
			+ COLUMN_PROJECT + " integer,"
			+ " FOREIGN KEY (" + COLUMN_PROJECT + ") REFERENCES "
			+ TABLE_PROJECTS + "(" + PROJECT_ID + "));";

	/** Method Called to create the Database Table Timestamp */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	/** Method called to update the Database table Timestamp */
	//TODO Table should backup before updating and restore after
	public static void onUpdate(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TimestampTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMESTAMP);
		onCreate(database);
	}
}