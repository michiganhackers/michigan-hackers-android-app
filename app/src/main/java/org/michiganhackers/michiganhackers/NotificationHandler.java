package org.michiganhackers.michiganhackers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationHandler extends NotificationCompat {
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Test Notification")
            .setContentText("Test Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    private void createNotificaionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(channel_description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
