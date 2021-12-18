package com.uni.treest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preferences {

    private final String FIRST_RUN_STRING = "firstRun";
    private final String PREFERENCES_NAME = "mainPreferences";
    private final String LAST_DID = "lastDid";
    private final String SWITCHED_DID = "switchedDid";
    private final String SESSION_ID = "sid";
    private final String DIRECTION = "direction";
    private final String SWITCH_DIRECTION = "switchedDirection";



    private static Preferences theInstance = null;

    private Preferences(){
    }
    public static synchronized Preferences getTheInstance (){
        if(theInstance == null){
            theInstance = new Preferences();
        }
        return theInstance;
    }
    public boolean isFirstRun(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(FIRST_RUN_STRING, true);
    }
    public void setFirstRunAndSid(Context context, String sid){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(FIRST_RUN_STRING, false);
        editor.putString(SESSION_ID, sid);
        editor.apply();
    }
    public int getLastDid(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(LAST_DID, -1); // -1 IF First run with no selected DID.
    }
    public int getSwitchedLastDid(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(SWITCHED_DID, -1); // -1 IF First run with no selected DID.
    }
    public void setDid(Context context, int did){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(LAST_DID, did);
        editor.apply();
    }
    public void setSwitchedDid(Context context, int did){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SWITCHED_DID, did);
        editor.apply();
    }
    public String getSid(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(SESSION_ID, "");
    }
    public String getDirection(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(DIRECTION, "CULO");
    }
    public String getSwitchedDirection(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(SWITCH_DIRECTION, "");
    }
    public void setDirection(Context context, String direction){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(DIRECTION, direction);
        editor.apply();
    }
    public void setSwitchedDirection(Context context, String switchDirection){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SWITCH_DIRECTION, switchDirection);
        editor.apply();
    }
}
