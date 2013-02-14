/**
 * @author Kolby Cansler
 * @version 1.0.ALPHA
 * 
 * Builds a custom adapter to 
 */

package com.ubundude.timesheet;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class TimestampAdapter extends BaseAdapter {
	Context context;
	private List<Timestamp> list;
	
	public TimestampAdapter(Context c, List<Timestamp> values) {
		c = context;
		list = values;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		TimestampHolder holder = null;
		
		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.listview_timestamp, parent, false);
			
			holder = new TimestampHolder();
			holder.projectShortTextView = (TextView)row.findViewById(R.id.projectShortTextView);
			holder.fullNameTextView = (TextView)row.findViewById(R.id.fullNameTextView);
			holder.minusImageButton = (ImageButton)row.findViewById(R.id.minusImageButton);
			holder.hoursEditText = (EditText)row.findViewById(R.id.hoursEditText);
			holder.plusImageButton = (ImageButton)row.findViewById(R.id.plusImageButton);
			
			row.setTag(holder);
		} else {
			holder = (TimestampHolder)row.getTag();
		}
		
		return row;
	}
	
	static class TimestampHolder {

		ImageButton plusImageButton;
		EditText hoursEditText;
		ImageButton minusImageButton;
		TextView fullNameTextView;
		TextView projectShortTextView;
	}
}
