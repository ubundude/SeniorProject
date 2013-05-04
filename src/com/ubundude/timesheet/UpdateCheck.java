/** Copyright 2013 Kolby Cansler
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.os.AsyncTask;
/**
 * @author Kolby Cansler
 * @version 1.0.3.B4
 * 
 * This is a temporary method to check the installed user version. It is only being used
 * in the beta version. It will be depreciated when the app is published to the Google 
 * Play Store
 */
public class UpdateCheck extends AsyncTask<URL, Void, String> {

	@Override
	protected void onPreExecute() {

	}

	
	@Override
	protected String doInBackground(URL... params) {
		/** The version of the app returned from the file */
		String urlVersion = null;
		try {
			/** The url to check for the latest published version */
			URL url = new URL("https://dl.dropbox.com/u/20328438/version.txt");
			
			/** Start a new buffered reader instance that reads first line of the input file */
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			urlVersion = in.readLine();
			in.close();
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		
		return urlVersion;
	}

	/** Send the urlVersion as a string back to the calling function */
	@Override
	protected void onPostExecute(String urlVersion) {
		@SuppressWarnings("unused")
		String ver = urlVersion;
	}


}