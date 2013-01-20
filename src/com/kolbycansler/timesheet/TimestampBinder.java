package com.kolbycansler.timesheet;

public class TimestampBinder {

	public static String projectShortCode;
	public static String projectFullName;
	public static String hours;
	public TimestampBinder(){
		super();
	}
	
	public TimestampBinder(String projectShortCode, String projectFullName, String hours) {
		super();
		this.projectShortCode = projectShortCode;
		this.projectFullName = projectFullName;
		this.hours = hours;
	}
}
