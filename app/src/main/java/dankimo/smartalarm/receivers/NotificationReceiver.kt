package dankimo.smartalarm.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dankimo.smartalarm.NOTIFICATION_CHANNEL_ID
import dankimo.smartalarm.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var notificationTappedIntent =
            PendingIntent.getBroadcast(context, 1,
                Intent(context, NotificationTappedReceiver::class.java), 0)

        val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("SmartAlarm")
            .setAutoCancel(true)
            .setContentText("Are you awake? Tap to record a successful wake-up.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(notificationTappedIntent)

            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(0, builder.build())
    }

}