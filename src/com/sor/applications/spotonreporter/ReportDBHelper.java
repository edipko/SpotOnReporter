package com.sor.applications.spotonreporter;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sor.beans.EventReport;

public class ReportDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "SORdb";
	private static String DB_PATH = null;
	@SuppressWarnings("unused")
	private static Context THE_CONTEXT = null;

	public ReportDBHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);

		THE_CONTEXT = context;

		/*
		 * Construct the path to the database
		 */
		DB_PATH = context.getApplicationContext()
				.getDatabasePath(DATABASE_NAME).getAbsolutePath();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}


	/*
	 * Method to insert a new report into the local database
	 */
	public static void insertEvent(EventReport er) {
		Log.d("DB Access", "Inserting Event Report");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null,
				SQLiteDatabase.OPEN_READWRITE);
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray(er.getFilePaths());
		try {
			jsonObj.put("files", jsonArray);
		} catch (Exception ex) {
			Log.e("ReportDBHelper", "Error creating JSON Object");
		}
		
		String insertRecord = "INSERT INTO SORREPORTS VALUES(" + "null" 
		        + "," + "'" + er.getName() + "'" 
				+ "," + "'" + er.getSeverity() + "'"
				+ "," + "'" + er.getType() + "'" 
				+ "," + "'" + er.getDescription() + "'" 
				+ "," + er.getLat()
				+ "," + er.getLon() 
				+ "," + "'" + er.getDisposition() + "'" 
				+ "," + "'" + jsonObj.toString() + "'" + ");";
		
		db.execSQL(insertRecord);
		db.close();
	}
	
	/*
	 * Method to insert a filename in the to local database
	 * 
	 */
	public static void insertFilname(String filename, UUID uuid) {
	Log.d("DB Access", "Inserting Event Report");
	SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null,
			SQLiteDatabase.OPEN_READWRITE);
	
	String insertRecord = "INSERT INTO FILENAMES VALUES("
	              + "'" + uuid.toString() + "'" 
			+ "," + "'" + filename + "'"
			+ ");";

	db.execSQL(insertRecord);	
	db.close();
	}
}






