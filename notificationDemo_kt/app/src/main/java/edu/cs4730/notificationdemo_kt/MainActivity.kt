package edu.cs4730.notificationdemo_kt

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cs4730.notificationdemo_kt.databinding.ActivityMainBinding
import java.util.Calendar

/**
 * This one of two notification demos.  The second one uses the broadcast receiver located in this app.
 * <p>
 * This maybe helpful.
 * https://developer.android.com/training/notify-user/build-notification.html
 * https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html
 */

class MainActivity : AppCompatActivity() {
    object Actions {
        const val id1 = "test_channel_01"
        const val id2 = "test_channel_02"
        const val id3 = "test_channel_03"
        const val TAG = "MainActivity"
        const val ACTION = "edu.cs4730.notificationdemo.broadNotification"
    }

    private lateinit var nm: NotificationManager
    private var NotID = 1
    private lateinit var rpl: ActivityResultLauncher<Array<String>>
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(binding.main.id)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }


        nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        rpl = registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGrant ->
            var granted = true
            for ((key, value) in isGrant!!) {
                logthis("$key is $value")
                if (!value) granted = false
            }
            if (granted) logthis("Permissions granted for api 33+")
        }

        //call a new activity so we can play with a broadcast receiver.


        //call a new activity so we can play with a broadcast receiver.
        binding.btnMbc.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext, BroadCastRDemo::class.java
                )
            )
        }
        //Icon and message icon
        binding.btnIconMarquee.setOnClickListener { simplenoti() }
        //With Sounds, maybe not work in emulator
        binding.btnSound.setOnClickListener { extras(1) }
        //With Vibrate (doesn't work in emulator)
        binding.btnVibrate.setOnClickListener { extras(2) }

        //With both sounds and vibrate, not going to work in emulator
        binding.btnBoth.setOnClickListener { extras(3) }
        //With both sounds, vibrate, lights, not going to work in emulator
        binding.btnLight.setOnClickListener { extras(4) }

        //Notification with Action buttons
        binding.btnActions.setOnClickListener { actionbuttons() }
        //With expanded text
        binding.btnExpandText.setOnClickListener { expandtext() }
        //With expanded text/image
        binding.btnExpandImage.setOnClickListener { expandimage() }
        //similar to inbox notifications
        binding.btnExpandInbox.setOnClickListener { expandinbox() }
        //similar to inbox notifications
        binding.btnCancel.setOnClickListener {
            //cancels and removed last notification programming, user doesn't remove it.
            if (NotID > 1) {
                nm.cancel(NotID)
                NotID--
            }
            //Remember use notify with the same ID number, it will just update notification
            //assuming the user hasn't removed it already.
        }
        //notification 2 minutes in the future
        binding.btnAlarm.setOnClickListener { notlater() }

        binding.notiAnd5.setOnClickListener { and5_notificaiton() }
        createchannel()
        //for the new api 33+ notifications permissions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!allPermissionsGranted()) {
                rpl.launch(REQUIRED_PERMISSIONS)
            }
        }
    }

    /**
     * creates notification channels (required for API 26+)
     */
    private fun createchannel() {
        var mChannel = NotificationChannel(
            Actions.id1, getString(R.string.channel_name),  //name of the channel
            NotificationManager.IMPORTANCE_DEFAULT
        ) //importance level
        //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
        // Configure the notification channel.
        mChannel.description = getString(R.string.channel_description)
        mChannel.enableLights(true)
        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
        mChannel.setShowBadge(true)
        mChannel.setVibrationPattern(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        nm.createNotificationChannel(mChannel)

        //a medium level channel
        mChannel = NotificationChannel(
            Actions.id2, getString(R.string.channel_name2),  //name of the channel
            NotificationManager.IMPORTANCE_LOW
        ) //importance level
        // Configure the notification channel.
        mChannel.description = getString(R.string.channel_description2)
        mChannel.enableLights(true)
        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        mChannel.lightColor = Color.BLUE
        mChannel.enableVibration(true)
        mChannel.setShowBadge(true)
        mChannel.setVibrationPattern(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        nm.createNotificationChannel(mChannel)

        //a urgent level channel
        mChannel = NotificationChannel(
            Actions.id3, getString(R.string.channel_name2),  //name of the channel
            NotificationManager.IMPORTANCE_HIGH
        ) //importance level
        // Configure the notification channel.
        mChannel.description = getString(R.string.channel_description3)
        mChannel.enableLights(true)
        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        mChannel.lightColor = Color.GREEN
        mChannel.enableVibration(true)
        mChannel.setShowBadge(true)
        mChannel.setVibrationPattern(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        nm.createNotificationChannel(mChannel)
    }


    //ask for permissions when we start.
    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * create a notification with a icon and message, plus a title.
     */
    private fun simplenoti() {
        val notificationIntent = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.putExtra(
            "mytype", "simple$NotID"
        ) //not required, but used in this example.
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        //Create a new notification. The construction Notification(int icon, CharSequence tickerText, long when) is deprecated.
        //If you target API level 11 or above, use Notification.Builder instead
        //With the second parameter, it would show a marquee
        val noti: Notification = NotificationCompat.Builder(applicationContext, Actions.id2)
            .setSmallIcon(R.drawable.ic_announcement_black_24dp) //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setWhen(System.currentTimeMillis()) //When the event occurred, now, since noti are stored by time.
            .setContentTitle("Marquee or Title") //Title message top row.
            .setContentText("Message, this has only a small icon.") //message when looking at the notification, second row
            .setContentIntent(contentIntent) //what activity to open.
            .setAutoCancel(true) //allow auto cancel when pressed.
            .setChannelId(Actions.id2).build() //finally build and return a Notification.

        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }

    /*
     * sets different types of flags
     * 1 sounds
     * 2 vibrate
     * 3 both
     * 4 other
     */
    private fun extras(which: Int) {
        var msg = ""
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, Actions.id1)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("My notification").setWhen(
                    System.currentTimeMillis()
                ) //When the event occurred, now, since noti are stored by time.
                .setAutoCancel(true) //allow auto cancel when pressed.
                .setContentTitle("With Extras") //Title message top row.
                .setContentText("Hello World!").setChannelId(Actions.id1)
        when (which) {
            1 -> {
                msg = "Sounds only"
                builder.setDefaults(Notification.DEFAULT_SOUND)
            }

            2 -> {
                //NOTE, Need the <uses-permission android:name="android.permission.VIBRATE"></uses-permission> in manifest or force Close
                msg = "Vibrate"
                builder.setDefaults(Notification.DEFAULT_VIBRATE)
            }

            3 -> {
                msg = "Both sound and vibrate"
                builder.setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            }

            4 -> {
                msg = "and Lights"
                builder.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            }
        }
        val notificationIntent = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.putExtra("mytype", msg)
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(contentIntent) //what activity to open.
        builder.setContentText(msg)
        val noti = builder.build()
        //This will still work even if the channel is set differently.
        if (which == 4) {  //really annoy the user!
            noti.flags = Notification.FLAG_INSISTENT
        }


        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }

    /**
     * create a notification with extra buttons.
     * Note, that the intents each ahve the own number, otherwise, they are did the same thing.
     * something about android conserving memory, since the won't numbered differently.
     */
    private fun actionbuttons() {

        //default, user clicks the notification (not the buttons)
        val notificationIntent = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.setAction("Click")
        notificationIntent.putExtra("mytype", "No cursing Notification")
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        //first button
        val notificationIntent1 = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.setAction("button1")
        notificationIntent1.putExtra("mytype", "Action Button1")
        val contentIntent1 = PendingIntent.getActivity(
            this@MainActivity, NotID + 1, notificationIntent1, PendingIntent.FLAG_IMMUTABLE
        )
        //button 2
        val notificationIntent2 = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.setAction("button2")
        notificationIntent2.putExtra("mytype", "Action Button2")
        val contentIntent2 = PendingIntent.getActivity(
            this@MainActivity, NotID + 2, notificationIntent2, PendingIntent.FLAG_IMMUTABLE
        )
        //button 2
        val notificationIntent3 = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.setAction("button3")
        notificationIntent3.putExtra("mytype", "Action Button3")
        val contentIntent3 = PendingIntent.getActivity(
            this@MainActivity, NotID + 3, notificationIntent3, PendingIntent.FLAG_IMMUTABLE
        )

        //Set up the notification
        val noti: Notification = NotificationCompat.Builder(applicationContext, Actions.id1)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setSmallIcon(R.drawable.ic_announcement_black_24dp)
            .setTicker("This is a notification marquee").setWhen(
                System.currentTimeMillis()
            ).setContentTitle("Action Buttons").setContentText("has 3 different action buttons")
            .setContentIntent(contentIntent) //At most three action buttons can be added
            .addAction(android.R.drawable.ic_menu_camera, "Action 1", contentIntent1)
            .addAction(android.R.drawable.ic_menu_compass, "Action 2", contentIntent2)
            .addAction(android.R.drawable.ic_menu_info_details, "Action 3", contentIntent3)
            .setAutoCancel(true).setChannelId(Actions.id1).build()

        //Show the notification
        nm.notify(NotID, noti)
        NotID += 4
    }

    /**
     * Create a notification with extra text option.
     */
    private fun expandtext() {
        //Set the activity to be launch when selected
        val notificationIntent = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.putExtra("mytype", "Expand text")
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        //Makes the Notification Builder
        val build: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, Actions.id1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("This is a notification marquee").setWhen(
                    System.currentTimeMillis()
                ).setContentTitle("Message Title 7")
                .setContentText("Message Content 7 will have more space for text")
                .setContentIntent(contentIntent) //At most three action buttons can be added (Optional)
                .addAction(
                    android.R.drawable.ic_menu_edit, "Edit", contentIntent
                ) //Maybe a different intent here?  depends.
                .setChannelId(Actions.id1).setAutoCancel(true)

        //Set up the notification
        val noti = NotificationCompat.BigTextStyle(build)
            .bigText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent consequat dictum sem. Aliquam lacus turpis, aliquet id dictum id, fringilla nec tortor. Sed consectetur eros vel lectus ornare a vulputate dui eleifend. Integer ac lorem ipsum, in placerat ligula. Mauris et dictum risus. Aliquam vestibulum nibh vitae nibh vehicula nec ullamcorper sapien feugiat. Proin vel porttitor diam. In laoreet eleifend ipsum eget lobortis. Suspendisse est magna, egestas non sodales ac, eleifend sit amet tellus.")
            .build()

        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }

    /**
     * Create a notification with mostly a picture, could have buttons too.
     */
    private fun expandimage() {
        //Set the activity to be launch when selected
        val notificationIntent = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.putExtra("mytype", "Expand Image")
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        //Makes the Notification Builder
        val build: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, Actions.id1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("This is a notification marquee").setWhen(
                    System.currentTimeMillis()
                ).setContentTitle("Message Title 8")
                .setContentText("Message Content 8 will have a large image")
                .setContentIntent(contentIntent) //At most three action buttons can be added (Optional)
                .addAction(
                    android.R.drawable.ic_menu_edit, "Edit", contentIntent
                ) //should be a different intent here.
                .addAction(
                    android.R.drawable.ic_menu_share, "Share", contentIntent
                ) //should be a different intent here.
                .setChannelId(Actions.id1).setAutoCancel(true)

        //Set up the notification
        val noti = NotificationCompat.BigPictureStyle(build)
            .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.jelly_bean)).build()

        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }

    /**
     * Create a notification that looks more like a email/inbox notification.
     */
    private fun expandinbox() {
        //Set the activity to be launch when selected
        val notificationIntent = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.putExtra("mytype", "Expand Inbox")
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        //Makes the Notification Builder
        val build: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, Actions.id1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("This is a notification marquee").setWhen(
                    System.currentTimeMillis()
                ).setContentTitle("Message Title 9").setContentText("You have many emails")
                .setContentIntent(contentIntent).setChannelId(Actions.id1).setAutoCancel(true)

        //Set up the notification
        val noti = NotificationCompat.InboxStyle(build).addLine("Cupcake: Hi, how are you?")
            .addLine("Dount: LOL XD").addLine("Eclair: Here is a funny joke: http://...")
            .addLine("Froyo: You have a new message.")
            .addLine("Gingerbread: I really love eating gingerbread.")
            .addLine("Honeycomb: Why Google only make me for tablets?").addLine("ICS: I am nice.")
            .addLine("Jelly Bean: Yummy").setSummaryText("+999 more emails").build()

        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }

    /**
     * This creates a "notification" to happen later.  Actually sets the alarm alarm to wake up
     * this code in 2 minutes and set a notification then.
     */
    private fun notlater() {

        //---use the AlarmManager to trigger an alarm---
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        //---get current date and time---
        val calendar = Calendar.getInstance()

        //---sets the time for the alarm to trigger in 2 minutes from now---
        calendar[Calendar.MINUTE] = calendar[Calendar.MINUTE] + 2
        calendar[Calendar.SECOND] = 0

        //---PendingIntent to launch activity when the alarm triggers-

        //Intent notificationIntent = new Intent(getApplicationContext(), receiveActivity.class);
        val notificationIntent = Intent("edu.cs4730.notificationdemo.DisplayNotification")
        notificationIntent.putExtra("NotifID", NotID)
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        Log.i("MainACtivity", "Set alarm, I hope")


        //---sets the alarm to trigger---
        alarmManager[AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()] = contentIntent
        NotID++
    }

    /**
     * Create a popup notification.
     */
    private fun and5_notificaiton() {
        val notificationIntent = Intent(
            applicationContext, receiveActivity::class.java
        )
        notificationIntent.putExtra("mytype", "iconmsg$NotID")
        val contentIntent = PendingIntent.getActivity(
            this@MainActivity, NotID, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val noti: Notification = NotificationCompat.Builder(
            applicationContext, Actions.id3
        ) //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis()) //When the event occurred, now, since noti are stored by time.
            .setContentTitle("Lollipop notificaiton") //Title message top row.
            .setContentText("This should be an annoying heads up message.") //message when looking at the notification, second row
            //the following 2 lines cause it to show up as popup message at the top in android 5 systems.
            .setPriority(NotificationManager.IMPORTANCE_HIGH) //could also be PRIORITY_HIGH.  needed for LOLLIPOP, M and N.  But not Oreo
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000, 1000, 1000
                )
            ) //for the heads/pop up must have sound or vibrate
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //VISIBILITY_PRIVATE or VISIBILITY_SECRET
            .setContentIntent(contentIntent) //what activity to open.
            .setAutoCancel(true) //allow auto cancel when pressed.
            .setChannelId(Actions.id3) //Oreo notifications
            .build() //finally build and return a Notification.

        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }


    private fun logthis(msg: String) {
        Log.d(Actions.TAG, msg)
    }

}