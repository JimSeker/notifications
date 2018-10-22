package edu.cs4730.notiodemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


/**
 * Very simple main to start the service that will then show create notifications for the O notification channels and icon dots.
 * <p>
 * Note for the badges (dots) to work, you need something like google launcher, where badges are supported
 *   also turned on, since they can be turned off in some launchers as well.
 */

public class MainActivity extends AppCompatActivity {

    // The id of the channel.
    public static String id = "test_channel_01";
    NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent number5 = new Intent(getBaseContext(), MyNotiService.class);
                number5.putExtra("times", 5);
                startService(number5);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makenoti("hi there", 1);
            }
        });

        createchannel();
    }

    private void createchannel() {
        mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
// The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;  //which is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
// Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setShowBadge(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);
    }


    public void makenoti(String message, int msgcount) {

        //Notification noti = new NotificationCompat.Builder(getApplicationContext())
        Notification noti = new Notification.Builder(getApplicationContext(), MainActivity.id)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
            .setChannelId(MainActivity.id)
            .setContentTitle("Service")   //Title message top row.
            .setContentText(message)  //message when looking at the notification, second row
            .setAutoCancel(true)   //allow auto cancel when pressed.
            .build();  //finally build and return a Notification.

        //Show the notification
        mNotificationManager.notify(1, noti);

    }

}
