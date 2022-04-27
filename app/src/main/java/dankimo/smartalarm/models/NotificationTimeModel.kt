package dankimo.smartalarm.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class NotificationTimeModel(
    Id: Int? = null,
    TimeStopped: LocalDateTime? = null,
){
    val id : Int? = Id
    val timeStopped : LocalDateTime? = TimeStopped

    private val formatter = SimpleDateFormat(dateStringPattern)

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStopped_toString() : String {
        return formatter.format(timeStopped)
    }
}