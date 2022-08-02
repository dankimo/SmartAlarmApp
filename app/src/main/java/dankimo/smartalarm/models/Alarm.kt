package dankimo.smartalarm.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

val dateStringPattern = "yyyy-MM-dd HH:mm:ss"
val dateFormatter : DateTimeFormatter =
    DateTimeFormatterBuilder().appendPattern(dateStringPattern)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
        .parseDefaulting(ChronoField.MICRO_OF_SECOND, 0)
        .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
        .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
        .toFormatter()

open class Alarm (
    id: Int? = null,
    timeSet: LocalDateTime,
    timeStopped: LocalDateTime?
) {
    val Id : Int? = id
    val TimeSet : LocalDateTime = timeSet
    var TimeStopped : LocalDateTime? = timeStopped

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeSet_toString() : String{
        return dateFormatter.format(TimeSet)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStopped_toString() : String{
        return dateFormatter.format(TimeSet)
    }
}