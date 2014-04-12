package com.sor.applications.spotonreporter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.os.StrictMode;
import android.util.Log;

import com.google.gson.Gson;
import com.sor.applications.spotonreporter.util.HttpTools;
import com.sor.beans.User;
import com.sor.exceptions.HttpToolsException;

public class Authentication {

	public static User authenticate(String username, String password, String URL) {

		Log.d("Authenticate", "Trying authentication...");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			UrlEncodedFormEntity data = new UrlEncodedFormEntity(nameValuePairs);

			Log.d("Authenticate", "Connecting to: " + URL);
			HttpTools ht = new HttpTools();
			Log.d("Authenticate", "Sendign Data: " + data.toString());
			InputStream source = ht.postFormData(URL, data);
			
			
			Log.d("Authenticate", "Starting gson");
			Gson gson = new Gson();
			Log.d("Authenticate", "Starting Reader");
			Reader reader = new InputStreamReader(source);
			Log.d("Authenticate", "Storing User");
			User response = gson.fromJson(reader, User.class);
			
			return response;

		} catch (HttpToolsException hte) {
			User response = new User();
			response.setAuthenticated(false);
			response.setProjName("Network Error");
			return response;
			
		} catch (Exception ex) {
			User response = new User();
			response.setAuthenticated(false);
			response.setProjName("Other Exception");
			Log.d("Authentication Exception", ex.getLocalizedMessage());
			return response;
		}

			
	}

}
