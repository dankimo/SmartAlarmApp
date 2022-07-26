package dankimo.smartalarm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dankimo.smartalarm.databinding.ActivitySetInitialBinding

class ActivitySetInitial : AppCompatActivity() {

    private lateinit var binding: ActivitySetInitialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonInitalNext.setOnClickListener { buttonNextClicked() }
    }

    private fun buttonNextClicked() {
        val setGoalIntent = Intent(this, ActivitySetGoal::class.java)
        setGoalIntent.putExtra("Current Hour", binding.timePickerInitial.hour)
        setGoalIntent.putExtra("Current Minute", binding.timePickerInitial.minute)
        startActivity(setGoalIntent)
    }
}