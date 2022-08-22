package dankimo.smartalarm.models

import android.os.Build
import androidx.annotation.RequiresApi
import dankimo.smartalarm.controllers.DataBaseController.Companion.dateFormatter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

open class Alarm (
    id: Int? = null,
    timeSet: LocalDateTime,
    timeStopped: LocalDateTime?
) {
    val Id : Int? = id
    val TimeSet : LocalDateTime = timeSet
    var TimeStopped : LocalDateTime? = timeStopped

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeSet_toString() : String {
        return dateFormatter.format(TimeSet)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStopped_toString() : String {
        if (TimeStopped == null) return "";
        return dateFormatter.format(TimeStopped)
    }
}

