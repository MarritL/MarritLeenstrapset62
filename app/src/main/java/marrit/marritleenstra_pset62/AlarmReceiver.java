package marrit.marritleenstra_pset62;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    // variables
    private static final String TAG = "ALARMRECEIVER";
    public static final String CHANNEL1 = "channel1";


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(TAG + " alarm received");

        long when = System.currentTimeMillis();

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // todo: reauthenticate is logged out when clicked on notification

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context, CHANNEL1)
                //Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder")
                .setContentText("Did you eat vegetarian today?")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true).setWhen(when)
                .setVibrate(new long[]{1000,1000,1000});


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, mNotificationBuilder.build());
    }
}
