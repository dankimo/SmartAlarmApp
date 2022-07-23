package dankimo.smartalarm.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val dateStringPattern = "yyyy-MM-dd HH:mm:ss"

open class Alarm (
    id: Int? = null,
    time: LocalDateTime,
) {
    val id : Int? = id
    val time : LocalDateTime = time

    protected val formatter = SimpleDateFormat(dateStringPattern)

    @RequiresApi(Build.VERSION_CODES.O)
    fun time_toString() : String{
        return formatter.format(time)
    }
}