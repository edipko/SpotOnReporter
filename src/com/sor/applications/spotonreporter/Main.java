package com.sor.applications.spotonreporter;

import java.util.Iterator;
import java.util.List;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import com.sor.applications.spotonreporter.util.GetLocation;
import com.sor.beans.EventReport;
//import com.sor.beans.EventReportResponse;
import com.sor.beans.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;

public class Main extends Activity implements View.OnClickListener {
	
	private String username = null;
	private String password = null;
	@SuppressWarnings("unused")
	private User user = null;
	final String URL = "http://dev.spotonresponse.com:8080/sor/u/user";
	final String REPORT_URL = "http://dev.spotonresponse.com:8080/sor/r/reportevent";
	private static final int REQ_CREATE_PATTERN = 1;
	private static final int REQ_ENTER_PATTERN = 2;
	private Context context = null;
	private User userInfo = null;
	
	//private Button reportEventButton = null;
	private Button sorOnlineButton = null;
	private Button exitButton = null;
	private Button checkinButton = null;
	private Button watchReportButton = null;
	private Button problemReportButton = null;
	private Button emergencyReportButton = null;
	
	private EventReport eventReport = null;
	
	private MonitorDatabase mdb_task = new MonitorDatabase();
	volatile boolean running;
	
	/*
	 * Database
	 */
	MainDBHelper dbHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		setContentView(R.layout.activity_main_event);

		//reportEventButton = (Button) findViewById(R.id.reportEventButton);
		sorOnlineButton = (Button) findViewById(R.id.sorButton);
		exitButton = (Button) findViewById(R.id.exitButton);
		checkinButton = (Button) findViewById(R.id.CheckinButton);
		watchReportButton = (Button) findViewById(R.id.reportWatchButton);
		problemReportButton = (Button) findViewById(R.id.reportProblemButton);
		emergencyReportButton = (Button) findViewById(R.id.reportEmergencyButton);

		
		
