package edu.cs4730.notiodemo_kt

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.Process
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import java.util.Random

/**
 * https://developer.android.com/preview/features/notification-badges.html
 *
 * This will attempt to put numbers instead of dots as badges.  Numbers must be supported by the
 * launcher, which don't appear to be supported on the pixel/pixel2/pixel3(android12) launcher.  But it does mostly work
 * in the emulators.  On the pixel2 and 3, instead of numbers, it's just the dot.  On the 3 a long press shows the numbers though.
 */
class MyNotiService : Service() {
    private lateinit var mServiceLooper: Looper
    private lateinit var mServiceHandler: ServiceHandler
    private val TAG = "myNotiService"

    //my variables
    lateinit var r: Random
    var NotID = 1
    lateinit var nm: NotificationManager

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            //setup how many messages
            nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            var times = 0
            var i: Int = 0
            var messenger: Messenger? = null
            val extras = msg.getData()
            if (extras != null) {
                times = extras.getInt("times", 0)
                messenger = extras["MESSENGER"] as Messenger?
            }

            while (i < times) {
                synchronized(this) {
                    try {
                        (this as Object).wait(5000)
                    } catch (e: InterruptedException) {
                    }
                }
                val info = i.toString() + " random=" + r.nextInt(100)
                Log.d(TAG, info)
                if (messenger != null) {
                    val mymsg = Message.obtain()
                    mymsg.obj = info
                    try {
                        messenger.send(mymsg)
                    } catch (e1: RemoteException) {
                        Log.wtf(TAG, "Exception sending message", e1)
                    }
                } else {
                    //no handler, so use notification
                    makenoti(info, i + 1)
                }
                i++
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        r = Random()
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper()
        mServiceHandler = ServiceHandler(mServiceLooper)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        val msg = mServiceHandler.obtainMessage()
        msg.arg1 = startId //needed for stop.
        msg.data = intent.extras
        mServiceHandler.sendMessage(msg)

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    fun makenoti(message: String?, msgcount: Int) {
        val noti = Notification.Builder(applicationContext, MainActivity.Actions.id)
            .setSmallIcon(R.mipmap.ic_launcher) //.setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
            .setChannelId(MainActivity.Actions.id)
            .setContentTitle("Service") //Title message top row.
            .setContentText(message) //message when looking at the notification, second row
            .setAutoCancel(true) //allow auto cancel when pressed.
            .setNumber(1) //error in emulator?  when it seems to add, not set.  so when I set, 1,2,3,4,5, I get 15, not 5.  with 1, I get 5.
            .build() //finally build and return a Notification.
        //Show the notification
        nm.notify(NotID, noti)
        NotID++
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }
}
