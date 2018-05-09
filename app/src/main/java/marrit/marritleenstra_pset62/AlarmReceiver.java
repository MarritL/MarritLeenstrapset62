package marrit.marritleenstra_pset62;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


public class AlarmReceiver extends BroadcastReceiver {

    // variables
    private static final String TAG = "ALARMRECEIVER";
    public static final String CHANNEL_ID = "channelReminder";
    public static final int NOTIFICATION_ID = 1;



    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(TAG + " alarm received");

        long when = System.currentTimeMillis();

        // set the notification tap action
        Intent resultIntent = new Intent(context, SignInActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // create a channel
        createNotificationChannel(context);

        // set the notification content
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder")
                .setContentText("Did you eat vegetarian today?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true).setWhen(when)
                .setVibrate(new long[]{1000,1000})
                .setSound(alarmSound);



        // show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    // create a channel (new from API level 26, ignored by older versions)
    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String description = "reminds user to let the app know if it was a vegetarian day";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "reminder", importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
