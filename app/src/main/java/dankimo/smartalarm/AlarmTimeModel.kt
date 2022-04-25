package dankimo.smartalarm

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

val dateStringPattern = "yyyy-MM-dd hh:mm:ss;"

class AlarmTimeModel(
    Id: Int,
    TimeSet: Calendar,
    TimeStopped: Calendar,
    AlarmType : String
){
    val id : Int = Id
    val timeSet : Calendar = TimeSet
    val timeStopped : Calendar = TimeStopped
    val alarmType : String = AlarmType

    private val formatter = SimpleDateFormat(dateStringPattern);

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeSet_toString() : String{
        return formatter.format(timeSet)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStopped_toString() : String {
        return formatter.format(timeStopped)
    }
}