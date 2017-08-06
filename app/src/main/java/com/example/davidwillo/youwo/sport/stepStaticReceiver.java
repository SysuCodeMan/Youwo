package com.example.davidwillo.youwo.sport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.davidwillo.youwo.LoginActivity;
import com.example.davidwillo.youwo.R;


public class stepStaticReceiver extends BroadcastReceiver {
    private static final String STATICACTION = "com.example.john.finalproject.Sport.stepStaticReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STATICACTION)) {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.widget);
            Notification.Builder builder = new Notification.Builder(context);
            Intent click = new Intent(context, LoginActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, click, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentTitle("目标达成！")
                    .setContentText("您已达成每日目标")
                    .setTicker("点击查看")
                    .setLargeIcon(bm)
                    .setSmallIcon(R.mipmap.widget)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //  绑定Notification,发送相应的通知
            Notification notify = builder.build();
            manager.notify(0, notify);
        }
    }


}
