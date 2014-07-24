package com.cloudmediaplus.followme.framework;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.cloudmediaplus.followme.Resources;
import com.cloudmediaplus.followme.views.MainActivity;

public class DeviceNotifier {

    NotificationManager notificationManager;
    private Context context;

    public DeviceNotifier(Context context){
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public void notify(String subject, String content)
    {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification  = new Notification.Builder(context)
                .setContentTitle(subject)
                .setContentText(content)
                .setSmallIcon(Resources.almogtarbeenIcon)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();


        notificationManager.notify(0, notification);
    }
}
