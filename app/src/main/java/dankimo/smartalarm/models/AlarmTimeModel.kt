package dankimo.smartalarm.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

val dateStringPattern = "yyyy-MM-dd hh:mm:ss;"

open class AlarmTimeModel(
    Id: Int? = null,
    TimeSet: LocalDateTime,
) {
    val id : Int? = Id
    val timeSet : LocalDateTime = TimeSet

    protected val formatter = SimpleDateFormat(dateStringPattern)

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeSet_toString() : String{
        return formatter.format(timeSet)
    }
}