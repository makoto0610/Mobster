package Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import java.util.Calendar;

/**
 * Created by christineshih on 2/6/17.
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
}
