package edu.cs4730.notificationdemo2;

import java.util.Calendar;
import java.util.Map;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import edu.cs4730.notificationdemo2.databinding.ActivityMainBinding;

/**
 * This demo calls a broadcast receiver located in NotificationDemo.
 * <p>
 * This maybe helpful.
 * https://developer.android.com/training/notify-user/build-notification.html
 * https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html
 */
public class MainActivity extends AppCompatActivity {
    public static String id = "test_channel_01";
    NotificationManager nm;
    ActivityMainBinding binding;
    int NotID = 1;
    final String TAG = "MainActivity";
    ActivityResultLauncher<String[]> rpl;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.POST_NOTIFICATIONS};


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
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // for notifications permission now required in api 33
        //this allows us to check with multiple permissions, but in this case (currently) only need 1.
        rpl = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> isGranted) {
                boolean granted = true;
                for (Map.Entry<String, Boolean> x : isGranted.entrySet()) {
                    logthis(x.getKey() + " is " + x.getValue());
                    if (!x.getValue()) granted = false;
                }
                if (granted) logthis("Permissions granted for api 33+");
            }
        });


        binding.button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //We are going to call the broadcast receiver from notificationDemo1
                Intent intent = new Intent();
                intent.setAction("edu.cs4730.notificationdemo.broadNotification");
                intent.setPackage("edu.cs4730.notificationdemo"); //in API 26, it must be explicit now.
                //adding some extra inform again.
                intent.putExtra("mytype", "From notificationDemo2");
                sendBroadcast(intent);
            }
        });

        binding.button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //We are going to call the broadcast receiver from notificationDemo1
                //same problem, this may fail if notificationDemo1 is targeted at API26.  I'm working to find a fix.
                Intent intent = new Intent();
                intent.setAction("edu.cs4730.notificationdemo.broadNotification");
                intent.setPackage("edu.cs4730.notificationdemo"); //in API 26, it must be explicit now.
                //adding some extra inform again.
                intent.putExtra("mytype", "alarm from notificationDemo2");

                //---use the AlarmManager to trigger an alarm---
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                //---get current date and time---
                Calendar calendar = Calendar.getInstance();

                //---sets the time for the alarm to trigger in 2 minutes from now---
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 2);
                calendar.set(Calendar.SECOND, 0);


                PendingIntent contentIntent = PendingIntent.getBroadcast(MainActivity.this, NotID, intent, PendingIntent.FLAG_IMMUTABLE);
                Log.i("MainActivity", "Set alarm, I hope");
                Toast.makeText(getApplicationContext(), "Alarm for " + calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();

                //---sets the alarm to trigger---
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), contentIntent);
            }
        });

        binding.button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbarnoti();
            }
        });
        binding.button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activitybarnoti();
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

    public void logthis(String msg) {
        Log.d(TAG, msg);
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

    /**
     * for API 26+ create notification channels
     */
    private void createchannel() {
        NotificationChannel mChannel = new NotificationChannel(id, getString(R.string.channel_name),  //name of the channel
                NotificationManager.IMPORTANCE_LOW);   //importance level
        //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
        // Configure the notification channel.
        mChannel.setDescription(getString(R.string.channel_description));
        // mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        // mChannel.setLightColor(Color.RED);
        // mChannel.enableVibration(true);
        mChannel.setShowBadge(true);
        //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        nm.createNotificationChannel(mChannel);
    }

    /**
     * An example of using a notification with a progress bar.
     */
    public void progressbarnoti() {

        //Normally some long running tasking would do this, but we cheat for the example.
        new Thread(new Runnable() {
            @Override
            public void run() {
                int incr;
                //create the basic notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), id).setOngoing(true)  //user can't remember the notification.
                        .setSmallIcon(R.drawable.ic_announcement_black_24dp).setContentTitle("Progress Bar")   //Title message top row.
                        .setContentText("making progress I hope");  //message when looking at the notification, second row

                // Do the "lengthy" operation 20 times
                for (incr = 0; incr <= 100; incr += 5) {
                    // Sets the progress indicator to a max value, the
                    // current completion percentage, and "determinate"
                    // state

                    mBuilder.setProgress(100, incr, false);
                    // Displays the progress bar for the first time.
                    nm.notify(NotID, mBuilder.build());
                    // Sleeps the thread, simulating an operation
                    // that takes time
                    try {
                        // Sleep for 2 seconds
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        logthis("sleep failure");
                    }
                }
                // When the loop is finished, updates the notification
                mBuilder.setContentText("Download complete").setOngoing(false)  //now the user can remove the notification.
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                nm.notify(NotID, mBuilder.build());
            }
        }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
        NotID++;
    }

    /**
     * Creates a notification that shows there is activity, but not a progress bar.
     * Until the "activity" is done, the notification is not canceled either.
     */
    public void activitybarnoti() {
        //Normally some long running tasking would do this, but we cheat for the example.
        new Thread(new Runnable() {
            @Override
            public void run() {
                int incr;
                //create the basic notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), id).setSmallIcon(R.drawable.ic_announcement_black_24dp).setContentTitle("Activity Indicator")   //Title message top row.
                        .setContentText("animated indicator bar");  //message when looking at the notification, second row

                // Do the "lengthy" operation 20 times
                for (incr = 0; incr <= 100; incr += 10) {
                    // Sets an activity indicator for an operation of indeterminate length
                    mBuilder.setProgress(0, 0, true);
                    // Displays the progress bar for the first time.
                    nm.notify(NotID, mBuilder.build());
                    // Sleeps the thread, simulating an operation
                    // that takes time
                    try {
                        // Sleep for 2 seconds
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        logthis("sleep failure");
                    }
                }
                // When the loop is finished, updates the notification
                mBuilder.setContentText("Download complete").setAutoCancel(true)
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                nm.notify(NotID, mBuilder.build());
            }
        }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
        NotID++;
    }
}
