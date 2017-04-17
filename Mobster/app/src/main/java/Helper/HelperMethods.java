package Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.firebase.database.DataSnapshot;
import com.the_great_amoeba.mobster.R;
import com.the_great_amoeba.mobster.SaveSharedPreferences;

import org.joda.time.Duration;

import java.util.Calendar;
import java.util.HashMap;

import Objects.DisplayQuestion;

/**
 * Keeps track of commonly used helper methods.
 *
 * @author Christine
 * @version 1.0
 */
public class HelperMethods {

    /**
     * Displays an error dialog.
     *
     * @param title - title of the dialogue
     * @param message - message of the dialogue
     */
    public static void errorDialog(Context context, String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Calculates duration (end time - current time)
     *
     * @param end endTime in millis
     * @return time left as a long (milliseconds)
     */
    public static long computeDuration(long end) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long duration = end - currentTime;
        if (duration <= 0) {
            duration = 0;
        }
        return duration;
    }


    /**
     * Calculates the time left (duration).
     *
     * @param start start Calendar
     * @param end end Calendar
     * @return long array in the format of [days left, hours left, minutes left]
     */
    public static long[] computeDuration(Calendar start, Calendar end) {
        long[] returned = new long[3];

        long startTime = start.getTimeInMillis();
        long endTime = end.getTimeInMillis();

        long difference = endTime - startTime;

        final long secondsInMilli = 1000;
        final long minutesInMilli = secondsInMilli * 60;
        final long hoursInMilli = minutesInMilli * 60;
        final long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;

        // just need to do one more time if want to obtain seconds, would need to modify ret value

        returned[0] = elapsedDays;
        returned[1] = elapsedHours;
        returned[2] = elapsedMinutes;

        return returned;
    }

    /**
     * Returns a Question Object to be added to a list
     *
     * @param postSnapshot to get the Question from
     * @return the Question object to add
     */
    public static DisplayQuestion getQuestion(DataSnapshot postSnapshot, HashMap value, String keyQuestion) {
        Object start = value.get("start");

        Long ratingLong = 0L;
        Object ratingObj = value.get("num_favorites");
        if (ratingObj != null) ratingLong = (long) ratingObj;

        long access = (long) value.get("num_access");
        HashMap end =  (HashMap) value.get("end");
        long endTime = (long) end.get("timeInMillis");
        long duration = computeDuration(endTime);
        String username = (String) value.get("username");
        HashMap votedUsers = (HashMap) value.get("votedUsers");
        HashMap favoritedUsers = (HashMap) value.get("favoritedUsers");

        return new DisplayQuestion((String) (value.get("question")), "key", new Duration(duration),
                ratingLong, keyQuestion, access, username, votedUsers, favoritedUsers);
    }

    /**
     * Set the theme of the application accordingly
     *
     * @param app the activity
     * @param ctx context which the method is invoked from
     */
    public static void setChosenTheme(Activity app, Context ctx) {
        String currentTheme = SaveSharedPreferences.getChosenTheme(ctx);
        if (currentTheme.equals("dark")) {
            app.setTheme(R.style.AppTheme);
        } else {
            app.setTheme(R.style.AppThemeLight);
        }
    }
}

