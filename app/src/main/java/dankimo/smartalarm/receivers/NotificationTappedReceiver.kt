package dankimo.smartalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import dankimo.smartalarm.DataBaseHelper
import dankimo.smartalarm.models.NotificationTimeModel
import java.time.LocalDateTime

class NotificationTappedReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmId = intent?.getIntExtra("alarmId", 0)

        if (alarmId != 0) updateAlarmInDB(context, alarmId!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAlarmInDB(context: Context?, alarmId: Int) {
        val timeNow = LocalDateTime.now()

        val timeStopped = NotificationTimeModel(null, timeNow, alarmId)
        val dbh = DataBaseHelper(context)
        dbh.addStoppedTime(timeStopped)
    }
}