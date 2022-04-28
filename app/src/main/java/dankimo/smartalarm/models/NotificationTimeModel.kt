package dankimo.smartalarm.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class NotificationTimeModel(
    Id: Int? = null,
    TimeStopped: LocalDateTime,
    AlarmID : Int
) : AlarmTimeModel (Id, TimeStopped) {
    var alarmId : Int = AlarmID
    var timeStopped : LocalDateTime = TimeStopped

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStopped_toString() : String {
        return formatter.format(timeStopped)
    }
}