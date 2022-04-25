package dankimo.smartalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "HI", Toast.LENGTH_LONG)
        val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("SmartAlarm")
            .setAutoCancel(true)
            .setContentText("Are you awake? Tap to record a successful wake-up.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(0, builder.build())
    }

}