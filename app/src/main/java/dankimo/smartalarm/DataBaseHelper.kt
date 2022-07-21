package dankimo.smartalarm

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import dankimo.smartalarm.models.Alarm
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import dankimo.smartalarm.models.dateStringPattern

val ALARM_TABLE_NAME = "AlarmTimes"
val NOTIFICATION_TABLE_NAME = "NotificationTimes"
val COLUMN_TIMESET = "Time_Set"
val COLUMN_TIMESTOPPED = "Time_Stopped"
val COLUMN_ALARMID = "Alarm_Id"
var DB_HELPER : DatabaseHelper? = null

class DatabaseHelper(
    context: Context?,
    name: String? = "smartalarm.db",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1,
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createAlarmTableStatement = "CREATE TABLE IF NOT EXISTS $ALARM_TABLE_NAME " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TIMESET TEXT," +
                "$COLUMN_TIMESTOPPED TEXT);"

        val createNotificationTableStatement = "CREATE TABLE IF NOT EXISTS $NOTIFICATION_TABLE_NAME " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TIMESTOPPED TEXT," +
                "$COLUMN_ALARMID INTEGER," +
                "FOREIGN KEY ($COLUMN_ALARMID) REFERENCES $ALARM_TABLE_NAME(ID));"

        db?.execSQL(createAlarmTableStatement)
        db?.execSQL(createNotificationTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarmTime(time: Alarm) : Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TIMESET, time.time_toString())

        val insert = db.insert(ALARM_TABLE_NAME, null, cv)
        return insert != Integer.toUnsignedLong(-1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAll(tableName : String) : List<Alarm> {
        var returnList: MutableList<Alarm> = mutableListOf()

        val query = "SELECT * FROM $tableName"
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val alarmID = cursor.getInt(0)
                val timeSet = convertFromStringToDate(cursor.getString(1))

                val Alarm = Alarm(alarmID, timeSet)
                returnList.add(Alarm)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return returnList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addStoppedTime(timeStopped : Alarm) : Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TIMESTOPPED, timeStopped.time_toString())

        val insert = db.insert(NOTIFICATION_TABLE_NAME, null, cv)
        return insert != Integer.toUnsignedLong(-1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCollection(alarms: List<Alarm>) {
        alarms.forEach { alarm ->
            addAlarmTime(alarm)
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun convertFromStringToDate(date : String) : LocalDateTime {
            val dateFormatter = DateTimeFormatter.ofPattern(dateStringPattern)
            return LocalDateTime.parse(date, dateFormatter)
        }
    }
}