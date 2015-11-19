package com.freway.ebike.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.freway.ebike.model.User;
import com.google.gson.Gson;

public class TwitterSp {
    private static final String SHARED_PREFS_NAME = "t_prefs";
    
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String ACCESS_TOKEN_SECRET = "access_token_secret";

    private static final String USER = "USER";
    
    private static TwitterSp sInstance;

    private SharedPreferences mPrefs;

    private TwitterSp(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static TwitterSp get(Context context) {
        if (sInstance == null) {
            synchronized (TwitterSp.class) {
                if (sInstance == null) {
                    sInstance = new TwitterSp(context);
                }
            }
        }
        return sInstance;
    }

    public String getTwitterAccessTokenKey() {
        return mPrefs.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getTwitterAccessTokenSecret() {
        return mPrefs.getString(ACCESS_TOKEN_SECRET, null);
    }
    
    public void setTwitterAccessToken(String accessTokenKey, String accessTokenSecret) {
        Editor editor = mPrefs.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessTokenKey);
        editor.putString(ACCESS_TOKEN_SECRET, accessTokenSecret);
        editor.commit();
    }
    
    public void setUser(User user){
    	Gson gson=new Gson();
    	String data=gson.toJson(user);
    	Editor editor = mPrefs.edit();
        editor.putString(USER, data);
        editor.commit();
    }
    
    public User getUser(){
    	Gson gson=new Gson();
    	User user=null;
    	String data=mPrefs.getString(USER, "");
    	if(!TextUtils.isEmpty(data)){
    		user=gson.fromJson(data, User.class);
    	}
        return user;
    }
    
    public void clear(){
    	mPrefs.edit().clear();
    }
}
