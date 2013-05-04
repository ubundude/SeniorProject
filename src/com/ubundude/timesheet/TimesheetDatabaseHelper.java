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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Kolby Cansler
 * @version 1.0.3.B4
 * 
 * Class for handling creation and updating of database
 * Also called to open new database instances
 */
public class TimesheetDatabaseHelper extends SQLiteOpenHelper {
	
	/** Create Global Variables for Database Name and Version */
	private static final String DATABASE_NAME = "timesheet.db";
	private static final int DATABASE_VERSION = 3;
	
	/** Sets the context of the database */
	public TimesheetDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** Method called during the creation of the database that creates each of the tables */
	@Override
	public void onCreate(SQLiteDatabase database) {
		ProjectsTable.onCreate(database);
		TimestampTable.onCreate(database);
	}

	/** Method called during an upgrade of the database
	 * for example, if the version number is increased 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		ProjectsTable.onUpdate(database, oldVersion, newVersion);
		TimestampTable.onUpdate(database, oldVersion, newVersion);
		
	}

}
