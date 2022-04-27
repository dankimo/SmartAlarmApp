package dankimo.smartalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

class NotificationTappedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        updateAlarmInDB()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAlarmInDB() {
        val timeNow = LocalDateTime.now()


    }
}