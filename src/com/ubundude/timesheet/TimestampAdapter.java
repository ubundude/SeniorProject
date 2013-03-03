/**
 * @author Kolby Cansler
 * @version 1.0.ALPHA
 * 
 * Builds a custom adapter to 
 */

package com.ubundude.timesheet;

//TODO Write JavaDoc's

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class TimestampAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	
	public TimestampAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.listview_timestamp, null);
			
		TextView projectShort = (TextView)vi.findViewById(R.id.projectShortTextView);
		TextView projectFull = (TextView)vi.findViewById(R.id.projectFullTextView);
		TextView stampId = (TextView)vi.findViewById(R.id.listviewId);
		Button editButton = (Button)vi.findViewById(R.id.listviewEditButton);
		TextView hoursEdit = (TextView)vi.findViewById(R.id.listviewHoursTV);
		
		HashMap<String, String> timestamp = new HashMap<String, String>();
		timestamp = data.get(position);
		
		stampId.setText(timestamp.get(MainActivity.KEY_ID));
		projectShort.setText(timestamp.get(MainActivity.KEY_SHORT));
		projectFull.setText(timestamp.get(MainActivity.KEY_FULL));
		hoursEdit.setText(timestamp.get(MainActivity.KEY_HOURS) + " hrs");
		editButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.editClicked();
			}
		});

	return vi;
	}
	
}
