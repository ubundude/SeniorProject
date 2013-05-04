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
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Kolby Cansler
 * @version 1.0.3.B4
 * 
 * The TimestampAdapter class is an extension of the BaseAdapter that implements a 
 * custom adapter for binding data to a ListView. This class was created because
 * the Simple ListView class included in Android did not do everything that I wanted.
 */
public class TimestampAdapter extends BaseAdapter {
	/** Sets the context of the host activity */
	private Activity activity;
	/** Creates an array list to store the data passed to the adapter */
	private ArrayList<HashMap<String, String>> data;
	/** Creates a new LayoutInflater to inflate the spinner view */
	private static LayoutInflater inflater = null;

	/**
	 * Constructor to bind passed data to each row instance of the ListView
	 * 
	 * @param a Gets the passed Activity context
	 * @param d Gets the passed data
	 */
	public TimestampAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/** Method to get the count of rows */
	public int getCount() {
		return data.size();
	}

	/** Method to get the item at position position
	 * 
	 * @param positon The position of the current list item
	 */
	public Object getItem(int position) {
		return data.get(position);
	}

	/**
	 * Method to get the ID of the item at position position
	 * 
	 * @param position The position of the current list item
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Inflates the custom view and binds the data to it
	 * 
	 * @param position    The position of the item in the spinner list
	 * @param convertView If the view goes off screen, another can replace it
	 * @param parent      The parent of the View
	 * @return            The view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		/** If not converting a view, inflate a spinner_row */
		if(convertView==null)
			vi = inflater.inflate(R.layout.listview_timestamp, null);

		/** Instantiates the TextView's for the view */
		TextView projectShort = (TextView)vi.findViewById(R.id.projectShortTextView);
		TextView projectFull = (TextView)vi.findViewById(R.id.projectFullTextView);
		TextView stampId = (TextView)vi.findViewById(R.id.listviewId);
		TextView hoursEdit = (TextView)vi.findViewById(R.id.listviewHoursTV);
		TextView projectId = (TextView)vi.findViewById(R.id.projectIdTV);

		/** Creates a new HashMap for the timestamp and binds data at the given position to it */
		HashMap<String, String> timestamp = new HashMap<String, String>();
		timestamp = data.get(position);

		/** Sets the TextView's in the spinner row */
		stampId.setText(timestamp.get(MainActivity.KEY_ID));
		projectShort.setText(timestamp.get(MainActivity.KEY_SHORT));
		projectFull.setText(timestamp.get(MainActivity.KEY_FULL));
		hoursEdit.setText(timestamp.get(MainActivity.KEY_HOURS) + " hrs");
		projectId.setText(timestamp.get(MainActivity.KEY_PROID));

		return vi;
	}

}