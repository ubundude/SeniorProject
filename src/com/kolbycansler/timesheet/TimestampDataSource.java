package com.kolbycansler.timesheet;

import java.util.ArrayList;
import java.util.List;

//import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TimestampDataSource {
	
	/* Create variables for use in accessing Projects table  */
	private SQLiteDatabase database;
	private TimesheetDatabaseHelper dbHelper;
		/* Creates an array of the column names that can be used in place of 
		 * writing out all column names in a query
		 */
	private String[] allColumns = { TimestampTable.COLUMN_TIMESTAMP_ID,
			TimestampTable.COLUMN_TIME_IN, TimestampTable.COLUMN_DATE_IN, 
			TimestampTable.COLUMN_TIME_OUT, TimestampTable.COLUMN_DATE_OUT, TimestampTable.COLUMN_PROJECT };
	
	/*  */
	public TimestampDataSource(Context context) {
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
	
	/* Method to create new Timestamp entry */
	public Timestamp createTimestamp(String dateIn, String timeIn, String dateOut, String timeOut, String comments, int project) {
		/*ContentValues values = new ContentValues();
		values.put(TimestampTable.COLUMN_TIME_IN, dateIn);
		values.put(TimestampTable.COLUMN_DATE_IN, timeIn);
		values.put(TimestampTable.COLUMN_TIME_OUT, dateOut);
		values.put(TimestampTable.COLUMN_DATE_OUT, timeOut);
		values.put(TimestampTable.COLUMN_COMMENTS, comments);
		values.put(TimestampTable.COLUMN_PROJECT, project);

		long insertId = database.insert(TimestampTable.TABLE_TIMESTAMP, null, values);
		
		Cursor cursor = database.query(TimestampTable.TABLE_TIMESTAMP, allColumns, 
				TimestampTable.COLUMN_TIMESTAMP_ID + " = " + insertId, null, 
				null, null, null); */
    	
		String sql = "INSERT INTO " + TimestampTable.TABLE_TIMESTAMP + " VALUES(null, '" + dateIn +
				"', '" + timeIn + "', '" + dateOut + "', '" + timeOut + "', '" + comments +
				"', " + project + ")";
		
		try {
		database.execSQL(sql);
		} catch(Exception ex) {
			Log.d("dbexecfail", ex.getMessage(), ex.fillInStackTrace());
		}
		return null;
		
		/* cursor.moveToFirst();
		Timestamp newTimestamp = cursorToTimestamp(cursor);
		cursor.close(); */
		//return newTimestamp;
	
	}
	
	/* Method to delete a timestamp entry */
	public void deleteTimestamp(Timestamp timestamp) {
		long id = timestamp.getId();
		System.out.println("Timestamp deleted with id: " + id);
		database.delete(TimestampTable.TABLE_TIMESTAMP, TimestampTable.COLUMN_TIMESTAMP_ID
				+ " = " + id, null);
	}
	
	/* Method to load all timestamps into a list */
	public List<Timestamp> getAllTimestamps(String forDate) {
		List<Timestamp> timestamps = new ArrayList<Timestamp>();
		
		Cursor cursor = database.query(TimestampTable.TABLE_TIMESTAMP, allColumns, TimestampTable.COLUMN_DATE_IN + "==" + forDate, 
				null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Timestamp timestamp = cursorToTimestamp(cursor);
			timestamps.add(timestamp); // Refers to List<Timestamp> 
			cursor.moveToNext();
		}
		cursor.close();
		return timestamps;
	}
	
	private Timestamp cursorToTimestamp(Cursor cursor) {
		Timestamp timestamp = new Timestamp();
		timestamp.setId(cursor.getLong(0));
		timestamp.setTimestamp(cursor.getString(1));
		return timestamp;
	}

}
