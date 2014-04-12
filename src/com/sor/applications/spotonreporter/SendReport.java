package com.sor.applications.spotonreporter;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sor.applications.spotonreporter.util.SORReportRestClient;
import com.sor.applications.spotonreporter.util.SorFileTools;
import com.sor.beans.EventReport;

public class SendReport {

	private static int eventID;
	private static boolean complete = false;
    private static boolean file_continue = false;
    private static JSONObject fileObj = new JSONObject();
	private static JSONArray fileList = new JSONArray();
	
	public static boolean sendReport(EventReport er, String username,
			String password, int projID, String uuid, int orgID, String URL) {

		
		eventID = er.getId();

		
		Log.d("SendReport", "Begin sendReport");

		RequestParams params = new RequestParams();
		params.add("username", username);
		params.add("password", password);
		params.add("lat", er.getLat().toString());
		params.add("lon", er.getLon().toString());
		params.add("type", er.getType());
		params.add("severity", er.getSeverity());
		params.add("disposition", er.getDisposition());
		params.add("description", er.getDescription());
		params.add("name", er.getName());

		params.add("projectID", Integer.toString(projID));
		params.add("uuid", uuid);
		params.add("orgID", Integer.toString(orgID));

		
		
		
		/*
		 * If there is a file to upload - do that first this will return a
		 * filename (the server location) so we can send that with the rest of
		 * the entry
		 */
		if (er.getFilePaths().size() > 0) {
			for (String file : er.getFilePaths()) {
				file_continue = false;
				File uploadFile = new File(file);
				Log.d("SendReport", "Will upload: " + uploadFile.getName());
			//	final RequestParams f_params = params;

				try {
					RequestParams rp = new RequestParams();
					Uri uri = Uri.fromFile(uploadFile);
					String mimetype = SorFileTools.getMimeType(uri.toString());
					rp.put("media", uploadFile, mimetype);
					Log.d("SendReport", "Mimetype is: " + mimetype);

					SORReportRestClient.post("/f/uploadfile", rp,
							new JsonHttpResponseHandler() {
								@Override
								public void onStart() {
									Log.d("SendReport", "FileUPload Start");
								}

								@Override
								public void onFinish() {
									Log.d("SendReport", "FileUpload Finish");
								}

								@Override
								public void onSuccess(int statusCode,
										org.apache.http.Header[] headers,
										byte[] responseBody) {
									Log.d("SendReport", "FileUpload onSuccess");
									String s = new String(responseBody);
									JSONObject jObject = null;
									String filename = null;

									try {
										Log.d("SendReport",
												"Got fileupload response: " + s);
										jObject = new JSONObject(s);

										filename = jObject.getString("filename");
										fileList.put(filename);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									file_continue = true;
								}

								@Override
								public void onFailure(int statusCode,
										org.apache.http.Header[] headers,
										byte[] responseBody, Throwable error) {
									Log.e("SendReport", "File Send Failure");
								}

							});
					
				} catch (FileNotFoundException e) {
					Log.e("SendReport", "File not found exception");
				}
			
				/*
				 * Do files one at a time... not sure I need this, but
				 */
				while (!file_continue) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}
			try {
				fileObj.put("files", fileList);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			postReport(params, fileObj);
		} else {

			postReport(params, fileObj);
		}

		/*
		 * Wait for the async task to complete
		 */
		while (!complete) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;

	}

	private static void postReport(RequestParams params, JSONObject files) {
		Log.d("SendReport", "Start Rest Client");
		params.add("fileNames", files.toString());
		SORReportRestClient.post("/r/reportevent", params,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						Log.d("SendReport", "Start Rest Client2");
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						// Log.d("SendReport", "Sending byted: " +
						// bytesWritten);
					}

					@Override
					public void onFinish() {
						Log.d("SendReport", "Send Finish");
					}

					@Override
					public void onSuccess(int statusCode,
							org.apache.http.Header[] headers,
							byte[] responseBody) {
						Log.d("SendReport", "Post onSuccess");
						complete = true;
						boolean r = MainDBHelper.deleteEvent(eventID);
						if (r) {
							Log.d("MonitorDatabase",
									"Event deleted from local database");
						} else {
							Log.d("MonitorDatabase",
									"There was a problem deleting the entry from the database");
						}
					}

					@Override
					public void onFailure(int statusCode,
							org.apache.http.Header[] headers,
							byte[] responseBody, Throwable error) {
						Log.e("SendReport", "JSON Send Failure");
					}
				});
	}
}
