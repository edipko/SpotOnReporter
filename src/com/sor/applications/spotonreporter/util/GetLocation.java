package com.sor.applications.spotonreporter.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GetLocation implements LocationListener {
	private LocationManager locationManager = null;
	private boolean isGPSEnabled;
	private boolean isNetworkEnabled;
	@SuppressWarnings("unused")
	private boolean canGetLocation;
	private Context mContext;
	private Location location;


	public GetLocation(Context context) {
		this.mContext = context;
	}

	@SuppressWarnings({ "static-access", "unused" })
	public Location getLocation() {

		Double latitude = 0.0;
		Double longitude = 0.0;
		
		try {

			Log.d("GetLocation", "Starting...");
			locationManager = (LocationManager) mContext
					.getSystemService(mContext.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				Log.w("DOCOMO-2", "Network Connection failed");
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				
				Log.d("GetLocation", "Checking if network is enabled");
				if (isNetworkEnabled) {

					Log.d("GetLocation", "Network is enabled");
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 0, 0, this);
					String locationProvider = LocationManager.NETWORK_PROVIDER;
					// Or, use GPS location data:
					// String locationProvider = LocationManager.GPS_PROVIDER;

					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							Log.d("GetLocation", "Got Network location");
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				
				Log.d("GetLocation", "Checking if GPS is enabled");
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					Log.d("GetLocation", "GPS is enabled");
					if (location == null) {
						Log.d("GetLocation", "Location still null, getting GPS locaiton");
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 0, 0, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								Log.d("GetLocation", "Got GPS Location");
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}

						}
					}
				}
			}
			
			Log.d("GetLocation", "Cleaning up");
			locationManager = null;
			//location = null;

		} catch (Exception e) {
			locationManager = null;
			location = null;
			e.printStackTrace();
		}
		if (location != null) {
			Log.d("GetLocation", "Returning Location: " + location.getLatitude() + "/" + location.getLongitude());
		} else {
			Log.d("GetLocation", "Have null location");
		}
		return location;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
