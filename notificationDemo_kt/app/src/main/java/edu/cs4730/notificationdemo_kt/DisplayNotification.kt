package edu.cs4730.notificationdemo_kt

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

/**
 * This activity will not display to the screen (hopefully).  It just sets a notification
 * and then exits (finish())
 *
 *
 * http://mobiforge.com/developing/story/displaying-status-bar-notifications-android
 */
class DisplayNotification : AppCompatActivity() {
    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //---get the notification ID for the notification; 
        // passed in by the MainActivity---
        val notifID = intent.extras!!.getInt("NotifID")
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //---PendingIntent to launch activity if the user selects 
        // the notification---
        val notificationIntent = Intent(applicationContext, receiveActivity::class.java)
        notificationIntent.putExtra("mytype", "2 minutes later?")
        val contentIntent = PendingIntent.getActivity(
            this,
            notifID,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        //create the notification
        val notif = NotificationCompat.Builder(applicationContext, MainActivity.Actions.id1)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis()) //When the event occurred, now, since noti are stored by time.
            .setContentTitle("Time's up!") //Title message top row.
            .setContentText("This is your alert, courtesy of the AlarmManager") //message when looking at the notification, second row
            .setContentIntent(contentIntent) //what activity to open.
            .setAutoCancel(true) //allow auto cancel when pressed.
            .setChannelId(MainActivity.Actions.id1)
            .build() //finally build and return a Notification.

        //Show the notification
        nm.notify(notifID, notif)
        //---destroy the activity---
        finish()
    }
}
