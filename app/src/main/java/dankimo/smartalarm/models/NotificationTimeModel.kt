package dankimo.smartalarm.models

import java.time.LocalDateTime

class NotificationTimeModel(id : Int?, time : LocalDateTime, alarmId : Int) : Alarm(id, time) {

}