package dankimo.smartalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dankimo.smartalarm.ringTone

class StopAlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        stopAlarmSound(context)
    }

    companion object {
        fun stopAlarmSound(context: Context?) {
            if (ringTone != null) ringTone!!.stop()
        }
    }
}