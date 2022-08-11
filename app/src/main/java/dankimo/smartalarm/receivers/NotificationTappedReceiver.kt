package dankimo.smartalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ContextUtils.getActivity
import dankimo.smartalarm.MainActivity
import dankimo.smartalarm.MainActivity.Companion.goalHour
import dankimo.smartalarm.MainActivity.Companion.goalMinute
import dankimo.smartalarm.SHARED_PREFS
import dankimo.smartalarm.controllers.DB
import dankimo.smartalarm.models.Alarm
import java.time.LocalDateTime

class NotificationTappedReceiver : BroadcastReceiver() {
    private lateinit var context: Context;
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context!!;

        val alarm = DB?.getLatestTimeSet()
        alarm!!.TimeStopped = LocalDateTime.now()

        DB?.addStoppedTime(alarm)
        setNextAlarm(alarm, context);
    }

    // if the stopped time is within 30 mins of setting the alarm,
    // set a new alarm for tomorrow that is 5 minutes earlier
    private fun setNextAlarm(alarm: Alarm, context: Context) {
        // this will only work within the same hour
        if (alarm.TimeStopped!!.hour == alarm.TimeSet.hour) {
            if (alarm.TimeStopped!!.minute - alarm.TimeSet.minute <= 30)
            {
                var newMinute = alarm.TimeSet.minusMinutes(5).minute;
                if (newMinute < goalMinute && alarm.TimeStopped!!.hour == goalHour) {
                    newMinute = goalMinute;
                    val diff = alarm.TimeSet.minute - goalMinute
                    DB?.addAlarm(Alarm(null, alarm.TimeSet.minusMinutes(diff.toLong()), null))
                }
                else {
                    DB?.addAlarm(Alarm(null, alarm.TimeSet.minusMinutes(5), null))
                }
                context.sendBroadcast( Intent("UPDATE_ALARM"))
            }
            else {
                Toast.makeText(context,"Wakeup > 30 minutes after alarm. Set for same time.", Toast.LENGTH_LONG,)
            }
        }
    }
}