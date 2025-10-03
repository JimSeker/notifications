Android Notification Examples
===========
`eclipse/` has the examples for eclipse.  This is no longer updated.  otherwise the examples are of Andriod Studio.

`NotificationDemo` (java) shows how many of the different notifications works with the support notification lib.
It also shows how to use an alarm to send a notification 2 (ish) minutes later.  See comments for more info.
This also has a permission set for the receiver, NotificationDemo2 will call the receiver as well and also has a uses permission for it.

`NotificationDemo_kt` (kotlin) shows how many of the different notifications works with the support notification lib.
It also shows how to use an alarm to send a notification 2 (ish) minutes later.  See comments for more info.
This also has a permission set for the receiver, NotificationDemo2_kt will call the receiver as well and also has a uses permission for it.

`NotificationDemo2` (java) Shows how to use the progress bar (progressbar and "activity" ) notifications.  It also sends a Broadcast to notificationDemo directly and using an alarammanager.  It has a permission needed by notificationDemo in the manifestfile. 

`NotificationDemo2_kt` (kotlin) Shows how to use the progress bar (progressbar and "activity" ) notifications.  It also sends a Broadcast to notificationDemo directly and using an alarammanager.  It has a permission needed by notificationDemo in the manifestfile. 

`notificationDemo3` (java) is an example of how setup remoteInput (ie replies on notifications).  Also how you aplications can know if the notifcations were read and deleted.  Some code is from https://github.com/googlesamples/android-MessagingService and https://github.com/googlesamples/android-ActiveNotifications/   This example breaks at API 34+ now.  Updates will be needed, but no idea how to fix it.   Worse, both links also lead to archived (ie not updated) github repos.


`NotiODemo` (java) is an API 26  demo of badges/numbers for icons.   Check to see if you launcher supports badges; You turn on badging by going to Settings > Apps & notifications > Advanced > Special app access > Notification access and turn notification access on for your launcher {if it's not listed, then no badges).  In Android 12, settings, Notifications, scroll to Notification dot on app icon. 

`NotiODemo_kt` (kotlin) is an API 26  demo of badges/numbers for icons.   Check to see if you launcher supports badges; You turn on badging by going to Settings > Apps & notifications > Advanced > Special app access > Notification access and turn notification access on for your launcher {if it's not listed, then no badges).  In Android 12, settings, Notifications, scroll to Notification dot on app icon. 

---

These are example code for University of Wyoming, Cosc 4730 Mobile Programming course and cosc 4735 Advance Mobile Programing course. 
All examples are for Android.

