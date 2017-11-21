package com.michal.locationproject;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by michal on 18.10.17.
 */

public class LocationAppSharedPreferences {

    public static final String USER_SURNAME_KEY = "surname";
    public static final String USER_NAME_KEY = "name";
    public static final String TOKEN = "id";
    private static final String LOCATION_PROJECT_SHARED_PREFERENCES_NAME = "com.michal.locationproject.SHARED_PREFERENCES_FILE";

    private Context context;

    public LocationAppSharedPreferences(Context context) {
        this.context = context;
    }


    private SharedPreferences getSharedPreferences() {
       return context.getSharedPreferences(LOCATION_PROJECT_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //public for now
    public SharedPreferences.Editor getSharedPreferencesEditor() {
        return getSharedPreferences().edit();
    }

    public void saveNameToSharedPreferences(String name) {
        getSharedPreferencesEditor().putString(USER_NAME_KEY, name).commit();
    }

    public void saveSurnameToSharedPreferences(String surname) {
        getSharedPreferencesEditor().putString(USER_SURNAME_KEY, surname).commit();
    }

    public String getNameFromSharedPreferences() {
        return getSharedPreferences().getString(USER_NAME_KEY, "");
    }

    public String getSurnameFromSharedPreferences() {
        return getSharedPreferences().getString(USER_SURNAME_KEY, "");
    }

    public void saveToken(int id) {
        getSharedPreferencesEditor().putInt(TOKEN,id).commit();
    }

    public int getToken() {
        return getSharedPreferences().getInt(TOKEN, -1);
    }

    //// TODO: 18.10.17 GET TOKEN FROM SERVER AND PUT IT AS SHARED PREFERENCE

}
