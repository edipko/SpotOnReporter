package com.sor.applications.spotonreporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sor.beans.EventReport;
import com.sor.beans.User;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MainDBHelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 5;

	// Database Name
	private static final String DATABASE_NAME = "SORdb";

	private static String DB_PATH = null;
	private static SQLiteDatabase THE_DATABASE;

	@SuppressWarnings("unused")
	private static Context THE_CONTEXT = null;

	private static boolean isNewDatabase = false;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 * @return
	 */
	public MainDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		THE_CONTEXT = context;

		/*
		 * Construct the path to the database
		 */
		DB_PATH = context.getApplicationContext()
				.getDatabasePath(DATABASE_NAME).getAbsolutePath();

		// Log.d("Log", "Deleteing database");
		// THE_CONTEXT.deleteDatabase(DB_PATH);

		boolean db_exists = checkDataBase();
		if (db_exists) {
			// Do Nothing
			Log.d("Database Helper", "DB Exists");
			THE_DATABASE = getWritableDatabase();
			isNewDatabase = false;

			try {
				String projName = getProjName();
				Log.d("App Name", projName);
			} catch (java.lang.NullPointerException npe) {
				context.deleteDatabase("SORdb");
				isNewDatabase = true;
				THE_DATABASE = getWritableDatabase();
			}

		} else {
			/*
			 * Create the database
			 */
			Log.d("DATABASE_HELPER", "Creating Database 1");
			isNewDatabase = true;
			THE_DATABASE = getWritableDatabase();

		}

	}

	/**
	 * Check if the database already exist
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		Log.d("Debug", "Checking if DB exists");
		File dbFile = new File(DB_PATH);
		return dbFile.exists();
	}

	public void openDataBase() throws SQLException {
		Log.d("DB_HELPER", "openDataBase");
		// Open the database
		String myPath = DB_PATH;
		THE_DATABASE = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
		// THE_DATABASE = getWritableDatabase();

	}

	@Override
	public synchronized void close() {

		if (THE_DATABASE != null)
			THE_DATABASE.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("Database Helper", "onCreate");

		/*
		 * Create a table to store access and authentication information
		 */
		String CREATE_SORACCESS_TABLE = "CREATE TABLE " + "SORACCESS" + "("
				+ "_id" + " INTEGER PRIMARY KEY" + "," + "username" + " TEXT"
				+ "," + "password" + " TEXT" + "," + "uuid" + " TEXT" + ","
				+ "projectname" + " TEXT" + "," + "projectid" + " INTEGER"
				+ "," + "permissionLevel" + " INTEGER" + "," + "orgid"
				+ " INTEGER" + "," + "pattern" + " TEXT" + ")";
		db.execSQL(CREATE_SORACCESS_TABLE);

		/*
		 * Create a table to store the reports in
		 */
		String query = "CREATE TABLE " + "SORREPORTS" + "(" + "_id"
				+ " INTEGER PRIMARY KEY" + "," + "name" + " TEXT" + ","
				+ "severity" + " TEXT" + "," + "type" + " TEXT" + ","
				+ "description" + " TEXT" + "," + "latitude" + " REAL" + ","
				+ "longitude" + " REAL" + "," + "disposition" + " TEXT" + ","
				+ "filepaths" + " TEXT" + ")";
		db.execSQL(query);

		/*
		 * Create a table to store the filenames of the files to upload
		 */
		query = "CREATE TABLE " + "FILENAMES" + "(_id" + " INTEGER PRIMARY KEY"
				+ "," + "report_id " + "INTEGER" + "," + " filepath" + " TEXT"
				+ ")";
		db.execSQL(query);

		isNewDatabase = true;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("Database", "Upgrading Database from ver:" + oldVersion
				+ " to ver:" + newVersion);

		String query = null;
		switch (oldVersion) {
		case 2:
			query = "DROP TABLE SORREPORTS;";
			db.execSQL(query);
			/*
			 * Create a table to store the reports in
			 */
			query = "CREATE TABLE " + "SORREPORTS" + "(" + "_id"
					+ " INTEGER PRIMARY KEY" + "," + "name" + " TEXT" + ","
					+ "severity" + " TEXT" + "," + "type" + " TEXT" + ","
					+ "description" + " TEXT" + "," + "latitude" + " REAL"
					+ "," + "longitude" + " REAL" + "," + "disposition"
					+ " TEXT" + "," + "filepaths" + " TEXT" + ")";
			db.execSQL(query);

			break;

		case 3:
			Log.d("MainDBHelper", "DB Upgrade #3");

			query = "DROP TABLE SORREPORTS;";
			db.execSQL(query);
			/*
			 * Create a table to store the reports in
			 */
			query = "CREATE TABLE " + "SORREPORTS" + "(" + "_id"
					+ " INTEGER PRIMARY KEY" + "," + "name" + " TEXT" + ","
					+ "severity" + " TEXT" + "," + "type" + " TEXT" + ","
					+ "description" + " TEXT" + "," + "latitude" + " REAL"
					+ "," + "longitude" + " REAL" + "," + "disposition"
					+ " TEXT" + "," + "filepaths" + " TEXT" + ")";
			db.execSQL(query);

			/*
			 * Create a table to store the filenames of the files to upload
			 */
			query = "DROP TABLE SORREPORTS;";
			db.execSQL(query);
			/*
			 * Create a table to store the reports in
			 */
			query = "CREATE TABLE " + "SORREPORTS" + "(" + "_id"
					+ " INTEGER PRIMARY KEY" + "," + "name" + " TEXT" + ","
					+ "severity" + " TEXT" + "," + "type" + " TEXT" + ","
					+ "description" + " TEXT" + "," + "latitude" + " REAL"
					+ "," + "longitude" + " REAL" + "," + "disposition"
					+ " TEXT" + "," + "filepaths" + " TEXT" + ")";
			db.execSQL(query);

			break;
		case 4:
			query = "DROP TABLE SORREPORTS;";
			db.execSQL(query);
			/*
			 * Create a table to store the reports in
			 */
			query = "CREATE TABLE " + "SORREPORTS" + "(" + "_id"
					+ " INTEGER PRIMARY KEY" + "," + "name" + " TEXT" + ","
					+ "severity" + " TEXT" + "," + "type" + " TEXT" + ","
					+ "description" + " TEXT" + "," + "latitude" + " REAL"
					+ "," + "longitude" + " REAL" + "," + "disposition"
					+ " TEXT" + "," + "filepaths" + " TEXT" + ")";
			db.execSQL(query);

			break;
		}

	}

	/*
	 * Insert SOR data into the tables
	 */
	public void insertData(User user, String password) {

		String insertRecord = "INSERT INTO SORACCESS VALUES(" + "null" + ","
				+ "'" + user.getUsername() + "','" + password + "','"
				+ user.getUuid() + "','" + user.getProjName() + "',"
				+ user.getProjectID() + "," + user.getPermissionLevel() + ","
				+ user.getOrgID() + "," + "null" + ");";

		THE_DATABASE.execSQL(insertRecord);
	}

	public void insertPattern(char[] pattern) {
		String updateRecord = "UPDATE SORACCESS SET pattern = '"
				+ pattern.toString() + "';";
		THE_DATABASE.execSQL(updateRecord);
	}

	public char[] getPattern() {
		openDataBase();
		String result = null;
		String query = "SELECT pattern FROM SORACCESS;";
		Cursor cursor = (THE_DATABASE).rawQuery(query, null);
		while (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		return result.toCharArray();
	}

	public String getProjName() {
		String result = null;

		String query = "SELECT projectname from SORACCESS;";
		Cursor cursor = (THE_DATABASE).rawQuery(query, null);
		while (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		return result;
	}

	public boolean isNewDatabase() {
		return isNewDatabase;
	}

	public void setNewDatabase(boolean isNewDatabase) {
		MainDBHelper.isNewDatabase = isNewDatabase;
	}

	public User getUserInfo() {
		Log.d("DB Access", "Getting User information");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null,
				SQLiteDatabase.OPEN_READWRITE);
		String query = "SELECT username, password, uuid, projectid from SORACCESS;";
		Cursor cursor = (db).rawQuery(query, null);
		User user = new User();
		while (cursor.moveToNext()) {
			user.setUsername(cursor.getString(0));
			user.setPassword(cursor.getString(1));
			user.setUuid(cursor.getString(2));
			user.setProjectID(cursor.getInt(3));
		}
		cursor.close();
		db.close();
		return user;
	}

	public List<EventReport> checkForEvents() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null,
				SQLiteDatabase.OPEN_READWRITE);
		String query = "SELECT _id, name, severity, type, description, latitude, longitude, disposition, filepaths FROM SORREPORTS;";
		Cursor cursor = (db).rawQuery(query, null);
		List<EventReport> eventReportList = new ArrayList<EventReport>();

		while (cursor.moveToNext()) {
			ArrayList<String> fpArray = new ArrayList<String>();

			try {

				String jsonFilePaths = cursor.getString(8);
				JSONObject jsonObject = new JSONObject(jsonFilePaths);
				JSONArray files = (JSONArray) jsonObject.get("files");

				if (files != null) {
					int len = files.length();
					for (int i = 0; i < len; i++) {
						fpArray.add(files.get(i).toString());
					}
				}
			} catch (Exception ex) {
				Log.d("MainDBHelper", "Error converting JSONArray/JSONObject. "
						+ ex.getLocalizedMessage());
			}

			EventReport er = new EventReport();
			er.setId(cursor.getInt(0));
			er.setName(cursor.getString(1));
			er.setSeverity(cursor.getString(2));
			er.setType(cursor.getString(3));
			er.setDescription(cursor.getString(4));
			er.setLat(cursor.getDouble(5));
			er.setLon(cursor.getDouble(6));
			er.setDisposition(cursor.getString(7));
			er.setFilePaths(fpArray);
			eventReportList.add(er);
		}
		cursor.close();
		db.close();
		return eventReportList;

	}

	public static boolean deleteEvent(int id) {

		Log.d("DB Access", "deleteEvent id: " + id);
		try {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);

			String query = "DELETE FROM SORREPORTS WHERE _id=" + id + ";";

			db.execSQL(query);
			db.close();

			return true;
		} catch (Exception ex) {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			String query = "SELECT id FROM SORREPORTS;";
			Cursor cursor = (db).rawQuery(query, null);
			while (cursor.moveToNext()) {
				Log.d("MainDBHelper", "Deleting id: " + cursor.getInt(0));
			}
			cursor.close();
			db.close();

			return false;
		}
	}

}