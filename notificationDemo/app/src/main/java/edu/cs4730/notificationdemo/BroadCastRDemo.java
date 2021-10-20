package edu.cs4730.notificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;

public class BroadCastRDemo extends AppCompatActivity {
    //"edu.cs4730.notificationdemo.broadNotification"

    NotificationManager nm;
    int NotID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        findViewById(R.id.btn_bc).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                viaBroadcast();
            }
        });

        findViewById(R.id.btn_notibc).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Noti2broadcast();
            }
        });

    }

    public void viaBroadcast() {
        //in the simplest form, create an intent with the action set for the broadcastreceiver and send it.
        Intent intent = new Intent("edu.cs4730.notificationdemo.broadNotification");
        intent.setPackage("edu.cs4730.notificationdemo");  //in API 26, it must be explicit now.  generalized broadcasts are bad...
        //intent.setAction("edu.cs4730.notificationdemo.broadNotification");
        intent.putExtra("mytype", "direct send");
        sendBroadcast(intent);
    }

    public void Noti2broadcast() {
        //using a notification, which the user would click that sends to a broadcastreceiver
        //create the intent for the broadcast
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("edu.cs4730.notificationdemo.broadNotification");
        broadcastIntent.setPackage("edu.cs4730.notificationdemo"); //in API 26, it must be explicit now.
        //adding some extra inform again.
        broadcastIntent.putExtra("mytype", "Broadcast Msg" + NotID);
        //the pendingIntent now use the getBroadcast method.  Other no other changes.
        PendingIntent contentIntent = PendingIntent.getBroadcast(BroadCastRDemo.this, NotID, broadcastIntent, PendingIntent.FLAG_IMMUTABLE);
        //the rest of the notification is just like before.
        Notification noti = new NotificationCompat.Builder(getApplicationContext(), MainActivity.id1)
            //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setSmallIcon(R.drawable.ic_launcher)
            .setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
            .setContentTitle("Notification to BroadcastReciever")   //Title message top row.
            .setContentText("Click me!")  //message when looking at the notification, second row
            .setContentIntent(contentIntent)  //what activity to open.
            .setAutoCancel(true)   //allow auto cancel when pressed.
            .setChannelId(MainActivity.id1)
            .build();  //finally build and return a Notification.

        //Show the notification
        nm.notify(NotID, noti);
        NotID++;

    }

}
