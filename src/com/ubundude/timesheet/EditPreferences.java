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

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.2.A1
 * 
 * EditPreferences
 * 
 * Inflates the preferences layout
 */
public class EditPreferences extends PreferenceActivity {
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp = new TimesheetDatabaseHelper(this);
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preference_activity);
	
	    /**
	     * Set ListPrecerence from database
	     * 
	     * Section to get all projects from the database 
	     * and hand them to the ListPreference as entries
	     */
	    ListPreference projectsList = (ListPreference)findPreference(getString(R.string.prefProjectKey));
	    /** Open the database table for reading and writing */
	    db = dbHelp.getReadableDatabase();
	    /** The string to get the projects from the database */
	    String getProjects = "select _id, name from projects;";
	
	    /** Run the select an store it in a cursor */
	    Cursor cu = db.rawQuery(getProjects, null);
	
	    /** The ArrayLists that store the entries and entryValues from the cursor */
	    List<String> entries = new ArrayList<String>();
	    List<String> entryValues = new ArrayList<String>();
	
	    if(cu != null && cu.getCount() > 0){
	        cu.moveToFirst();
	
	        /** For each entry in the cursor, set the array values */
	        do {
	            entries.add(cu.getString(1));
	            Log.d("Pref Getter", "Adding Project: " + cu.getString(1));
	            entryValues.add(Integer.toString(cu.getInt(0)));
	            Log.d("Pref Getter", "Adding ID: " + cu.getInt(0));
	        } while (cu.moveToNext());
	
	    }
	    cu.close();
	    db.close();
	
	    /** Convert the arrays to CharSequences for processing by the ListPreference's */
	    final CharSequence[] entryCharSeq = entries.toArray(new CharSequence[entries.size()]);
	    final CharSequence[] entryValsChar = entryValues.toArray(new CharSequence[entryValues.size()]);
	
	    /** Set the entries and values to the CharSequences */
	    projectsList.setEntries(entryCharSeq);
	    projectsList.setEntryValues(entryValsChar);
	
	}
}