		//reportEventButton.setOnClickListener(this);
		sorOnlineButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);
		
		checkinButton.setOnClickListener(this);
		watchReportButton.setOnClickListener(this);
		problemReportButton.setOnClickListener(this);
		emergencyReportButton.setOnClickListener(this);
	
		
		/*
		 * Determine if we have a database, if not - create one - authenticate
		 * to the server - gather program attributes to store in the database -
		 * create PIN entry screen to avoid authenticating each time the app
		 * starts
		 */

		dbHelper = new MainDBHelper(this);
		
		
		/*
		 * Check is this if the database was just created (First Run)
		 */
		if (dbHelper.isNewDatabase()) {
			Log.d("Main", "Is a new database");
			/*
			 * Since this is the first run... we need to get authentication
			 * information and then connect to the server and download app
			 * branding information
			 * 
			 * Ask for username and password
			 * 
			 */
            //reportEventButton.setEnabled(false);
			showLoginDialog();
					
		} else {
			/*
			 * Not a new database, so the user has already authenticated
			 * Ask for the lock pattern and then continue
			 * 
			 * First we need to get the lock pattern that was stored in the database
			 */
			Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null,
			        this, LockPatternActivity.class);

			startActivityForResult(intent, REQ_ENTER_PATTERN);	
		}
		
		
	}
	
	public void checkAuthentication(String Username, String Password) {
		/*
		 * Authenticate against SOR servers
		 */		
		User user = Authentication.authenticate(Username, Password, URL);
		
		Log.d("Test", "checking response");
		if (user.isAuthenticated()) {
			/*
			 * Valid user/password - update database
			 */
			 dbHelper.insertData(user, password);
			 
			/* Ask the user for a lock pattern so we do not need to keep asking
			 * for the username/password combination
			 */		
			SecurityPrefs.setAutoSavePattern(this, true);
			Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,
			        this, LockPatternActivity.class);
			startActivityForResult(intent, REQ_CREATE_PATTERN);
			
		
			
		} else {
			try {if (user.getProjName().equalsIgnoreCase("Network Error")) {
				Toast.makeText(this, "Network Error - Try later", Toast.LENGTH_LONG).show();
				Log.d("Diag","HttpTools returend Exception");
			} else {
			    Toast.makeText(this, "Bad Username/Password", Toast.LENGTH_LONG).show();	
			    Log.d("Exiting", "Bad username/password combination: " + Username + "/" + Password);
			    Log.d("Diag", "Got this: " + user.isAuthenticated());
			}
			this.deleteDatabase("SORdb");
			exitApp(3);
			} catch (Exception e) {
				Log.e("Main", "Error occurred connecting to web service");
				Toast.makeText(this,  "Error occurred connecting to online service", Toast.LENGTH_LONG).show();
				exitApp(5);
			}
		}

	}

	private void exitApp(int seconds) {
		int delay = seconds * 1000;
		
		dbHelper.close();
		
		final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            	running = false;
                finish();
                ((Activity) context).finish();
            }
        }, delay);
	}
	
	
	public void showRetryDialog() {
		LinearLayout ll_Main = new LinearLayout(this);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Reset Authentication???");
		alert.setView(ll_Main);
		alert.setCancelable(false);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				context.deleteDatabase("SORdb");
				showLoginDialog();
			}
		});

		alert.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						exitApp(3);
					}
				});

		AlertDialog dialog = alert.create();
		dialog.show();
	}
	
	public void showLoginDialog() {
		LinearLayout ll_Main = new LinearLayout(this);
		LinearLayout ll_Row1 = new LinearLayout(this);
		LinearLayout ll_Row2 = new LinearLayout(this);
		ll_Main.setOrientation(LinearLayout.VERTICAL);
		ll_Row1.setOrientation(LinearLayout.HORIZONTAL);
		ll_Row2.setOrientation(LinearLayout.HORIZONTAL);
		final EditText et_User = new LoginEditText(this);
		final EditText et_Pass = new LoginEditText(this);
		TextView tv_User = new TextView(this);
		TextView tv_Pass = new TextView(this);
		tv_User.setText("Username: ");
		tv_Pass.setText("Password: ");
		et_Pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		ll_Row1.addView(tv_User);
		ll_Row1.addView(et_User);
		ll_Row2.addView(tv_Pass);
		ll_Row2.addView(et_Pass);
		ll_Main.addView(ll_Row1);
		ll_Main.addView(ll_Row2);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Authenticate to SOR");
		alert.setView(ll_Main);
		alert.setCancelable(false);
		alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				username = et_User.getText().toString();
				password = et_Pass.getText().toString();
				Toast.makeText(getBaseContext(),
						"Authenticating...",
						Toast.LENGTH_LONG).show();
				checkAuthentication(username, password);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		AlertDialog dialog = alert.create();
		dialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	        Intent data) {
	    switch (requestCode) {
	    case REQ_CREATE_PATTERN: {
	        if (resultCode == RESULT_OK) {
	        	
	        	/*
	        	 * open the database and check for upgrades before moving on
	        	 */
	        	dbHelper.getWritableDatabase();
	        	dbHelper.close();
	        	
	            // OK to activate the button on the page.
	        	//reportEventButton.setEnabled(true);
	        	/*
				 * Start the database monitor thread
				 */
				userInfo = dbHelper.getUserInfo();
				running = true;
				mdb_task.execute();
	        } 
	        break;
	    }// REQ_CREATE_PATTERN
	    case REQ_ENTER_PATTERN: {
	        /*
	         * NOTE that there are 4 possible result codes!!!
	         */
	    	
	        switch (resultCode) {
	        case RESULT_OK:
	            // The user passed
	        	//reportEventButton.setEnabled(true);
	        	/*
				 * Start the database monitor thread
				 */
				userInfo = dbHelper.getUserInfo();
				running = true;
				mdb_task.execute();   		
	            break;
	        case RESULT_CANCELED:
	            // The user cancelled the task
	        	Toast.makeText(getBaseContext(),
						"Cancelled by user - Goodbye",
						Toast.LENGTH_LONG).show();
	        	exitApp(3);
	            break;
	        case LockPatternActivity.RESULT_FAILED:
	        	showRetryDialog();
	            break;
	        case LockPatternActivity.RESULT_FORGOT_PATTERN:
	            showLoginDialog();
	            break;
	        }

	        /*
	         * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
	         * the number of tries that the user did.
	         */
	        @SuppressWarnings("unused")
			int retryCount = data.getIntExtra(
	                LockPatternActivity.EXTRA_RETRY_COUNT, 0);

	        break;
	    }// REQ_ENTER_PATTERN
	    
	     
	    
	    }
	}

	/*
	 * Get the current GPS or Network location
	 * Then store it along with the other collected data
	 * in the local database
	 * 
	 * The DBMonitor thread will pick it up and send to the server
	 */
	private void getLocation(EventReport er) {
		GetLocation gl = new GetLocation(this);
		Location loc = gl.getLocation();
		
		if (loc != null ) {
		er.setLat(loc.getLatitude());
		er.setLon(loc.getLongitude());
		ReportDBHelper.insertEvent(er);
		} else {
			
			/*
			 * We did not get a location - sleep for 5 seconds and try again
			 */
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getLocation(er);
		}
	}
	
	@SuppressWarnings("unused")
	private void createReport(String ReportType) {
		eventReport = new EventReport();
		
		User s = dbHelper.getUserInfo();
		// There is only one other button, so handle the data submit
		Toast.makeText(this, "Saving Data", Toast.LENGTH_LONG).show();
		eventReport.setName(ReportType + " Report - " + s.getUsername());
		eventReport.setDescription(s.getUsername() + "," + s.getOrgID() + ", Submitted quick report");
		eventReport.setSeverity(ReportType);
		eventReport.setType(ReportType + " from SpotOn Report");
		eventReport.setDisposition("Report Provided");
		//eventReport.setFilename(null);
		
		
		/*
		 * Now go get the current location to store with this data
		 */
		getLocation(eventReport);
					
	}
	
	
	@Override
	public void onClick(View v) {
		Log.d("Got click", "Got button click: " + v.getId());
		/*
		 * Determine What was clicked
		 */
		if (v.getId() == R.id.exitButton) {
			/*
			 * Exit button pushed... so quit
			 */
			
			/*
			 * Turn off the background tasks now
			 */
			running = false;
			mdb_task.cancel(true);
			finish();
		} else {
			if (v.getId() == R.id.sorButton) {
				/*
				 * Go to SOR online (dev.spotonresponse.com for now)
				 */
				String url = "https://dev.spotonresponse.com/index.php?autologin=true&username=" 
						+ userInfo.getUsername() 
						+ "&password=" + userInfo.getPassword()
						+ "&uuid=" + userInfo.getUuid();
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);	
			} else {
				Intent k = new Intent(this, ReportEvent.class);
				
				/*
				 * Get some user information out of the database to include
				 * with the report
				 */
				User s = dbHelper.getUserInfo();
	        	k.putExtra("username", s.getUsername());
	        	k.putExtra("orgid", s.getOrgID());
	        	
	        	/*
	        	 * Set the auto report status if applicable
	        	 * The ReportEvent class will fill out the form if necessary
	        	 */
				switch (v.getId()) {
		        case R.id.CheckinButton:
		        	k.putExtra("reportType", "OK");
		        	break;
		        case R.id.reportWatchButton:
		        	k.putExtra("reportType", "Watch");
		            break;
		        case R.id.reportProblemButton:
		        	k.putExtra("reportType", "Problem");
		        	break;
		        case R.id.reportEmergencyButton:
		        	k.putExtra("reportType", "Emergency");
		        	break;
		      //  case R.id.reportEventButton:
		      //  	k.putExtra("reportType", "None");
		      //  	break;
				}
				
				startActivity(k);
				
			}
		} 
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	
	
	/*
	 * This AsyncTask monitors the SORREPORTS table for entries
	 * When a new entry is found, it will sent it to the
	 * REST webservices at the SOR cloud.
	 * 
	 * This allows the device to be offline when recording data
	 * and then allow it to sync with the server when it is
	 * back online
	 */
	private class MonitorDatabase extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String result = null;

			while (running) {
				Log.d("ThreadReport", "monitorDatabase Thread woke up");
				List<EventReport> erl = dbHelper.checkForEvents();

				for (Iterator<EventReport> i = erl.iterator(); i.hasNext();) {
					EventReport er = i.next();
					SendReport.sendReport(er, userInfo.getUsername(),
							userInfo.getPassword(), userInfo.getProjectID(),
							userInfo.getUuid(), userInfo.getOrgID(), REPORT_URL);
				}

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					running = false;
					super.cancel(true);
					result = "restart";
				}
			}

			return result;

		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 * 
		 * If we make it here - there was a problem with the thread that
		 * Monitors the database - so try to restart it
		 */
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(context, "Restarting DB Monitor", Toast.LENGTH_LONG)
					.show();
			
			running = true;
			mdb_task.execute(); 
		}

	}
	
}
