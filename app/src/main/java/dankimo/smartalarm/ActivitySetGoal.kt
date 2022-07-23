package dankimo.smartalarm

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import dankimo.smartalarm.databinding.ActivitySetGoalBinding
import dankimo.smartalarm.models.Alarm
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class ActivitySetGoal : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonGoalNext.setOnClickListener { buttonNextClicked() }
    }

    private fun buttonNextClicked() {
        val setMainIntent = Intent(this, MainActivity::class.java)
        setMainIntent.putExtra("setNewAlarm", true)
        saveData()
        startActivity(setMainIntent)
    }

    private fun saveData() {
        val sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val spEditor = sp.edit()

        spEditor.putInt("currentHour", intent.getIntExtra("Current Hour", 0))
        spEditor.putInt("currentHour", intent.getIntExtra("Current Minute", 0))
        spEditor.putInt("goalHour", binding.timePickerGoal.hour)
        spEditor.putInt("goalMinute", binding.timePickerGoal.minute)

        spEditor.commit()

        val currentTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, intent.getIntExtra("Current Hour", 0))
            set(Calendar.MINUTE, intent.getIntExtra("Current Minute", 0))
        }

        val currentTimeAlarm = Alarm(
            null, LocalDateTime.ofInstant(currentTime.toInstant(), currentTime.timeZone.toZoneId()))

        DB_HELPER?.addStoppedTime(currentTimeAlarm)
        DB_HELPER?.addAlarmTime(currentTimeAlarm)
    }
}