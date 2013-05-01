/**
 * 
 */
package com.ubundude.timesheet;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.bugsense.trace.BugSenseHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class MainActivity extends FragmentActivity 
implements MainUIFragment.OnDateSetListener, 
ListViewFragment.OnDateGetListener,
ReportFragment.OnReportsRunListener,
TabHost.OnTabChangeListener {

	String lDate;
	private TabHost mTabHost;
	private HashMap mapTabInfo = new HashMap();
	private TabInfo mLastTab = null;
	/** Keys for the HashMap used in the list view */
	public static final String KEY_ID = "listviewId";
	public static final String KEY_SHORT = "projectShortTextView";
	public static final String KEY_FULL = "projectFullTextView";
	public static final String KEY_HOURS = "listviewHoursTV";
	public static final String KEY_PROID = "projectIdTV";
	public static final String KEY_DATE = "dateKey";
	public static final String KEY_REPORT = "reportKey";

	private class TabInfo {
		private String tag;
		private Class clss;
		private Bundle args;
		private Fragment fragment;
		TabInfo(String tag, Class clss, Bundle args) {
			this.tag = tag;

			this.clss = clss;
			this.args = args;
		}
	}
	
	class TabFactory implements TabContentFactory {
		private final Context mContext;
		
		public TabFactory(Context context) {
		mContext = context;
		}
		
		public View createTabContent(String tag) {
		View v= new View(mContext);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(MainActivity.this, "8b04fe90");
		setContentView(R.layout.activity_main);

		/** Calls temporary method for checking updates
		 *
		 * Should be removed for actual production app*/

		try {
			updateCheck();
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		initialiseTabHost(savedInstanceState);
		if(savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	/**
	 * Checks for an updated version of the app
	 *
	 * If there is an updated version, an Alert dialog is displayed
	 * to advise the user to download the latest version
	 *
	 * @throws NameNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */

	@SuppressWarnings("deprecation")
	private void updateCheck() throws NameNotFoundException, IOException, InterruptedException, ExecutionException {
		/** String to store the version from the url */
		String urlVersion;
		/** Get instance of UpdateCheck.java and get the version returned from it */

		UpdateCheck check = new UpdateCheck();
		urlVersion = check.execute().get();

		PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		String packageVersion = pInfo.versionName;

		Log.d("Package Version", packageVersion);

		if (!urlVersion.equals(packageVersion)) {
			AlertDialog alert = new AlertDialog.Builder(this).create();
			alert.setTitle("Version Check");
			alert.setMessage("You're version is out of date. Please visit "
					+ "www.ubundude.com/p/beta.html to update to the latest version.");

			alert.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alert.show();
		}
	}



	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
		super.onSaveInstanceState(outState);
	}

	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Add"),
				(tabInfo = new TabInfo("Tab1", MainUIFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Report"),
				(tabInfo = new TabInfo("Tab2", ReportFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		this.onTabChanged("Tab1");
		mTabHost.setOnTabChangedListener(this);
	}

	private static void addTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state.  If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}

	public void onTabChanged(String tag) {
		TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent1, newTab.fragment, newTab.tag);
					//ft.add(R.id.realtabcontent2, ListViewFragment, "listview");
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_preferences:
			startActivity(new Intent(this, EditPreferences.class));
			return(true);
		}
		return(super.onOptionsItemSelected(item));
	}

	@Override
	public void dateSetter(String date, int frag) { // From MainUI
		Log.d("Date Setter", "From Main UI");
		int rType = 0;
		lDate = date;

		Log.d("dateSetter", "Frag is: " + frag);
		dateGetter(lDate, rType, frag);
	}

	public void dateGetter(String date, int rType, int frag) { //From ListView
		Log.d("dateGetter", "From ListView");
		Log.d("Date Getter", "Frag is: " + frag);
		ListViewFragment listFrag = (ListViewFragment)getSupportFragmentManager().findFragmentById(R.id.realtabcontent2);

		if(listFrag != null) {
			switch(rType) {
			case 0: 
				Log.d("dateGetter", "Getting daily timestamps");
				listFrag.getDailyTimestamps(date, frag);
				break;
			case 1: 
				Log.d("dateGetter", "Getting weekly timestamps");
				listFrag.getWeeklyTimestamps(date);	
				break;
			case 2: 
				Log.d("dateGetter", "Getting monthly timestamps");
				listFrag.getMonthlyTimestamps(date);	
			}
		} else {
			ListViewFragment nListFrag =  new ListViewFragment();
			Bundle args = new Bundle();
			args.putString(KEY_DATE, date);
			args.putInt(KEY_REPORT, rType);
			nListFrag.setArguments(args);

			FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
			trans.replace(R.id.realtabcontent2, nListFrag);
			trans.commit();
		}

	}


	@Override
	public void sendDate(String date, int reportType, int frag) { //From Report
		lDate = date;
		Log.d("dateSetter", "Date is: " + lDate);
		dateGetter(lDate, reportType, frag);
	}

	@Override
	public void mTotalSetter(String total) { // From MainUI
		MainUIFragment uiFrag = (MainUIFragment)getSupportFragmentManager().findFragmentById(R.id.realtabcontent1);
		uiFrag.setTotal(total);
	}

	@Override
	public void setTotal(String total, int frag) { // From ListViewFrag
		Log.d("Set Total", "Total: " + total);
		Log.d("Set Total", "Frag: " + frag);
		switch(frag){
		case 0:
			Log.d("Set Total", "In Case 0");
			mTotalSetter(total);
			break;
		case 1:
			Log.d("Set Total", "In Case 1");
			rTotalSetter(total);
			break;
		}
	}

	@Override
	public void rTotalSetter(String total) { // From Report
		ReportFragment rFrag = (ReportFragment)getSupportFragmentManager().findFragmentById(R.id.realtabcontent1);
		rFrag.setTotal(total);
	}


}
