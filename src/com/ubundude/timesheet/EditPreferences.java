/**
 * @author Kolby Cansler <golfguy90@gmail.com>
 * @version 1.0.2.A1
 * 
 * Creates the interface for managing preferences
 * and defaults for the app
 */

package com.ubundude.timesheet;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * EditPreferences
 * 
 * Inflates the preferences layout
 * @extends PreferenceActivity
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
	    ListPreference projectsList = (ListPreference)findPreference(getString(R.string.perfProjectKey));
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
	            entryValues.add(Integer.toString(cu.getInt(0)));
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