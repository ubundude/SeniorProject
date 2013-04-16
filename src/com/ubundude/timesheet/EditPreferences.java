package com.ubundude.timesheet;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class EditPreferences extends PreferenceActivity {
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp = new TimesheetDatabaseHelper(this);
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preference_activity);
	
	    ListPreference projectsList = (ListPreference)findPreference(getString(R.string.perfProjectKey));
	    /** Open the database table for reading and writing */
	    db = dbHelp.getReadableDatabase();
	    String getProjects = "select _id, name from projects;";
	
	    Cursor cu = db.rawQuery(getProjects, null);
	
	    List<String> entries = new ArrayList<String>();
	    List<String> entryValues = new ArrayList<String>();
	
	    if(cu != null && cu.getCount() > 0){
	        cu.moveToFirst();
	
	        do {
	            entries.add(cu.getString(1));
	            entryValues.add(Integer.toString(cu.getInt(0)));
	        } while (cu.moveToNext());
	
	    }
	    cu.close();
	    db.close();
	
	    final CharSequence[] entryCharSeq = entries.toArray(new CharSequence[entries.size()]);
	    final CharSequence[] entryValsChar = entryValues.toArray(new CharSequence[entryValues.size()]);
	
	    projectsList.setEntries(entryCharSeq);
	    projectsList.setEntryValues(entryValsChar);
	
	}
}