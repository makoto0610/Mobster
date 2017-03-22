package com.the_great_amoeba.mobster;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by anireddy on 1/23/17.
 */

public class SaveSharedPreferences {
    static final String PREF_USER_NAME= "username";
    static final String PREF_THEME = "theme";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static void setChosenTheme(Context ctx, String theme) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_THEME, theme);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getChosenTheme(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_THEME, "");
    }
}
