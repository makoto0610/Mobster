package com.the_great_amoeba.mobster;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Saved shared preference class for consistency in log in.
 *
 * @author Ani
 * @version 1.0
 */
public class SaveSharedPreferences {
    static final String PREF_USER_NAME= "username";
    static final String PREF_THEME = "theme";
    static final String PREF_NOTIFICATION = "notification";

    /**
     * Getter method for the shared preference
     *
     * @param ctx context from which the method is invoked.
     * @return default shared preference
     */
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Setter method for the the user name
     *
     * @param ctx context from which the method is invoked.
     * @param userName new username to set to
     */
    public static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    /**
     * Setter method for theme
     *
     * @param ctx context from which the method is invoked.
     * @param theme the preferred theme
     */
    public static void setChosenTheme(Context ctx, String theme) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_THEME, theme);
        editor.commit();
    }

    /**
     * Setter notification preference
     *
     * @param ctx context from which the method is invoked.
     * @param notification notification preference
     */
    public static void setNotification(Context ctx, String notification) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NOTIFICATION, notification);
        editor.commit();
    }

    /**
     * Getter method for the user name
     *
     * @param ctx context from which the method is invoked.
     * @return username
     */
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    /**
     * Getter method for the chosen theme
     *
     * @param ctx context from which the method is invoked.
     * @return the chosen theme
     */
    public static String getChosenTheme(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_THEME, "");
    }

    /**
     * Getter method for notification setting
     *
     * @param ctx context from which the method is invoked.
     * @return notification setting preference
     */
    public static String getNotification(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_NOTIFICATION, "");
    }
}
