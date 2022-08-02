package dankimo.smartalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import dankimo.smartalarm.DB_HELPER
import java.time.LocalDateTime

class NotificationTappedReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmId = intent?.getIntExtra("AlarmId", 0)
        alarmId ?: return

        val alarm = DB_HELPER?.getAlarm(alarmId)
        alarm!!.TimeStopped = LocalDateTime.now()

        if (alarmId != 0) DB_HELPER?.addStoppedTime(alarm)
    }
}