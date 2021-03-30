package com.example.meowtify.models;

import com.google.gson.annotations.SerializedName;

public class ExternalUrls{

	@SerializedName("spotify")
	private String spotify;

	public String getSpotify(){
		return spotify;
	}

	@Override
 	public String toString(){
		return 
			"ExternalUrls{" + 
			"spotify = '" + spotify + '\'' + 
			"}";
		}
}