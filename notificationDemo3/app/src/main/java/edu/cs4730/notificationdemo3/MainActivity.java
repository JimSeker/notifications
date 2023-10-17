package edu.cs4730.notificationdemo3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Map;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.Person;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import edu.cs4730.notificationdemo3.databinding.ActivityMainBinding;

/**
 * while most of the work of creating the notifications is in the fragment, we need to create
 * three receivers that are here for the read, delete, reply intents.
 * <p>
 * the reply receiver also updates the notification that we have dealt with he replay message as well.
 * <p>
 * As note, with Android 8.x Oreo, they have changed how receivers work in the background.  This example
 * may fail to set "replied" if the app is in the background.
 */


public class MainActivity extends AppCompatActivity {
    public static String id = "test_channel_01";
    String TAG = "MainActivity";
    private NotificationManager mNotificationManager;
    private NotificationManagerCompat mNotificationManagerCompat;
    protected static final String ACTION_NOTIFICATION_DELETE = "edu.cs4730.notificationdemo3.delete";
    public static final String READ_ACTION = "edu.cs4730.notification3.ACTION_MESSAGE_READ";
    public static final String REPLY_ACTION = "edu.cs4730.notification3.ACTION_MESSAGE_REPLY";
    public static final String CONVERSATION_ID = "conversation_id";
    public static final String EXTRA_REMOTE_REPLY = "extra_remote_reply";

    public PendingIntent mDeletePendingIntent;
    private static final int REQUEST_CODE = 2323;

    ActivityMainBinding binding;

