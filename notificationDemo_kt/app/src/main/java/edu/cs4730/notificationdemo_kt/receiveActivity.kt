package edu.cs4730.notificationdemo_kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.cs4730.notificationdemo_kt.databinding.ActivityReceiveBinding

/**
 * This is the activity that the notification calls.
 * All this does is check to see that the information is in the intent
 * normally you would have some sort of response to the notification
 *
 *
 * http://stackoverflow.com/questions/1198558/how-to-send-parameters-from-a-notification-click-to-an-activity
 * http://mobiforge.com/developing/story/displaying-status-bar-notifications-android
 * http://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html
 * http://mobiforge.com/developing/story/displaying-status-bar-notifications-android
 * http://stackoverflow.com/questions/12006724/set-a-combination-of-vibration-lights-or-sound-for-a-notification-in-android
 * http://developer.android.com/reference/android/app/Notification.html
 */
class receiveActivity : AppCompatActivity() {
    lateinit var binding: ActivityReceiveBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(
            layoutInflater
        )
        setContentView(binding.getRoot())
        var info = "Nothing"
        val extras = intent.extras
        if (extras != null) {
            info = extras.getString("mytype").toString()
            if (info == null) {
                info = "nothing 2"
            }
        }
        binding.logger.text = info
    }
}
