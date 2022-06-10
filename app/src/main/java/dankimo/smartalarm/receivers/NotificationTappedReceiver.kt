package dankimo.smartalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import dankimo.smartalarm.DatabaseHelper
import dankimo.smartalarm.models.NotificationTimeModel
import java.time.LocalDateTime

class NotificationTappedReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "RECEIVED", Toast.LENGTH_LONG);
        val alarmId = intent?.getIntExtra("AlarmId", 0)

        Toast.makeText(context, alarmId!!.toString(), Toast.LENGTH_LONG).show()

        if (alarmId != 0) updateAlarmInDB(context, alarmId!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAlarmInDB(context: Context?, alarmId: Int) {
        val timeNow = LocalDateTime.now()

        val timeStopped = NotificationTimeModel(null, timeNow, alarmId)
        val dbh = DatabaseHelper(context)
        dbh.addStoppedTime(timeStopped)
    }
}