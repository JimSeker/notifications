package edu.cs4730.notificationdemo2_kt

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cs4730.notificationdemo2_kt.databinding.ActivityMainBinding
import java.util.Calendar

/**
 * This demo calls a broadcast receiver located in NotificationDemo.
 * <p>
 * This maybe helpful.
 * https://developer.android.com/training/notify-user/build-notification.html
 * https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html
 */

class MainActivity : AppCompatActivity() {
    object Actions {
        const val id = "test_channel_01"
        const val TAG = "MainActivity"
    }

    private lateinit var nm: NotificationManager
    private lateinit var binding: ActivityMainBinding
    private var NotID = 1

    private lateinit var rpl: ActivityResultLauncher<Array<String>>
    private  val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

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

        // for notifications permission now required in api 33
        //this allows us to check with multiple permissions, but in this case (currently) only need 1.
        rpl = registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            var granted = true
            for ((key, value) in isGranted) {
                logthis("$key is $value")
                if (!value) granted = false
            }
            if (granted) logthis("Permissions granted for api 33+")
        }
        binding.button1.setOnClickListener { //We are going to call the broadcast receiver from notificationDemo1
            val intent = Intent()
            intent.setAction("edu.cs4730.notificationdemo.broadNotification")
            intent.setPackage("edu.cs4730.notificationdemo_kt") //in API 26, it must be explicit now.
            //adding some extra inform again.
            intent.putExtra("mytype", "From notificationDemo2_kt")
            sendBroadcast(intent)
        }
        binding.button2.setOnClickListener { //We are going to call the broadcast receiver from notificationDemo1
            //same problem, this may fail if notificationDemo1 is targeted at API26.  I'm working to find a fix.
            val intent = Intent()
            intent.setAction("edu.cs4730.notificationdemo.broadNotification")
            intent.setPackage("edu.cs4730.notificationdemo_kt") //in API 26, it must be explicit now.
            //adding some extra inform again.
            intent.putExtra("mytype", "alarm from notificationDemo2")

            //---use the AlarmManager to trigger an alarm---
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            //---get current date and time---
            val calendar = Calendar.getInstance()

            //---sets the time for the alarm to trigger in 2 minutes from now---
            calendar[Calendar.MINUTE] = calendar[Calendar.MINUTE] + 2
            calendar[Calendar.SECOND] = 0
            val contentIntent = PendingIntent.getBroadcast(
                this@MainActivity, NotID, intent, PendingIntent.FLAG_IMMUTABLE
            )
            Log.i("MainActivity", "Set alarm, I hope")
            Toast.makeText(
                applicationContext, "Alarm for " + calendar[Calendar.MINUTE], Toast.LENGTH_SHORT
            ).show()

            //---sets the alarm to trigger---
            alarmManager[AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()] = contentIntent
        }
        binding.button3.setOnClickListener { progressbarnoti() }
        binding.button4.setOnClickListener { activitybarnoti() }
        createchannel()
        //for the new api 33+ notifications permissions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!allPermissionsGranted()) {
                rpl.launch(REQUIRED_PERMISSIONS)
            }
        }
    }

    private fun logthis(msg: String) {
        Log.d(Actions.TAG, msg)
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
     * for API 26+ create notification channels
     */
    private fun createchannel() {
        val mChannel = NotificationChannel(
            Actions.id, getString(R.string.channel_name),  //name of the channel
            NotificationManager.IMPORTANCE_LOW
        ) //importance level
        //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
        // Configure the notification channel.
        mChannel.description = getString(R.string.channel_description)
        // mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        // mChannel.setLightColor(Color.RED);
        // mChannel.enableVibration(true);
        mChannel.setShowBadge(true)
        //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        nm.createNotificationChannel(mChannel)
    }

    /**
     * An example of using a notification with a progress bar.
     */
    private fun progressbarnoti() {

        //Normally some long running tasking would do this, but we cheat for the example.
        Thread {
            var incr: Int
            //create the basic notification
            val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                applicationContext, Actions.id
            ).setOngoing(true) //user can't remember the notification.
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setContentTitle("Progress Bar") //Title message top row.
                .setContentText("making progress I hope") //message when looking at the notification, second row

            // Do the "lengthy" operation 20 times
            incr = 0
            while (incr <= 100) {

                // Sets the progress indicator to a max value, the
                // current completion percentage, and "determinate"
                // state
                mBuilder.setProgress(100, incr, false)
                // Displays the progress bar for the first time.
                nm.notify(NotID, mBuilder.build())
                // Sleeps the thread, simulating an operation
                // that takes time
                try {
                    // Sleep for 2 seconds
                    Thread.sleep((2 * 1000).toLong())
                } catch (e: InterruptedException) {
                    logthis("sleep failure")
                }
                incr += 5
            }
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Download complete")
                .setOngoing(false) //now the user can remove the notification.
                // Removes the progress bar
                .setProgress(0, 0, false)
            nm.notify(NotID, mBuilder.build())
        } // Starts the thread by calling the run() method in its Runnable
            .start()
        NotID++
    }

    /**
     * Creates a notification that shows there is activity, but not a progress bar.
     * Until the "activity" is done, the notification is not canceled either.
     */
    private fun activitybarnoti() {
        //Normally some long running tasking would do this, but we cheat for the example.
        Thread {
            var incr: Int
            //create the basic notification
            val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                applicationContext, Actions.id
            ).setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setContentTitle("Activity Indicator") //Title message top row.
                .setContentText("animated indicator bar") //message when looking at the notification, second row

            // Do the "lengthy" operation 20 times
            incr = 0
            while (incr <= 100) {

                // Sets an activity indicator for an operation of indeterminate length
                mBuilder.setProgress(0, 0, true)
                // Displays the progress bar for the first time.
                nm.notify(NotID, mBuilder.build())
                // Sleeps the thread, simulating an operation
                // that takes time
                try {
                    // Sleep for 2 seconds
                    Thread.sleep((2 * 1000).toLong())
                } catch (e: InterruptedException) {
                    logthis("sleep failure")
                }
                incr += 10
            }
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Download complete")
                .setAutoCancel(true) // Removes the progress bar
                .setProgress(0, 0, false)
            nm.notify(NotID, mBuilder.build())
        } // Starts the thread by calling the run() method in its Runnable
            .start()
        NotID++
    }
}