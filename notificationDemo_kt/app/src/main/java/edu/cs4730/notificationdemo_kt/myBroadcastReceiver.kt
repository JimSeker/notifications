package edu.cs4730.notificationdemo_kt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * A broadcast receiver doesn't have a screen.
 * We could easy use this instead of DisplayNotification activity, because we don't
 * need a screen.
 */
class myBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var info = "no bundle"
        Toast.makeText(context, "received something (kt)", Toast.LENGTH_SHORT).show()
        Log.i("myBroadcastReceiver", "received something")
        if (intent.action == MainActivity.Actions.ACTION) {  //is it our action?
            val extras = intent.extras
            if (extras != null) {
                info = extras.getString("mytype").toString()
                if (info == null) {
                    info = "nothing"
                }
            }
            Toast.makeText(context, "intent has KT: $info", Toast.LENGTH_SHORT).show()
            Log.i("myBroadcastReceiver", "intent has: $info")
        }
    }


}
