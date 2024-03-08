package edu.cs4730.notiodemo_kt


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import edu.cs4730.notiodemo_kt.databinding.ActivityMainBinding


/**
 * Very simple main to start the service that will then show create notifications for the O notification channels and icon dots.
 *
 * Note for the badges (dots) to work, you need something like google launcher, where badges are supported
 * also turned on, since they can be turned off in some launchers as well.
 */
class MainActivity : AppCompatActivity() {
    object Actions {
        const val id = "test_channel_01"
        const val TAG = "MainActivity"
    }

    private lateinit var mNotificationManager: NotificationManager
    private lateinit var rpl: ActivityResultLauncher<Array<String>>
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

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
        binding.button.setOnClickListener {
            val number5 = Intent(
                baseContext, MyNotiService::class.java
            )
            number5.putExtra("times", 5)
            startService(number5)
        }
        binding.button2.setOnClickListener { makenoti("hi there", 1) }
        createchannel()
        //for the new api 33+ notifications permissions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!allPermissionsGranted()) {
                rpl.launch(REQUIRED_PERMISSIONS)
            }
        }
    }

    private fun createchannel() {
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

// The user-visible name of the channel.
        val name: CharSequence = getString(R.string.channel_name)
        // The user-visible description of the channel.
        val description = getString(R.string.channel_description)
        val importance =
            NotificationManager.IMPORTANCE_DEFAULT //which is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
        val mChannel = NotificationChannel(Actions.id, name, importance)
        // Configure the notification channel.
        mChannel.description = description
        mChannel.enableLights(true)
        // Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
        mChannel.setShowBadge(true)
        mChannel.setVibrationPattern(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        mNotificationManager.createNotificationChannel(mChannel)
    }

    fun makenoti(message: String?, msgcount: Int) {

        //Notification noti = new NotificationCompat.Builder(getApplicationContext())
        val noti = Notification.Builder(applicationContext, Actions.id)
            .setSmallIcon(R.mipmap.ic_launcher) //.setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
            .setChannelId(Actions.id).setContentTitle("Service") //Title message top row.
            .setContentText(message) //message when looking at the notification, second row
            .setAutoCancel(true) //allow auto cancel when pressed.
            .build() //finally build and return a Notification.

        //Show the notification
        mNotificationManager.notify(1, noti)
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

    fun logthis(msg: String) {
        //   logger.append(msg);
        Log.d(Actions.TAG, msg)
    }

}

