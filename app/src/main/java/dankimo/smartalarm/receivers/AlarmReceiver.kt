package dankimo.smartalarm.receivers

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dankimo.smartalarm.ALARM_CHANNEL_ID
import dankimo.smartalarm.R
import dankimo.smartalarm.ringTone

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stopAlarmIntent =
            PendingIntent.getBroadcast(context, 1,
                Intent(context, StopAlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context!!, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("SmartAlarm")
            .setContentText("Tap to turn off alarm.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(stopAlarmIntent)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(0, builder.build())

        playAlarmSound(context)
    }

    fun playAlarmSound(context: Context?) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        ringTone = RingtoneManager.getRingtone(context, alarmSound)
        ringTone!!.play()
    }
}