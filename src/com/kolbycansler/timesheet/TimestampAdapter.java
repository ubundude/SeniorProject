/**
 * @author Kolby Cansler
 * @version 1.0.ALPHA
 * 
 * Builds a custom adapter to 
 */

package com.kolbycansler.timesheet;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class TimestampAdapter extends ArrayAdapter<TimestampBinder> {
	private final Context context;
	List<TimestampBinder> values = null;
	
	public TimestampAdapter(Context context, int layoutResourceId, List<TimestampBinder> data){
		super(context, layoutResourceId, data);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) 
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.listview_timestamp, parent, false);
		TextView shortTextView = (TextView) rowView.findViewById(R.id.projectShortTextView);
		EditText editText = (EditText) rowView.findViewById(R.id.projectFullTextView);
		TextView fullNameTextView = (TextView) rowView.findViewById(R.id.hoursTextView);
		
		
		return rowView;
	}
	
	
}
