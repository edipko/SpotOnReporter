package com.sor.applications.spotonreporter.util;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocListener implements LocationListener {
    private static double lat =0.0;
    private static double lon = 0.0;
    private static double alt = 0.0; 
    private static double speed = 0.0;

    public static double getLat()
    {
        return lat;
    }

    public static double getLon() 
    {
        return lon;
    }

    public static double getAlt()
    {
        return alt;
    }

    public static double getSpeed()
    {
        return speed;
    }



    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    

	@Override
	public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        alt = location.getAltitude();
        speed = location.getSpeed(); 
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}