package com.etrust.stategrid.utils;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

public class TimeFileNameGenerator implements FileNameGenerator{

	private static final String TAG = TimeFileNameGenerator.class.getSimpleName();

	@Override
	public String generate(String imageUri) {
		String name = null;
		int p = imageUri.lastIndexOf("=");
		if(p!=-1){
			name = imageUri.substring(p+1);
			return name;
		}else{
			return imageUri;
		}
		
		/**
		try {
			String filename = imageUri.replace("*", "");
			if(filename.endsWith("jpg")||filename.endsWith("png")){
				filename = filename.substring(0, filename.lastIndexOf("."));
			}
			return URLEncoder.encode(filename, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			Log.e(TAG, "createFilePath - " + e);
		}
		return null;
		**/
	}
}
