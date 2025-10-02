package edu.cs4730.notificationdemo_kt

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cs4730.notificationdemo_kt.databinding.ActivityBroadcastBinding

class BroadCastRDemo : AppCompatActivity() {
    lateinit var nm: NotificationManager
    var NotID = 1
    lateinit var binding: ActivityBroadcastBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.main
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        binding.btnBc.setOnClickListener { viaBroadcast() }
        binding.btnNotibc.setOnClickListener { Noti2broadcast() }
    }

    fun viaBroadcast() {
        //in the simplest form, create an intent with the action set for the broadcastreceiver and send it.
        val intent = Intent(MainActivity.Actions.ACTION)
        intent.setPackage(applicationContext.packageName) //in API 26, it must be explicit now.  generalized broadcasts are bad...
        intent.putExtra("mytype", "direct send")
        sendBroadcast(intent)
    }

    @SuppressLint("LaunchActivityFromNotification")
    fun Noti2broadcast() {
        //using a notification, which the user would click that sends to a broadcastreceiver
        //create the intent for the broadcast
        val broadcastIntent = Intent()
        broadcastIntent.setAction("edu.cs4730.notificationdemo_kt.broadNotification")
        broadcastIntent.setPackage(applicationContext.packageName) //in API 26, it must be explicit now.
        //adding some extra inform again.
        broadcastIntent.putExtra("mytype", "Broadcast Msg$NotID")
        //the pendingIntent now use the getBroadcast method.  Other no other changes.
        val contentIntent = PendingIntent.getBroadcast(
            this@BroadCastRDemo, NotID, broadcastIntent, PendingIntent.FLAG_IMMUTABLE
        )
        //the rest of the notification is just like before.
        val noti = NotificationCompat.Builder(
            applicationContext, MainActivity.Actions.id1
        ) //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis()) //When the event occurred, now, since noti are stored by time.
            .setContentTitle("Notification to BroadcastReciever kt") //Title message top row.
            .setContentText("Click me!") //message when looking at the notification, second row
            .setContentIntent(contentIntent) //what activity to open.
            .setAutoCancel(true) //allow auto cancel when pressed.
            .setChannelId(MainActivity.Actions.id1)
            .build() //finally build and return a Notification.

        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }
}
