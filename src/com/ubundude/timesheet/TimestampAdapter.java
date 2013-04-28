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

// TODO Write JavaDoc's
/**
* @author Kolby Cansler
* @version 1.0.3.B1
* 
* Builds a custom adapter to bind database information to a custom listview 
*/
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.listview_timestamp, null);
			
		TextView projectShort = (TextView)vi.findViewById(R.id.projectShortTextView);
		TextView projectFull = (TextView)vi.findViewById(R.id.projectFullTextView);
		TextView stampId = (TextView)vi.findViewById(R.id.listviewId);
		TextView hoursEdit = (TextView)vi.findViewById(R.id.listviewHoursTV);
		TextView projectId = (TextView)vi.findViewById(R.id.projectIdTV);
		
		HashMap<String, String> timestamp = new HashMap<String, String>();
		timestamp = data.get(position);
		
		stampId.setText(timestamp.get(MainActivity.KEY_ID));
		projectShort.setText(timestamp.get(MainActivity.KEY_SHORT));
		projectFull.setText(timestamp.get(MainActivity.KEY_FULL));
		hoursEdit.setText(timestamp.get(MainActivity.KEY_HOURS) + " hrs");
		projectId.setText(timestamp.get(MainActivity.KEY_PROID));
		
	return vi;
	}
	
}
