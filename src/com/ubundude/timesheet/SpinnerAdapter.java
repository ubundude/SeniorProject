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
 * The SpinnerAdapter class is an extension of the BaseAdapter that implements a 
 * custom adapter for binding data to a Spinner. This class was created because
 * the simple spinner class included in Android did not do everything that I wanted.
 */
public class SpinnerAdapter extends BaseAdapter {
	/** Sets the context of the host activity */
	private Activity act;
	/** Creates an array list to store the data passed to the adapter */
	private ArrayList<HashMap<String, String>> data;
	/** Creates a new LayoutInflater to inflate the spinner view */
	private static LayoutInflater inflater = null;
	
	/**
	 * Constructor to bind passed data to each row instance of the spinner
	 * 
	 * @param a Gets the passed Activity context
	 * @param d Gets the passed data
	 */
	public SpinnerAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		act = a;
		data = d;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	/**
	 * Inflates the custom view and binds the data to it
	 * 
	 * @param position    The position of the item in the spinner list
	 * @param convertView If the view goes off screen, another can replace it
	 * @param parent      The parent of the View
	 * @return            The view
	 */
	private View getCustomView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		/** If not converting a view, inflate a spinner_row */
		if(convertView == null)
			vi = inflater.inflate(R.layout.spinner_row, null);
		
		/** Instantiates the TextView's for the view */
		TextView proName = (TextView)vi.findViewById(R.id.proNameTV);
		TextView proId = (TextView)vi.findViewById(R.id.proIdTV);
		
		/** Creates a new HashMap for the project and binds data at the given position to it */
		HashMap<String, String> project = new HashMap<String, String>();
		project = data.get(position);
		
		/** Sets the TextView's in the spinner row */
		proName.setText(project.get(TimestampEditorActivity.KEY_NAME));
		proId.setText(project.get(TimestampEditorActivity.PRO_ID_KEY));
		
		return vi;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


}