package edu.cs4730.notificationdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.cs4730.notificationdemo.databinding.ActivityReceiveBinding;

/**
 * This is the activity that the notification calls.
 * All this does is check to see that the information is in the intent
 * normally you would have some sort of response to the notification
 * <p>
 * http://stackoverflow.com/questions/1198558/how-to-send-parameters-from-a-notification-click-to-an-activity
 * http://mobiforge.com/developing/story/displaying-status-bar-notifications-android
 * http://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html
 * http://mobiforge.com/developing/story/displaying-status-bar-notifications-android
 * http://stackoverflow.com/questions/12006724/set-a-combination-of-vibration-lights-or-sound-for-a-notification-in-android
 * http://developer.android.com/reference/android/app/Notification.html
 */

public class receiveActivity extends AppCompatActivity {
    ActivityReceiveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String info = "Nothing";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            info = extras.getString("mytype");
            if (info == null) {
                info = "nothing 2";
            }
        }
        binding.logger.setText(info);
    }
}
