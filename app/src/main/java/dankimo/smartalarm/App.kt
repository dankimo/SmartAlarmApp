package dankimo.smartalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.media.AudioAttributes
import android.media.Ringtone

val NOTIFICATION_CHANNEL_ID = "SmartAlarm Notifications"
val ALARM_CHANNEL_ID = "SmartAlarm"
var ringTone : Ringtone? = null

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create the notification channel for the alarm sounds
            val channelName = "Smart Alarm"

            val alarmNotificationChannel = NotificationChannel(ALARM_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            alarmNotificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), audioAttributes)
            alarmNotificationChannel.description = "Alarm Notifications"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(alarmNotificationChannel)

            // create the channel for the reminder notifications
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Smart Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Text Notifications"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}