package dankimo.smartalarm

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

val dateStringPattern = "yyyy-MM-dd hh:mm:ss;"

class AlarmTimeModel(
    Id: Int,
    TimeSet: LocalDateTime,
    TimeStopped: LocalDateTime,
){
    val id : Int = Id
    val timeSet : LocalDateTime = TimeSet
    val timeStopped : LocalDateTime = TimeStopped

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