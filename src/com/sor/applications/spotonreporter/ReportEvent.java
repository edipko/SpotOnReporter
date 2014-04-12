package com.sor.applications.spotonreporter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sor.applications.spotonreporter.util.GetLocation;
import com.sor.applications.spotonreporter.util.SystemUiHider;
import com.sor.beans.EventReport;
import com.sor.beans.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ReportEvent extends Activity implements View.OnClickListener {

	private static final int PICK_IMAGE = 1;
	private ArrayList<String> filePaths = new ArrayList<String>();
	private Spinner severity = null;
	private Spinner type = null;
	private EditText description = null;
	private EditText name = null;
	private EditText disposition = null;
	private EventReport eventReport = null;
	private LinearLayout imageLinearLayout = null;

	private Context context = null;
	@SuppressWarnings("unused")
	private User userInfo = null;
	@SuppressWarnings("unused")
	private ReportDBHelper db = null;

	final String URL = "http://dev.spotonresponse.com:8080/sor/r/reportevent";
	final int delay = 2000;
	
	int imageid = 1000;

	public ReportEvent() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		Log.d("ReportEvent", "In Class");

		eventReport = new EventReport();
		setContentView(R.layout.activity_report_event);

		db = new ReportDBHelper(this);

		final TextView severity_text = (TextView) findViewById(R.id.severity_text);
		final TextView types_text = (TextView) findViewById(R.id.types_text);
		final Button fileButton = (Button) findViewById(R.id.fileSelectButton);
		final TextView fileLabelText = (TextView) findViewById(R.id.fileLabelText);
		final TextView descriptionLabelText = (TextView) findViewById(R.id.descriptionLabelText);
		final Button submitButtonTop = (Button) findViewById(R.id.submitDataButtonTop);
		final Button submitButtonBottom = (Button) findViewById(R.id.submitDataButtonBottom);
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final TextView nameLabelText = (TextView) findViewById(R.id.nameLabelText);
		final TextView dispositionLabelText = (TextView) findViewById(R.id.dispositionLabelText);
        
        imageLinearLayout = (LinearLayout) findViewById(R.id.imageLinearLayout);
		/*
		 * Configure buttons
		 */
		submitButtonTop.setMaxHeight(10);
		fileButton.setMaxHeight(10);
		fileButton.setMaxWidth(10);
		
		
		severity = (Spinner) findViewById(R.id.severity_spinner);
		type = (Spinner) findViewById(R.id.types_spinner);
		description = (EditText) findViewById(R.id.descriptionEditText);
		name = (EditText) findViewById(R.id.nameEditText);
		disposition = (EditText) findViewById(R.id.dispositionEditText);

		/*
		 * Get the default values passed from the Main activity
		 */
		Intent i = getIntent();
		String username = i.getStringExtra("username");
		int orgid = i.getIntExtra("orgid", 0);
		String reportType = i.getStringExtra("reportType");

		/*
		 * If a default report button was pressed, auto fill the form
		 */
		if ((reportType.contains("OK")) || (reportType.contains("Watch"))
				|| (reportType.contains("Problem"))
				|| (reportType.contains("Emergency"))) {
			name.setText(reportType + " Report - " + username);
			description.setText(username + ", " + orgid
					+ ", Submitted quick report");
			disposition.setText("Report Provided");
			type.setSelection(1);

		}

		/*
		 * Set the Severity spinner based on the button pushed
		 */
		if (reportType.contains("OK")) {
			severity.setSelection(1);
		}
		if (reportType.contains("Watch")) {
			severity.setSelection(2);
		}
		if (reportType.contains("Problem")) {
			severity.setSelection(3);
		}
		if (reportType.contains("Emergency")) {
			severity.setSelection(4);
		}

		/*
		 * Set the labels of the form
		 */
		severity_text.setText("Severity:");
		types_text.setText("Type:");
		descriptionLabelText.setText("Description:");
		nameLabelText.setText("Title:");
		dispositionLabelText.setText("Disposition:");
		fileLabelText.setText("Add Media:");

		/*
		 * Set the Button Listeners
		 */
		fileButton.setOnClickListener(this);
		submitButtonTop.setOnClickListener(this);
		submitButtonBottom.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

	}

	private void submitdata() {
		final Handler handler = new Handler();
		ArrayList<String> filepathArray = new ArrayList<String>();
		
		Toast.makeText(this, "Saving Data", Toast.LENGTH_LONG).show();
		eventReport.setName(name.getText().toString());
		eventReport.setDescription(description.getText().toString());
		eventReport.setSeverity(severity.getSelectedItem().toString());
		eventReport.setType(type.getSelectedItem().toString());
		eventReport.setDisposition(disposition.getText().toString());
		
		/*
		 * Create array file the filenames to store in the Bean
		 */
		for (String filePath : filePaths) {
			filepathArray.add(filePath);
		}
		eventReport.setFilePaths(filepathArray);

		/*
		 * Now go get the current location to store with this data
		 */
		getLocation(eventReport);
		
		
		/*
		 * Not sure if this is the correct way to do this, But we want to get
		 * back to the data entry screen So wait a few seconds and restart this
		 * Class
		 */
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, delay);
	}

	@Override
	public void onClick(View v) {

		/*
		 * Will need this to exit after click
		 */
		final Handler handler = new Handler();

		/*
		 * Determine What was clicked
		 */
		switch (v.getId()) {
		case R.id.fileSelectButton:
			handler.post(new Runnable() {
				public void run() {
					// File Select button was pushed
					Intent pickIntent = new Intent();
					pickIntent.setType("image/*");
					pickIntent.setAction(Intent.ACTION_GET_CONTENT);

					 Log.d("ReportEvent", "FileSelectButton Pushed");

					Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Intent getVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					Intent getAudioIntent = new Intent(MediaStore.EXTRA_MEDIA_TITLE);

					String pickTitle = "Select or take a new Picture"; // Or get from
					// strings.xml
					Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {
							takePhotoIntent, getVideoIntent, getAudioIntent });

					startActivityForResult(chooserIntent, PICK_IMAGE);
				}
			});

			break;

		case R.id.submitDataButtonTop:
			submitdata();
			break;
		case R.id.submitDataButtonBottom:
			submitdata();
			break;
		case R.id.cancelButton:
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, delay);
			break;
		}
	}

	/*
	 * Get the current GPS or Network location Then store it along with the
	 * other collected data in the local database
	 * 
	 * The DBMonitor thread will pick it up and send to the server
	 */
	private void getLocation(EventReport er) {
		GetLocation gl = new GetLocation(this);
		Location loc = gl.getLocation();

		if (loc != null) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 * 
	 * Handle the Image Picking event
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("Report Event - onActivityResult", "Request Code is: " + requestCode + " looking for: " + PICK_IMAGE);
		if (data.getData() == null) {
			Log.d("Report Event - onActivityResult", "Null data");
		}
		if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
			Uri _uri = data.getData();

			// User had pick an image.
			Cursor cursor = getContentResolver()
					.query(_uri,
							new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
							null, null, null);
			cursor.moveToFirst();

			// Link to the image
			String filePath = cursor.getString(0);
			filePaths.add(filePath);
			cursor.close();

			BitmapDrawable bmd = resizeImage(filePath);
			//imageView.setImageDrawable(bmd);
			//imageView.setScaleType(ScaleType.CENTER);
			
			/*
			 * Create a new imageView and display an icon of the image that will
			 * be uploaded
			 */
			Log.d("ReportEvent", "Creating Image: " + imageid);
			
			ImageView iv = new ImageView(context);
		    iv.setTag(imageid);
		    iv.setImageDrawable(bmd);
		    iv.setScaleType(ScaleType.CENTER);
		    iv.setId(imageid);
		    imageid++;
		    imageLinearLayout.addView(iv);
		}
	}

	/*
	 * The pictures stored on the devices are sometimes to big to display like
	 * we need it, so this will resize them to avoid the out-of-memory error
	 * that results from the large image
	 */
	private BitmapDrawable resizeImage(String filePath) {

		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int newWidth = 1000;
		int newHeight = 1000;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// create the new Bitmap object
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		@SuppressWarnings("deprecation")
		BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);

		return bmd;
	}

}
