package com.example.sct_ad_03


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import com.example.sct_ad_03.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var timerTextView: TextView? = null
    private var startPauseButton: Button? = null
    private var resetButton: Button? = null

    private var isRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private var lastPauseTime = 0L

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        setupClickListeners()
        initializeTimer()
    }

    private fun initializeViews() {
        timerTextView = binding.timerTextView
        startPauseButton = binding.startPauseButton
        resetButton = binding.resetButton
    }

    private fun setupClickListeners() {
        startPauseButton?.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        resetButton?.setOnClickListener {
            resetTimer()
        }
    }

    private fun initializeTimer() {
        runnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    updateTimerDisplay()
                    handler.postDelayed(this, 10) // Update every 10ms for milliseconds
                }
            }
        }
        updateTimerDisplay()
    }

    private fun startTimer() {
        if (!isRunning) {
            if (elapsedTime == 0L) {
                // Starting fresh
                startTime = System.currentTimeMillis()
            } else {
                // Resuming from pause
                startTime = System.currentTimeMillis() - elapsedTime
            }
            isRunning = true
            startPauseButton?.text = getString(R.string.pause)
            handler.postDelayed(runnable, 10)
        }
    }

    private fun pauseTimer() {
        if (isRunning) {
            isRunning = false
            lastPauseTime = System.currentTimeMillis()
            elapsedTime = lastPauseTime - startTime
            startPauseButton?.text = getString(R.string.start)
            handler.removeCallbacks(runnable)
        }
    }

    private fun resetTimer() {
        isRunning = false
        startTime = 0L
        elapsedTime = 0L
        lastPauseTime = 0L
        startPauseButton?.text = getString(R.string.start)
        handler.removeCallbacks(runnable)
        updateTimerDisplay()
    }

    private fun updateTimerDisplay() {
        val currentTime = if (isRunning) {
            System.currentTimeMillis() - startTime
        } else {
            elapsedTime
        }

        val minutes = (currentTime / 60000) % 60
        val seconds = (currentTime / 1000) % 60
        val milliseconds = (currentTime % 1000) / 10

        timerTextView?.text = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
    }

    override fun onPause() {
        super.onPause()
        if (isRunning) {
            pauseTimer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}