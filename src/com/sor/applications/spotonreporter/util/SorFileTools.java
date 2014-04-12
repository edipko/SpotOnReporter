package com.sor.applications.spotonreporter.util;

import android.webkit.MimeTypeMap;

public class SorFileTools {

	public static String getMimeType(String url)
    {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }
}
