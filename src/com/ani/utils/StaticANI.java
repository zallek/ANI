package com.ani.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class StaticANI {

	/***
	 * Folder PATHS
	 */
	public static final long baseInterval = 1000;
	public static final String BASES_DIR_PATH = "/data/data/com.example.blopdatabase/databases/";
	/**
	 * Bases Names
	 */

	public static final String LOCAL_DATA_BASE = "datatest.db";

	public static final SimpleDateFormat FORM_DATEFORMAT = new SimpleDateFormat(
			"yyyy/MM/dd", Locale.KOREA);
	public static final SimpleDateFormat FORM_TIMEFORMAT = new SimpleDateFormat(
			" hh:mm", Locale.KOREA);
	public static final SimpleDateFormat COMPLETE_TIMEFORMAT = new SimpleDateFormat(
			"yyyy/MM/dd hh:mm", Locale.KOREA);

	// Shared Preference
	public static final String SERVICE_LOGS_ACTIVATE = "servicelogbool";
	public static final String NOTIFICATION_TRAFFIC_ACTIVATE = "nottrafficbool";
	public static final String NOTIFICATION_BLACKLIST_ACTIVATE = "notblacklistbool";
}
