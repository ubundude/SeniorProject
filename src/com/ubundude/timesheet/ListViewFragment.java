package com.ubundude.timesheet;

import java.util.ArrayList;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//TODO Need to get times from database and add them together to display in the totals

public class ListViewFragment extends Fragment {
	OnDateGetListener mCallback;
	/** Database instance and call to Timesheet OpenHelper */
	private SQLiteDatabase db;
	private TimesheetDatabaseHelper dbHelp;
	public ListView list;
	/** Gets the custom adapter for the listview */
	TimestampAdapter adapter;
	private String lDate;
	private int lReport;

	public interface OnDateGetListener {
		public void dateGetter(String date, int reportType);
	}

	@Override
	public void onAttach(Activity act) {
		super.onAttach(act);
		Log.d("ListViewFragment", "Is Attached to Activity: " + act.toString());
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnDateGetListener) act;
		} catch (ClassCastException e) {
			throw new ClassCastException(act.toString()
					+ " must implement OnDateGetListener");
		}
		Bundle extras = getArguments();
		lDate = extras.getString(MainActivity.KEY_DATE);
		lReport = extras.getInt(MainActivity.KEY_REPORT);
		dbHelp = new TimesheetDatabaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_listview, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		switch(lReport){
		case 0:
			getDailyTimestamps(lDate);
			break;
		case 1:
			getWeeklyTimestamps(lDate);
			break;
		case 2:
			getMonthlyTimestamps(lDate);
			break;
		}
	}

	/**
	 *  Method to get timestamps from database
	 * 
	 * Method runs an SQL Query to get all timestamps for the given 
	 * date and displays them in a list view
	 * 
	 * @param date The date the user has selected
	 */
	public void getDailyTimestamps(String date) {
		final ArrayList<HashMap<String, String>> stampList = new ArrayList<HashMap<String, String>>();

		/** Open the database table for reading and writing */
		db = dbHelp.getReadableDatabase();

		/** Select statement to get data needed for the list view */
		String getTimestamps = "select ti._id, pr.name, pr.shortcode, ti.hours, ti.project "
				+ "from timestamp ti inner join projects pr "
				+ "where ti.project = pr._id and ti.date_in = '" + date + "'";

		/** Open a cursor and store the return of the query */
		Cursor cu = db.rawQuery(getTimestamps, null);

		/** Make sure cursor is not null */
		if(cu != null && cu.getCount() > 0){
			cu.moveToFirst();

			/**
			 * Put values for each cursor row into HashMap
			 * 
			 *  While the cursor has a row, get the data
			 *  and store it in a HashMap that is passed on the adapter.
			 */
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(MainActivity.KEY_ID, Integer.toString(cu.getInt(0)));
				map.put(MainActivity.KEY_SHORT, cu.getString(1));
				map.put(MainActivity.KEY_FULL, cu.getString(2));
				map.put(MainActivity.KEY_HOURS, cu.getString(3));
				map.put(MainActivity.KEY_PROID, Integer.toString(cu.getInt(4)));

				stampList.add(map);

			} while(cu.moveToNext());
		}

		/** Close the cursor and database */
		cu.close();
		db.close();

		/** Initialize the listview */
		list = (ListView)getActivity().findViewById(R.id.timestampListView);

		/** Initialize the adapter with the stamplist for */
		adapter = new TimestampAdapter(getActivity(), stampList);
		list.setAdapter(adapter);

		/** Method to handle clicked list view items */
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/** Get HashMap for current position */
				HashMap<String, String> test = new HashMap<String, String>();
				test = stampList.get(position);
				/** Store projectId and timestampId from the current HashMap */
				int proId = Integer.parseInt(test.get(MainActivity.KEY_PROID));
				int timeId = Integer.parseInt(test.get(MainActivity.KEY_ID));

				/** Intent to move to TimestampEditorActivity and pass values to load */
				Intent intent = new Intent(getActivity(), EditorActivity.class);
				intent.putExtra("TIMESTAMP_ID", timeId);
				intent.putExtra("PROJECT_ID", proId);
				startActivity(intent);
			}
		});

	}

	public void getWeeklyTimestamps(String week) {
		final ArrayList<HashMap<String, String>> stampList = new ArrayList<HashMap<String, String>>();

		/** Open the database table for reading and writing */
		db = dbHelp.getReadableDatabase();

		/** Select statement to get data needed for the list view */
		String getTimestamps = "select ti._id, pr.name, pr.shortcode, ti.hours, ti.project "
				+ "from timestamp ti inner join projects pr "
				+ "where ti.project = pr._id and ti.week_year = '" + week + "'";

		/** Open a cursor and store the return of the query */
		Cursor cu = db.rawQuery(getTimestamps, null);

		/** Make sure cursor is not null */
		if(cu != null && cu.getCount() > 0){
			cu.moveToFirst();

			/**
			 * Put values for each cursor row into HashMap
			 * 
			 *  While the cursor has a row, get the data
			 *  and store it in a HashMap that is passed on the adapter.
			 */
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(MainActivity.KEY_ID, Integer.toString(cu.getInt(0)));
				map.put(MainActivity.KEY_SHORT, cu.getString(1));
				map.put(MainActivity.KEY_FULL, cu.getString(2));
				map.put(MainActivity.KEY_HOURS, cu.getString(3));
				map.put(MainActivity.KEY_PROID, Integer.toString(cu.getInt(4)));

				stampList.add(map);

			} while(cu.moveToNext());
		}

		/** Close the cursor and database */
		cu.close();
		db.close();

		/** Initialize the listview */
		list = (ListView)getActivity().findViewById(R.id.timestampListView);

		/** Initialize the adapter with the stamplist for */
		adapter = new TimestampAdapter(getActivity(), stampList);
		list.setAdapter(adapter);

		/** Method to handle clicked list view items */
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/** Get HashMap for current position */
				HashMap<String, String> test = new HashMap<String, String>();
				test = stampList.get(position);
				/** Store projectId and timestampId from the current HashMap */
				int proId = Integer.parseInt(test.get(MainActivity.KEY_PROID));
				int timeId = Integer.parseInt(test.get(MainActivity.KEY_ID));

				/** Intent to move to TimestampEditorActivity and pass values to load */
				Intent intent = new Intent(getActivity(), EditorActivity.class);
				intent.putExtra("TIMESTAMP_ID", timeId);
				intent.putExtra("PROJECT_ID", proId);
				startActivity(intent);
			}
		});
	}

	public void getMonthlyTimestamps(String month) {
		final ArrayList<HashMap<String, String>> stampList = new ArrayList<HashMap<String, String>>();
		Log.d("Get Monthly Timestamps", "Running with month: " + month);
		/** Open the database table for reading and writing */
		db = dbHelp.getReadableDatabase();

		/** Select statement to get data needed for the list view */
		String getTimestamps = "select ti._id, pr.name, pr.shortcode, ti.hours, ti.project "
				+ "from timestamp ti inner join projects pr "
				+ "where ti.project = pr._id and ti.month = '" + month + "'";
		
		/** Open a cursor and store the return of the query */
		Cursor cu = db.rawQuery(getTimestamps, null);
		
		/** Make sure cursor is not null */
		if(cu != null && cu.getCount() > 0){
			cu.moveToFirst();

			/**
			 * Put values for each cursor row into HashMap
			 * 
			 *  While the cursor has a row, get the data
			 *  and store it in a HashMap that is passed on the adapter.
			 */
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(MainActivity.KEY_ID, Integer.toString(cu.getInt(0)));
				map.put(MainActivity.KEY_SHORT, cu.getString(1));
				map.put(MainActivity.KEY_FULL, cu.getString(2));
				map.put(MainActivity.KEY_HOURS, cu.getString(3));
				map.put(MainActivity.KEY_PROID, Integer.toString(cu.getInt(4)));

				stampList.add(map);

			} while(cu.moveToNext());
		}
		
		/** Close the cursor and database */
		cu.close();
		db.close();
		Log.d("Get Timestamps", "Stuff closed");
		/** Initialize the listview */
		list = (ListView)getActivity().findViewById(R.id.timestampListView);

		/** Initialize the adapter with the stamplist for */
		adapter = new TimestampAdapter(getActivity(), stampList);
		list.setAdapter(adapter);

		/** Method to handle clicked list view items */
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/** Get HashMap for current position */
				HashMap<String, String> test = new HashMap<String, String>();
				test = stampList.get(position);
				/** Store projectId and timestampId from the current HashMap */
				int proId = Integer.parseInt(test.get(MainActivity.KEY_PROID));
				int timeId = Integer.parseInt(test.get(MainActivity.KEY_ID));

				/** Intent to move to TimestampEditorActivity and pass values to load */
				Intent intent = new Intent(getActivity(), EditorActivity.class);
				intent.putExtra("TIMESTAMP_ID", timeId);
				intent.putExtra("PROJECT_ID", proId);
				startActivity(intent);
			}
		});
	}
}
