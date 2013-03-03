package com.ubundude.timesheet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.os.AsyncTask;

public class UpdateCheck extends AsyncTask<URL, Void, String> {
	
	@Override
	protected void onPreExecute() {
		
	}

	@Override
	protected String doInBackground(URL... params) {
		// TODO Auto-generated method stub
		String urlVersion = null;
		try {
		URL url = new URL("https://dl.dropbox.com/u/20328438/version.txt");
    	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			
    	urlVersion = in.readLine();
		in.close();
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		
		return urlVersion;
	}
	
	@Override 
	protected void onPostExecute(String urlVersion) {
		@SuppressWarnings("unused")
		String ver = urlVersion;
	}


}
