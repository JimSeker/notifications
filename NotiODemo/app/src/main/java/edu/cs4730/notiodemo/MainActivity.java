package edu.cs4730.notiodemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


/*
  Very simple main to start the service that will then show create notifications for the O notification channels and icon dots.

  Note for the badges (dots) to work, I think (untested) you need studio 3.0 with the adaptive icons.
  Or preview 3 on a nexus 5x is not working correctly, also possible.  I'll see when the final version of 8 and studio 3.0 are out.
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
                makenoti("hi htere", 1);
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
