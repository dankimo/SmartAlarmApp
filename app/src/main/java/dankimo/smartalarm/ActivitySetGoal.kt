package dankimo.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dankimo.smartalarm.databinding.ActivitySetGoalBinding

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
        spEditor.putInt("currentMinute", intent.getIntExtra("Current Minute", 0))
        spEditor.putInt("goalHour", binding.timePickerGoal.hour)
        spEditor.putInt("goalMinute", binding.timePickerGoal.hour)

        spEditor.commit()
    }
}