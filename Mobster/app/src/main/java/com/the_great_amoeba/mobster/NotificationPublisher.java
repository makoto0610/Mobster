package com.the_great_amoeba.mobster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Notification publisher class.
 *
 * @author Natalie
 * @version 1.0
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        Intent notificationIntent = new Intent(context, Login.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pending = PendingIntent.getActivity(context, 0 , notificationIntent, 0);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        if (SaveSharedPreferences.getNotification(context).equals("on")) {
            notificationManager.notify(id, notification);
        }
    }
}