package edu.cs4730.notificationdemo;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/*
 *  This one of two notification demos.  The second one uses the broadcast receiver located in this app.
 *
 *  This maybe helpful.
 *  https://developer.android.com/training/notify-user/build-notification.html
 *  https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html
 */
public class MainActivity extends AppCompatActivity {

    public static String id1 = "test_channel_01";
    public static String id2 = "test_channel_02";
    public static String id3 = "test_channel_03";
    NotificationManager nm;
    int NotID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //call a new activity so we can play with a broadcast receiver.
        findViewById(R.id.btn_mbc).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BroadCastRDemo.class));
            }
        });
        //Icon and message icon
        findViewById(R.id.btn_icon_marquee).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                simplenoti();
            }
        });
        //With Sounds, maybe not work in emulator
        findViewById(R.id.btn_sound).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                extras(1);
            }
        });
        //With Vibrate (doesn't work in emulator)
        findViewById(R.id.btn_vibrate).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                extras(2);
            }
        });
        //With both sounds and vibrate, not going to work in emulator
        findViewById(R.id.btn_both).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                extras(3);
            }
        });

        //With both sounds, vibrate, lights, not going to work in emulator
        findViewById(R.id.btn_light).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                extras(4);
            }
        });
        //Notification with Action buttons
        findViewById(R.id.btn_actions).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionbuttons();
            }
        });
        //With expanded text
        findViewById(R.id.btn_expand_text).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                expandtext();
            }
        });
        //With expanded text/image
        findViewById(R.id.btn_expand_image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                expandimage();
            }
        });
        //similar to inbox notifications
        findViewById(R.id.btn_expand_inbox).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                expandinbox();
            }
        });
        //similar to inbox notifications
        findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancels and removed last notification programming, user doesn't remove it.
                if (NotID > 1) {
                    nm.cancel(NotID);
                    NotID--;
                }
                //Remember use notify with the same ID number, it will just update notification
                //assuming the user hasn't removed it already.
            }
        });
        //notification 2 minutes in the future
        findViewById(R.id.btn_alarm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                notlater();
            }
        });

        findViewById(R.id.noti_and5).setOnClickListener(new OnClickListener() {
            //create a android 5/lollipop notification popup.
            @Override
            public void onClick(View view) {
                and5_notificaiton();
            }
        });
        createchannel();
    }

    /*
     * for API 26+ create notification channels
     */
    private void createchannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id1,
                    getString(R.string.channel_name),  //name of the channel
                    NotificationManager.IMPORTANCE_DEFAULT);   //importance level
            //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description));
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);

            //a medium level channel
            mChannel = new NotificationChannel(id2,
                    getString(R.string.channel_name2),  //name of the channel
                    NotificationManager.IMPORTANCE_LOW);   //importance level
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description2));
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);

            //a urgent level channel
            mChannel = new NotificationChannel(id3,
                    getString(R.string.channel_name2),  //name of the channel
                    NotificationManager.IMPORTANCE_HIGH);   //importance level
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description3));
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);
        }
    }


    /*
     * create a notification with a icon and message, plus a title.
     */
    public void simplenoti() {
        Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.putExtra("mytype", "simple" + NotID); //not required, but used in this example.
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);
        //Create a new notification. The construction Notification(int icon, CharSequence tickerText, long when) is deprecated.
        //If you target API level 11 or above, use Notification.Builder instead
        //With the second parameter, it would show a marquee
        Notification noti = new NotificationCompat.Builder(getApplicationContext(), id1)
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
                .setContentTitle("Marquee or Title")   //Title message top row.
                .setContentText("Message, this has only a small icon.")  //message when looking at the notification, second row
                .setContentIntent(contentIntent)  //what activity to open.
                .setAutoCancel(true)   //allow auto cancel when pressed.
                .setChannelId(id2)
                .build();  //finally build and return a Notification.

        //Show the notification
        nm.notify(NotID, noti);
        NotID++;
    }

    /*
     * sets different types of flags
     * 1 sounds
     * 2 vibrate
     * 3 both
     * 4 other
     */
    public void extras(int which) {
        /*
		 * Changing this to a builder, instead of notification, so some things can be set separately.
		 */
        String msg = "";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),id1)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("My notification")
                .setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
                .setAutoCancel(true)   //allow auto cancel when pressed.
                .setContentTitle("With Extras")   //Title message top row.
                .setContentText("Hello World!")
                .setChannelId(id1);


        switch (which) {
            case 1:  //sound
                msg = "Sounds only";
                builder.setDefaults(Notification.DEFAULT_SOUND);
                //or harder way
                //builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //For you own sound in your package
                //builder.setSound(Uri.parse("android.resource://com.my.package/" + R.raw.sound));
                break;
            case 2: //Vibrate
                //NOTE, Need the <uses-permission android:name="android.permission.VIBRATE"></uses-permission> in manifest or force Close
                msg = "Vibrate";
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                break;
            case 3: //both
                msg = "Both sound and vibrate";
                builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
                break;
            case 4:
                msg = "and Lights";
                builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
                // Notification.DEFAULT_ALL  does the same thing.
                break;

        }

        Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.putExtra("mytype", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);
        builder.setContentIntent(contentIntent);  //what activity to open.
        builder.setContentText(msg);

        Notification noti = builder.build();
        if (which == 4) {  //really annoy the user!
            noti.flags = Notification.FLAG_INSISTENT;
        }


        //Show the notification
        nm.notify(NotID, noti);
        NotID++;
    }

    /*
     * create a notification with extra buttons.
     * Note, that the intents each ahve the own number, otherwise, they are did the same thing.
     *   something about android conserving memory, since the won't numbered differently.
     */

    public void actionbuttons() {

        //default, user clicks the notification (not the buttons)
        Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.setAction("Click");
        notificationIntent.putExtra("mytype", "No cursing Notification");
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);
        //first button
        Intent notificationIntent1 = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.setAction("button1");
        notificationIntent1.putExtra("mytype", "Action Button1");
        PendingIntent contentIntent1 = PendingIntent.getActivity(MainActivity.this, NotID + 1, notificationIntent1, 0);
        //button 2
        Intent notificationIntent2 = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.setAction("button2");
        notificationIntent2.putExtra("mytype", "Action Button2");
        PendingIntent contentIntent2 = PendingIntent.getActivity(MainActivity.this, NotID + 2, notificationIntent2, 0);
        //button 2
        Intent notificationIntent3 = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.setAction("button3");
        notificationIntent3.putExtra("mytype", "Action Button3");
        PendingIntent contentIntent3 = PendingIntent.getActivity(MainActivity.this, NotID + 3, notificationIntent3, 0);

        //Set up the notification
        Notification noti = new NotificationCompat.Builder(getApplicationContext(),id1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("This is a notification marquee")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Action Buttons")
                .setContentText("has 3 different action buttons")
                .setContentIntent(contentIntent)
                //At most three action buttons can be added
                .addAction(android.R.drawable.ic_menu_camera, "Action 1", contentIntent1)
                .addAction(android.R.drawable.ic_menu_compass, "Action 2", contentIntent2)
                .addAction(android.R.drawable.ic_menu_info_details, "Action 3", contentIntent3)
                .setAutoCancel(true)
                .setChannelId(id1)
                .build();

        //Show the notification
        nm.notify(NotID, noti);
        NotID += 4;

    }

    public void expandtext() {
        //Set the activity to be launch when selected
        Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.putExtra("mytype", "Expand text");
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);

        //Makes the Notification Builder
        NotificationCompat.Builder build = new NotificationCompat.Builder(getApplicationContext(),id1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("This is a notification marquee")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Message Title 7")
                .setContentText("Message Content 7 will have more space for text")
                .setContentIntent(contentIntent)
                //At most three action buttons can be added (Optional)
                .addAction(android.R.drawable.ic_menu_edit, "Edit", contentIntent)  //Maybe a different intent here?  depends.
                .setChannelId(id1)
                .setAutoCancel(true);

        //Set up the notification
        Notification noti = new NotificationCompat.BigTextStyle(build)
                .bigText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent consequat dictum sem. Aliquam lacus turpis, aliquet id dictum id, fringilla nec tortor. Sed consectetur eros vel lectus ornare a vulputate dui eleifend. Integer ac lorem ipsum, in placerat ligula. Mauris et dictum risus. Aliquam vestibulum nibh vitae nibh vehicula nec ullamcorper sapien feugiat. Proin vel porttitor diam. In laoreet eleifend ipsum eget lobortis. Suspendisse est magna, egestas non sodales ac, eleifend sit amet tellus.")
                .build();

        //Show the notification
        nm.notify(NotID, noti);
        NotID++;
    }

    public void expandimage() {
        //Set the activity to be launch when selected
        Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.putExtra("mytype", "Expand Image");
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);

        //Makes the Notification Builder
        NotificationCompat.Builder build = new NotificationCompat.Builder(getApplicationContext(),id1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("This is a notification marquee")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Message Title 8")
                .setContentText("Message Content 8 will have a large image")
                .setContentIntent(contentIntent)
                //At most three action buttons can be added (Optional)
                .addAction(android.R.drawable.ic_menu_edit, "Edit", contentIntent)   //should be a different intent here.
                .addAction(android.R.drawable.ic_menu_share, "Share", contentIntent) //should be a different intent here.
                .setChannelId(id1)
                .setAutoCancel(true);

        //Set up the notification
        Notification noti = new NotificationCompat.BigPictureStyle(build)
                .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.jelly_bean))
                .build();

        //Show the notification
        nm.notify(NotID, noti);
        NotID++;
    }

    public void expandinbox() {
        //Set the activity to be launch when selected
        Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.putExtra("mytype", "Expand Inbox");
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);

        //Makes the Notification Builder
        NotificationCompat.Builder build = new NotificationCompat.Builder(getApplicationContext(),id1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("This is a notification marquee")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Message Title 9")
                .setContentText("You have many emails")
                .setContentIntent(contentIntent)
                .setChannelId(id1)
                .setAutoCancel(true);

        //Set up the notification
        Notification noti = new NotificationCompat.InboxStyle(build)
                .addLine("Cupcake: Hi, how are you?")
                .addLine("Dount: LOL XD")
                .addLine("Eclair: Here is a funny joke: http://...")
                .addLine("Froyo: You have a new message.")
                .addLine("Gingerbread: I really love eating gingerbread.")
                .addLine("Honeycomb: Why Google only make me for tablets?")
                .addLine("ICS: I am nice.")
                .addLine("Jelly Bean: Yummy")
                .setSummaryText("+999 more emails")
                .build();

        //Show the notification
        nm.notify(NotID, noti);
        NotID++;

    }

    public void notlater() {

        //---use the AlarmManager to trigger an alarm---
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //---get current date and time---
        Calendar calendar = Calendar.getInstance();

        //---sets the time for the alarm to trigger in 2 minutes from now---
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 2);
        calendar.set(Calendar.SECOND, 0);

        //---PendingIntent to launch activity when the alarm triggers-

        //Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        Intent notificationIntent = new Intent("edu.cs4730.notificationdemo.DisplayNotification");
        notificationIntent.putExtra("NotifID", NotID);

        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);
        Log.i("MainACtivity", "Set alarm, I hope");


        //---sets the alarm to trigger---
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), contentIntent);
        NotID++;
    }


    //creates a notification for lollipop with a popup/heads up message..
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void and5_notificaiton() {
        Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        notificationIntent.putExtra("mytype", "iconmsg" + NotID);
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, NotID, notificationIntent, 0);
        Notification noti = new NotificationCompat.Builder(getApplicationContext(),id3)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
                .setContentTitle("Lollipop notificaiton")   //Title message top row.
                .setContentText("This should be an annoying heads up message.")  //message when looking at the notification, second row
                //the folowing 2 lines cause it to show up as popup message at the top in android 5 systems.
                .setPriority(Notification.PRIORITY_MAX)  //could also be PRIORITY_HIGH.  needed for LOLLIPOP, M and N.  But not Oreo
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})  //for the heads/pop up must have sound or vibrate
                .setVisibility(Notification.VISIBILITY_PUBLIC)  //VISIBILITY_PRIVATE or VISIBILITY_SECRET
                .setContentIntent(contentIntent)  //what activity to open.
                .setAutoCancel(true)   //allow auto cancel when pressed.
                .setChannelId(id3)  //Oreo notifications
                .build();  //finally build and return a Notification.

        //Show the notification
        nm.notify(NotID, noti);
        NotID++;
    }

}
