package com.example.waterpoloinfo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.waterpoloinfo.ui.home.HomeFragment;

public class NotificationHandler {
    private static final String CHANNEL_ID = "shop_notification_channel";
    private final int NOTIFICATION_ID = 1;
    private NotificationManager mManager;
    private Context mContext;

    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    //Minden értesítés ami erre a csatornára jön u.e. lesz a tulajdonsága
    private void createChannel(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "WaterPoloInfo Notification", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setDescription("Notifications from Shop application");

        mManager.createNotificationChannel(channel);
    }


    public void send(String message) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("fragment", "home");

        //Ha újabb értesítés jön, akkor frissíti az előzőt.
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("WaterPoloInfo Application")
                .setContentText(message)
                .setSmallIcon(R.drawable.baseline_new_releases_24)
                .setContentIntent(pendingIntent);

        mManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancel() {
        mManager.cancel(NOTIFICATION_ID);
    }
}
