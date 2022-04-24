package dankimo.smartalarm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.Intent.getIntent

import android.os.Bundle




class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var stopAlarmIntent =
            PendingIntent.getBroadcast(context, 1,
                Intent(context, StopAlarmReceiver::class.java), 0)

        val builder = NotificationCompat.Builder(context!!, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("SmartAlarm")
            .setAutoCancel(true)
            .setContentText("Tap to turn off alarm.")
            .setVibrate(arrayOf<Long>(0, 500, 1000).toLongArray())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(stopAlarmIntent)

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        }

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(0, builder.build())

        playAlarmSound(context)
    }

    fun playAlarmSound(context: Context?) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringTone = RingtoneManager.getRingtone(context, alarmSound);
        ringTone!!.play();
    }
}