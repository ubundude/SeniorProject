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

public class SpinnerAdapter extends BaseAdapter {
	private Activity act;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	
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

	private View getCustomView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if(convertView == null)
			vi = inflater.inflate(R.layout.spinner_row, null);
		
		TextView proName = (TextView)vi.findViewById(R.id.proNameTV);
		TextView proId = (TextView)vi.findViewById(R.id.proIdTV);
		
		HashMap<String, String> project = new HashMap<String, String>();
		project = data.get(position);
		
		proName.setText(project.get(TimestampEditorFragment.KEY_NAME));
		proId.setText(project.get(TimestampEditorFragment.KEY_ID));
		
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