    int NotificationNum = 1;
    ActivityResultLauncher<String[]> rpl;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.POST_NOTIFICATIONS};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //to send notifications and everything else.
        mNotificationManagerCompat = NotificationManagerCompat.from(this);

        // Create a PendingIntent to be fired upon deletion of a Notification.
        Intent deleteIntent = new Intent(MainActivity.ACTION_NOTIFICATION_DELETE);
        mDeletePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, deleteIntent, PendingIntent.FLAG_IMMUTABLE);

        // Supply actions to the button that is displayed on screen.
        binding.addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
            }
        });
        createchannel();  //setup channels if needed.
        //for the new api 33+ notifications permissions.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!allPermissionsGranted()) {
                rpl.launch(REQUIRED_PERMISSIONS);
            }
        }
    }


    //for updates when a notification has been deleted.
    private BroadcastReceiver mDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNumberOfNotifications();
        }
    };

    //for when a message has been read
    private BroadcastReceiver mReadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceiveRead");
            int conversationId = intent.getIntExtra(CONVERSATION_ID, -1);
            if (conversationId != -1) {
                NotificationRead(conversationId);
            }
        }
    };

    /**
     * Requests the current number of notifications from the {@link NotificationManager} and
     * display them to the user.
     */
    protected void updateNumberOfNotifications() {
        //get getting length, but getActiveNotifications() return an array of the notifications, which you can find out the info about the notifications.
        int numberOfNotifications = mNotificationManager.getActiveNotifications().length;

        binding.numNoti.setText("Number of Active notifications is: " + numberOfNotifications);
        Log.i(TAG, "Number of Active notifications is: " + numberOfNotifications);
    }

    // Creates an intent that will be triggered when a message is marked as read.
    private Intent getMessageReadIntent(int id) {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(MainActivity.READ_ACTION)
                .putExtra(MainActivity.CONVERSATION_ID, id);
    }

    // Creates an Intent that will be triggered when a voice reply is received.
    private Intent getMessageReplyIntent(int conversationId) {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(MainActivity.REPLY_ACTION)
                .putExtra(MainActivity.CONVERSATION_ID, conversationId);
    }


    @SuppressLint({"LaunchActivityFromNotification", "UnspecifiedImmutableFlag"})
    void createNotification() {
        // A pending Intent for reads
        PendingIntent readPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            readPendingIntent = PendingIntent.getBroadcast(this,
                    NotificationNum,
                    getMessageReadIntent(NotificationNum),
                    PendingIntent.FLAG_MUTABLE);
        } else {
            readPendingIntent = PendingIntent.getBroadcast(this,
                    NotificationNum,
                    getMessageReadIntent(NotificationNum),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // A choices list.
        String[] choices = new String[]{"No", "Yes", "Maybe", "Go away!"};
        // Build a RemoteInput for receiving voice input in a Car Notification or text input on
        // devices that support text input (like devices on Android N and above).
        RemoteInput remoteInput = new RemoteInput.Builder(MainActivity.EXTRA_REMOTE_REPLY)
                .setLabel("Reply")
                .setChoices(choices)
                .build();

        // Building a Pending Intent for the reply action to trigger
        PendingIntent replyIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            replyIntent = PendingIntent.getBroadcast(this,
                    NotificationNum,
                    getMessageReplyIntent(NotificationNum),
                    PendingIntent.FLAG_MUTABLE);
        } else {
            replyIntent = PendingIntent.getBroadcast(this,
                    NotificationNum,
                    getMessageReplyIntent(NotificationNum),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Build an Android N compatible Remote Input enabled action.
        NotificationCompat.Action actionReplyByRemoteInput = new NotificationCompat.Action.Builder(
                R.mipmap.notification_icon, "Reply", replyIntent)
                .addRemoteInput(remoteInput)
                .build();

        //Create a Person object, to use int he messagingStyle object below.
        Person sender = new Person.Builder()
                .setName("Jim")
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.id)
                .setSmallIcon(R.mipmap.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.android_contact))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Jim ")
                .setContentIntent(readPendingIntent)
                .setDeleteIntent(mDeletePendingIntent)
                .setChannelId(MainActivity.id)
                .setStyle(new NotificationCompat.MessagingStyle(sender)
                        .addMessage("Are you working?", System.currentTimeMillis(), sender)
                )
                .addAction(actionReplyByRemoteInput);

        logthis("Sending notification " + NotificationNum + "\n");


        //I do check in onCreate.  yes, it could be removed between then, but don't care.
        mNotificationManagerCompat.notify(NotificationNum, builder.build());
        NotificationNum++;
        //update the number of notifications.
        updateNumberOfNotifications();
    }


    /*
     * helper method that the mainactivity can call, to update the logger that a message has been read.
     */
    public void NotificationRead(int id) {
        logthis("Notification " + id + "has been read\n");
    }


    /*
     * helper method that the mainactivity can use to update the logger with the reply to notification.
     */
    public void NotificationReply(int id, String message) {
        logthis("Notification " + id + ": reply is " + message + "\n");
    }


    //for when a message has been replied to.
    private BroadcastReceiver mReplyReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceiveReply");
            int conversationId = intent.getIntExtra(CONVERSATION_ID, -1);
            if (conversationId != -1) {
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                if (remoteInput != null) {
                    String replyMessage = remoteInput.getCharSequence(EXTRA_REMOTE_REPLY).toString();
                    Log.d(TAG, "Notification " + conversationId + " reply is " + replyMessage);
                    // Update the notification to stop the progress spinner.
                    NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(context);
                    Notification repliedNotification = new NotificationCompat.Builder(context, id)
                        .setSmallIcon(R.mipmap.notification_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(
                            context.getResources(), R.mipmap.android_contact))
                        .setDeleteIntent(mDeletePendingIntent)  //so we know if they deleted it.
                        .setContentText("Replied")
                        .setChannelId(MainActivity.id)
                        .setOnlyAlertOnce(true)  //don't sound/vibrate/lights again!
                        .build();
                    //I do check in onCreate.  yes, it could be removed between then, but don't care.
                    notificationManager.notify(conversationId, repliedNotification);
                    NotificationReply(conversationId, replyMessage);
                }

            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
          if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              registerReceiver(mDeleteReceiver, new IntentFilter(ACTION_NOTIFICATION_DELETE), Context.RECEIVER_NOT_EXPORTED);
              registerReceiver(mReadReceiver, new IntentFilter(READ_ACTION), Context.RECEIVER_NOT_EXPORTED);
              registerReceiver(mReplyReceiver, new IntentFilter(REPLY_ACTION), Context.RECEIVER_NOT_EXPORTED);
          } else {

              //lint can't see the if statement and is stupid.
              registerReceiver(mDeleteReceiver, new IntentFilter(ACTION_NOTIFICATION_DELETE));
              registerReceiver(mReadReceiver, new IntentFilter(READ_ACTION));
              registerReceiver(mReplyReceiver, new IntentFilter(REPLY_ACTION));
          }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNumberOfNotifications();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mDeleteReceiver);
        unregisterReceiver(mReadReceiver);
        unregisterReceiver(mReplyReceiver);
    }

    /*
     * for API 26+ create notification channels
     */
    private void createchannel() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(id,
            getString(R.string.channel_name),  //name of the channel
            NotificationManager.IMPORTANCE_DEFAULT);   //importance level
        //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
        // Configure the notification channel.
        mChannel.setDescription(getString(R.string.channel_description));
        mChannel.enableLights(true);
        //Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setShowBadge(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        nm.createNotificationChannel(mChannel);
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
        binding.logger.append(msg);
        Log.d(TAG, msg);
    }
}
