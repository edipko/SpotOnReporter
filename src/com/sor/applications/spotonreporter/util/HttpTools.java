package com.sor.applications.spotonreporter.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sor.exceptions.HttpToolsException;

import android.util.Log;

public class HttpTools {

public InputStream postFormData(String url, UrlEncodedFormEntity data) throws HttpToolsException {
        
        DefaultHttpClient client = new DefaultHttpClient();       
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(data); 
	    
        try {
           HttpResponse getResponse = client.execute(httppost);
           final int statusCode = getResponse.getStatusLine().getStatusCode();
           
           if (statusCode != HttpStatus.SC_OK) { 
              Log.w(getClass().getSimpleName(), 
                  "Error " + statusCode + " for URL " + url); 
              throw new HttpToolsException("Bad Http Status: " + statusCode);
           }

           HttpEntity getResponseEntity = getResponse.getEntity();
           return getResponseEntity.getContent();
           
        } 
        catch (IOException e) {
           httppost.abort();
           Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }
        
        throw new HttpToolsException("Network Error");
        
     }


}
