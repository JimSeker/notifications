Android Notification Examples
===========
`eclipse/` has the examples for eclipse.  This is no longer updated.  otherwise the examples are of Andriod Studio.

`NotificationDemo` shows how many of the different notifications works with the support notification lib.
There is also have a Broadcast receiver, which NotificationDemo2 needs.  There is also example of how to set a notification for the "future" using an alarm manager.

`NotificationDemo2` uses notificationDemo with the alarammanager to setup a notification to be sent later on (via the broadcast receiver in notificationdemo)

`notificationDemo3` (API 24+) is an example of how setup remoteInput (ie replies on notifications).  Also how you aplications can know if the notifcations were read and deleted.  Some code is from https://github.com/googlesamples/android-MessagingService and https://github.com/googlesamples/android-ActiveNotifications/ 

`NotiODemo` is an API 26  demo of badges/numbers for icons.   Check to see if you launcher supports badges; You turn on badging by going to Settings > Apps & notifications > Advanced > Special app access > Notification access and turn notification access on for your launcher {if it's not listed, then no badges).  In Android 12, settings, Notifications, scroll to Notification dot on app icon. 

---

These are example code for University of Wyoming, Cosc 4730 Mobile Programming course and cosc 4735 Advance Mobile Programing course. 
All examples are for Android.

