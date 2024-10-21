package edu.cs4730.notiodemo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Map;

import edu.cs4730.notiodemo.databinding.ActivityMainBinding;


/**
 * Very simple main to start the service that will then show create notifications for the O notification channels and icon dots.
 * <p>
 * Note for the badges (dots) to work, you need something like google launcher, where badges are supported
 * also turned on, since they can be turned off in some launchers as well.
 */

public class MainActivity extends AppCompatActivity {

    // The id of the channel.
    public static String id = "test_channel_01";
    NotificationManager mNotificationManager;

    ActivityResultLauncher<String[]> rpl;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.POST_NOTIFICATIONS};
    String TAG = "MainActivity";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // for notifications permission now required in api 33
        //this allows us to check with multiple permissions, but in this case (currently) only need 1.
        rpl = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> isGranted) {
                    boolean granted = true;
                    for (Map.Entry<String, Boolean> x : isGranted.entrySet()) {
                        logthis(x.getKey() + " is " + x.getValue());
                        if (!x.getValue()) granted = false;
                    }
                    if (granted)
                        logthis("Permissions granted for api 33+");
                }
            }
        );

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent number5 = new Intent(getBaseContext(), MyNotiService.class);
                number5.putExtra("times", 5);
                startService(number5);
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makenoti("hi there", 1);
            }
        });

        createchannel();
        //for the new api 33+ notifications permissions.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!allPermissionsGranted()) {
                rpl.launch(REQUIRED_PERMISSIONS);
            }
        }
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

    //ask for permissions when we start.
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void logthis(String msg) {
        //   logger.append(msg);
        Log.d(TAG, msg);
    }

}
